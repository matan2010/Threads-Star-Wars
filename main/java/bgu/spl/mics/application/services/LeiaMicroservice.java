package bgu.spl.mics.application.services;

import java.util.*;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;


/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
    private Attack[] attacks;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
        this.attacks = attacks;
    }

    @Override
    protected void initialize() {
        //subscribe Termination Broadcast.
        subscribeBroadcast(TerminationBroadcast.class, (TerminationBroadcast terminationBroadcast) -> {
            //terminate and document.
            terminate();
            documentTermination();
        });

        //sleep while other subscribers being registered.
        try {
            Thread.sleep(100);
        } catch (Exception nothing) {
        }

//        HashMap<Integer, Future<Boolean>> futureMap = new HashMap<>();
        List<Future> leiaFutureList = new LinkedList<>();

        //send all Attacks and locate their futures in the Leia's future list.
        for (int i = 0; i < attacks.length; i++) {
            Future<Boolean> future;
            future = sendEvent(new AttackEvent(attacks[i]));
            leiaFutureList.add(future);
        }

        //MICROSERVICES ATTACK

        // make sure evey future is done. if not, wait (Embodied in get)
        for (Future future: leiaFutureList) {
            future.get();
        }
        //send deactivation event to R2D2
        Future <Boolean> future = sendEvent(new DeactivationEvent());
        future.get();

        //send bomb destroyer event to Lando
        sendEvent(new BombDestroyerEvent());
    }

    private void documentTermination() {
        this.diary.setLeiaTerminate(System.currentTimeMillis());
    }
}
