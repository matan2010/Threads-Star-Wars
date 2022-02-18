package bgu.spl.mics;

import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
//when checking a term on a synchronized data structure, check twice if condition

public class MessageBusImpl implements MessageBus {

    private static MessageBusImpl instance = null;

    private ConcurrentHashMap<MicroService, BlockingQueue<Message>> myMicroHashMap;
    private ConcurrentHashMap<Class<? extends Broadcast>, BlockingQueue<MicroService>> myBroadcastHashMap;
    private ConcurrentHashMap<Class<? extends Event>, BlockingQueue<MicroService>> myEventHashMap;
    private ConcurrentHashMap<Event<?>, Future> myFutureHashMap;

    private static class SingletonMessageBus {
        private static final MessageBusImpl instance = new MessageBusImpl();
    }

    private MessageBusImpl() {
        myMicroHashMap = new ConcurrentHashMap<>();
        myBroadcastHashMap = new ConcurrentHashMap<>();
        myEventHashMap = new ConcurrentHashMap<>();
        myFutureHashMap = new ConcurrentHashMap<>();
    }

    public static MessageBusImpl getInstance() {
        return SingletonMessageBus.instance;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        //if EventType is not in myEvent hashmap put a blank blocking queue.
        myEventHashMap.putIfAbsent(type, new LinkedBlockingQueue<>());

        if (!myEventHashMap.get(type).contains(m)) {
            synchronized (myEventHashMap.get(type)) {
                if (!myEventHashMap.get(type).contains(m)) {
                    BlockingQueue<MicroService> microServicesQueue = myEventHashMap.get(type);
                    microServicesQueue.add(m);
                }
            }
            synchronized (m.subscribedEventList) {
                m.subscribedEventList.add(type);
            }
        }

    }

    @Override
    //TODO: (ME) matan need to copy
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        //if EventType is not in myEvent hashmap put a blank blocking queue.
        myBroadcastHashMap.putIfAbsent(type, new LinkedBlockingQueue<>());
        if (!myBroadcastHashMap.get(type).contains(m)) {
            synchronized (myBroadcastHashMap.get(type)) {
                if (!myBroadcastHashMap.get(type).contains(m)) {
                    BlockingQueue microServicesQueue = myBroadcastHashMap.get(type);
                    microServicesQueue.add(m);
                }
            }
            synchronized (m.subscribedBroadcastList) {
                m.subscribedBroadcastList.add(type);
            }
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void complete(Event<T> e, T result) {
        myFutureHashMap.get(e).resolve(result);
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        //if there is no MS that registered to this broadcast - break
        //seperated in order to not block myBroadcastHashMap for long time.
        if (!myBroadcastHashMap.containsKey(b.getClass())) {
            synchronized (myBroadcastHashMap) {
                if (!myBroadcastHashMap.containsKey(b.getClass())) {
                }
            }
        }
        //there is a MS that registered to this broadcast, send him the broadcast.
        //lock only the specific MS queue (and not the whole hashmap).
        else {
            BlockingQueue<MicroService> microServiceBlockingQueue = myBroadcastHashMap.get(b.getClass());
            for (MicroService ms : microServiceBlockingQueue) {
                synchronized (myMicroHashMap.get(ms)) {
                    myMicroHashMap.get(ms).add(b);             //add event to MS's queue.
                    myMicroHashMap.get(ms).notifyAll();        //notify awaiting message waters with an empty queue
                }
            }
        }

    }

    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        if (e == null)
            throw new IllegalArgumentException("event can't be null");

        Future<T> output = new Future<>();
        //if there is no MS that registered to this event - return null
        //seperated in order to not block myEventHashMap for long time.
        if (!myEventHashMap.containsKey(e.getClass())) {
            synchronized (myEventHashMap) {                 //lock all myEventHashMap
                if (!myEventHashMap.containsKey(e.getClass())) {
                    return null;
                }
            }
        }
        //there is a MS that registered to this event, send him the broadcast, in round robbin manner.
        //lock only the specific MS queue (and not the whole hashmap).
        synchronized (myFutureHashMap) {       //save place for this event's future.
            myFutureHashMap.put(e, output);
        }
        if (myEventHashMap.containsKey(e.getClass()) && (myEventHashMap.get(e.getClass())).size() > 0) {
            synchronized (myEventHashMap.get(e.getClass())) {      //synchronize the queue of subscribed MS
                if (myEventHashMap.containsKey(e.getClass()) && (myEventHashMap.get(e.getClass())).size() > 0) {
                    BlockingQueue<MicroService> microServiceQueue = myEventHashMap.get(e.getClass());
                    MicroService ms = microServiceQueue.poll();                    //poll from ms queue
                    try {
                        microServiceQueue.add(ms);                                // get in right to the back of the queue.
                    } catch (Exception nothing) {
                    }
                    synchronized (myMicroHashMap.get(ms)) {
                        myMicroHashMap.get(ms).add(e);                          //add event to MS's queue.
                        myMicroHashMap.get(ms).notifyAll();                     //notify awaiting message waters with an empty queue

                    }
                }
            }
            return output;
        }
        return null;
    }

    @Override
    public void register(MicroService m) {
        //if myMicroHashMap contains m, so there is no need for it to register again
        if (myMicroHashMap.containsKey(m))
            return;
        //else, we'll locate there an empty Message queue.
        else{
            BlockingQueue<Message> messageLinkedBlockingQueue = new LinkedBlockingQueue<>();
            myMicroHashMap.put(m, messageLinkedBlockingQueue);
        }
    }

    @Override
    public void unregister(MicroService m) {
        //first check if m is ever registered, and still have a queue.
        if (myMicroHashMap.containsKey(m)) {
            //remove its queue from the myMicroHashMap
            myMicroHashMap.remove(m);
            //remove m from Events that it is subscribed to them
            synchronized (m.subscribedEventList) {
                for (Class<? extends Message> typeEvent : m.subscribedEventList) {
                    myEventHashMap.get(typeEvent).remove(m);
                }
                //clean m.subscribedEventList for optional reuse
                m.subscribedEventList.clear();
            }
            //remove m from Broadcasts that it is subscribed to them
            synchronized (m.subscribedBroadcastList) {
                for (Class<? extends Message> broadcastType : m.subscribedBroadcastList) {
                    myBroadcastHashMap.get(broadcastType).remove(m);
                }
                //clean m.subscribedBroadCastList for optional reuse
                m.subscribedBroadcastList.clear();
            }
        }
    }

    @Override
    //TODO: have to make it a blocking operation
    public Message awaitMessage(MicroService m) throws InterruptedException {         //synchronized
//        if (!myMicroHashMap.contains(m))
//            throw new NullPointerException("No message queue exsits for this Micro Service");
        Message message;
        if (myMicroHashMap.get(m).size() == 0) {  //if there are no messages in microservice queue
            synchronized (myMicroHashMap.get(m)) {
                while (myMicroHashMap.get(m).size() == 0) {
                    myMicroHashMap.get(m).wait();       //wait until SendEvent or SendBroadcast will wake you up.
                }
            }
        }
        //retrieve a message from the message queue
        message = myMicroHashMap.get(m).poll();
        return message;
    }

}
