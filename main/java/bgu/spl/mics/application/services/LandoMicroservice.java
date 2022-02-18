package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;


/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice extends MicroService {
    private long duration;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        //subscribe Termination Broadcast
        subscribeBroadcast(TerminationBroadcast.class, (TerminationBroadcast terminationBroadcast) -> {
            terminate();
            documentTermination();
        });

        //subscribe Bomb Event
        subscribeEvent(BombDestroyerEvent.class, (BombDestroyerEvent bombDestroyerEvent) -> {
            try {
                Thread.sleep(duration);
            } catch (Exception nothing) {
            }
            complete(bombDestroyerEvent, true);
            //terminate everyone after bombing
            sendBroadcast(new TerminationBroadcast());
        });

    }

    private void documentTermination() {
        diary.setLandoTerminate(System.currentTimeMillis());
    }
}
