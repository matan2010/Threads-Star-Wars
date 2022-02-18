package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.messages.AttackEvent;

import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.Collections;
import java.util.List;

/**
 * // * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * // * This class may not hold references for objects which it is not responsible for:
 * // * {@link AttackEvent}.
 * // *
 * // * You can add private fields and public methods to this class.
 * // * You MAY change constructor signatures and even add new public constructors.
 * //
 */
public class HanSoloMicroservice<T> extends MicroService {

    public HanSoloMicroservice() {
        super("Han");

    }

    @Override
    protected void initialize() {
        //subscribe Termination Broadcast
        subscribeBroadcast(TerminationBroadcast.class, (TerminationBroadcast terminationBroadcast) -> {
            terminate();
            documentTermination();
        });
        //subscribe attackEvent
        subscribeEvent(AttackEvent.class, (AttackEvent attackEvent) -> {
            //set attack parameters
            List<Integer> attackSerialNumbers = attackEvent.getMyAttack().getSerials();
            Ewoks ewoks = Ewoks.getInstance();
            Collections.sort(attackSerialNumbers);                  //sorting this list will help to prevent deadlocks.

            //acquiring resources
            ewoks.acquire(attackSerialNumbers);

            //after resources acquired - time to attack and documentation
            long attackDuration = attackEvent.getAttackDuration();
            try {
                Thread.sleep(attackDuration);
            } catch (InterruptedException neverMind) {}
            complete(attackEvent, true);
            documentFinish();
            diary.incrementTotalAttacksBy1();

            //attack ended, now release all ewoks to reuse.
            ewoks.release(attackSerialNumbers);
        });
    }

    protected void documentFinish() {
        diary.setHanSoloFinish(System.currentTimeMillis());
    }

    protected void documentTermination() {
        diary.setHanSoloTerminate(System.currentTimeMillis());
    }


}
