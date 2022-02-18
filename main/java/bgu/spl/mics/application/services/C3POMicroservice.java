package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.Collections;

import java.util.List;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {

    public C3POMicroservice() {
        super("C3PO");
    }

    @Override
    protected void initialize() {
        //subscribe Termination
        subscribeBroadcast(TerminationBroadcast.class, (TerminationBroadcast terminationBroadcast)->{
            terminate();
            documentTermination();
        });

        //subscribe attackEvent
        subscribeEvent(AttackEvent.class, (AttackEvent attackEvent) -> {
            //set attack parameters
            long attackDuration = attackEvent.getAttackDuration();
            Ewoks ewoks = Ewoks.getInstance();
            List<Integer> attackSerialNumbers = attackEvent.getMyAttack().getSerials();
            Collections.sort(attackSerialNumbers);                  //sorting this list will help to prevent deadlocks.

            //acquiring resources
            ewoks.acquire(attackSerialNumbers);

            //after resources acquired - time to attack and documentation
            try {
                Thread.sleep(attackDuration);
            } catch (InterruptedException e) {}
            documentFinish();
            diary.incrementTotalAttacksBy1();

            //attack ended, now release all ewoks to reuse.
            ewoks.release(attackSerialNumbers);
            complete(attackEvent, true);
        });
    }

    protected void documentFinish() {diary.setC3POFinish(System.currentTimeMillis());}
    protected void documentTermination() {diary.setC3POTerminate(System.currentTimeMillis());}


}
