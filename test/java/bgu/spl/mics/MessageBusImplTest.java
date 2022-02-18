package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Ewok;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import bgu.spl.mics.application.services.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessageBusImplTest {
    private MessageBusImpl myMesBus;

    @BeforeEach
    public void setUp() throws Exception {
        this.myMesBus = MessageBusImpl.getInstance();
    }

    @Test
    //tests if the MS was subscribed for the Event
    //methods that have been tested: subscribeEvent, register, sendEvent.
    public void testSubscribeEvent() throws InterruptedException {
        MicroService a = new C3POMicroservice();
        myMesBus.register(a);
        MicroService b = new HanSoloMicroservice();
        myMesBus.register(b);
        myMesBus.subscribeEvent(AttackEvent.class, b);
        //adding attack instance as parameter for attack event
        Integer[] integers = {1,2,3,4};
        List<Integer> serialNumber = Arrays.asList(integers);
        int duration = 100;
        Attack attack = new Attack(serialNumber, duration);

        AttackEvent c = new AttackEvent(attack);
        Message myMes = myMesBus.awaitMessage(a);
        myMesBus.sendEvent(c);
        assertEquals(myMes, c);
    }


    @Test
    //tests if the MS
    //methods that have been tested: subscribeBroadcast, register, sendBroadcast
    public void testSubscribeBroadcast() throws InterruptedException {
        MicroService a = new C3POMicroservice();
        myMesBus.register(a);
        MicroService b = new HanSoloMicroservice();
        myMesBus.register(b);
        myMesBus.subscribeBroadcast(myBroadcast.class, b);
        myBroadcast c = new myBroadcast();
        Message myMes = myMesBus.awaitMessage(a);
        myMesBus.sendBroadcast(c);
        assertTrue(myMes.equals(c));
    }

    @Test
    //tests if the MB was notified that the event was handeled and got the right result
    //methods that have been tested: subscribeEvent, complete.
    public void testComplete() {
        boolean RIGHT_RESULT = true;
        MicroService hanSolo = new HanSoloMicroservice();
        //adding attack instance as parameter for attack event
        Integer[] integers = {1,2,3,4};
        List<Integer> serialNumber = Arrays.asList(integers);
        int duration = 100;
        Attack attack = new Attack(serialNumber, duration);

        Event attackEvent = new AttackEvent(attack);
        myMesBus.subscribeEvent(AttackEvent.class, hanSolo);
        Future<String> future = hanSolo.sendEvent(attackEvent);
        boolean result = RIGHT_RESULT;
        hanSolo.complete(attackEvent, result);
        assertTrue(future.isDone());
        assertTrue(future.get().equals(RIGHT_RESULT));
    }

    @Test
    // //methods that have been tested: awaitMessage, sendEvent, register, subscribeEvent
    public void testAwaitMessage() throws InterruptedException {
        MicroService hanSolo = new HanSoloMicroservice();
        //adding attack instance as parameter for attack event
        Integer[] integers = {1,2,3,4};
        List<Integer> serialNumber = Arrays.asList(integers);
        int duration = 100;
        Attack attack = new Attack(serialNumber, duration);

        Event attackEvent = new AttackEvent(attack);
        myMesBus.register(hanSolo);
        myMesBus.subscribeEvent(AttackEvent.class, hanSolo);
        myMesBus.sendEvent(attackEvent);

        Message message= null;
        message = myMesBus.awaitMessage(hanSolo);
        assertTrue(message!=null);
    }
}