package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.Semaphore;


/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
//synchronized using Semaphore for each Ewok object.
public class Ewok {


    private int serialNumber;
    private boolean available;
    private Semaphore lockForEwok;


    public Ewok(int serialNumber) {
        this.serialNumber = serialNumber;
        this.available = true;
        this.lockForEwok = new Semaphore(1, false); //only one MS can use specified ewok at a time.
        // starvation of MS is OK.
    }

    /**
     * Acquires an Ewok
     */
    public void acquire() {
        try {
            lockForEwok.acquire();            //notice: acquire for semaphore lock
        } catch (Exception exception){}       //wait until getting the key
        available = false;
    }



    /**
     * release an Ewok
     */
    public void release() {
        available = true;
        lockForEwok.release();               //notice: release for semaphore lock
    }

    public boolean isAvailable() {
        return available;
    }

    public final int getSerialNumber() {
        return serialNumber;
    }

}
