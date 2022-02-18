package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */

public class Diary {

    private static class diaryInstance {
        private static Diary diaryInstance = new Diary();
    }

    private AtomicInteger totalAttacks;
    private long hanSoloFinish;
    private long c3POFinish;
    private long r2D2Deactivate;
    private long leiaTerminate;
    private long hanSoloTerminate;
    private long c3POTerminate;
    private long r2D2Terminate;
    private long landoTerminate;

    private Diary() {
        totalAttacks = new AtomicInteger(0);
        hanSoloFinish = 0;
        c3POFinish = 0;
        r2D2Deactivate = 0;
        leiaTerminate = 0;
        hanSoloTerminate = 0;
        c3POTerminate = 0;
        r2D2Terminate = 0;
        landoTerminate = 0;
    }

    public static Diary getInstance() {
        return diaryInstance.diaryInstance;
    }

    public AtomicInteger getTotalAttacks() {
        return getInstance().totalAttacks;
    }

    // todo: remove this proxy function
    public AtomicInteger getNumberOfAttacks() {
        return getTotalAttacks();
    }

    public long getHanSoloFinish() {
        return hanSoloFinish;
    }

    public long getC3POFinish() {
        return c3POFinish;
    }

    public long getR2D2Deactivate() {
        return r2D2Deactivate;
    }

    public long getLeiaTerminate() {
        return leiaTerminate;
    }

    public long getHanSoloTerminate() {
        return hanSoloTerminate;
    }

    public long getC3POTerminate() {
        return c3POTerminate;
    }

    public long getR2D2Terminate() {
        return r2D2Terminate;
    }

    public long getLandoTerminate() {
        return landoTerminate;
    }

    public void incrementTotalAttacksBy1() {
        totalAttacks.getAndIncrement();
    }

    public void setTotalAttacks(AtomicInteger totalAttacks) {
        this.totalAttacks = totalAttacks;
    }

    public void setHanSoloFinish(long hanSoloFinish) {
        this.hanSoloFinish = hanSoloFinish;
    }

    public void setC3POFinish(long c3POFinish) {
        this.c3POFinish = c3POFinish;
    }

    public void setR2D2Deactivate(long r2D2Deactivate) {
        this.r2D2Deactivate = r2D2Deactivate;
    }

    public void setLeiaTerminate(long leiaTerminate) {
        this.leiaTerminate = leiaTerminate;
    }

    public void setHanSoloTerminate(long hanSoloTerminate) {
        this.hanSoloTerminate = hanSoloTerminate;
    }

    public void setC3POTerminate(long c3POTerminate) {
        this.c3POTerminate = c3POTerminate;
    }

    public void setR2D2Terminate(long r2D2Terminate) {
        this.r2D2Terminate = r2D2Terminate;
    }

    public void setLandoTerminate(long landoTerminate) {
        this.landoTerminate = landoTerminate;
    }
}
