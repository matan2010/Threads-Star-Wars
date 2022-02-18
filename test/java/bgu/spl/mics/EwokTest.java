package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Ewok;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EwokTest {

    private Ewok ewok;
    @BeforeEach
    public void setUp(){

        ewok = new Ewok(1);
    }

    @Test
    //Tests if acquire() changes the flag from true to false.
    public void testAcquire(){

        ewok.acquire();
        assertTrue(!ewok.isAvailable());
    }

    @Test
    //Tests if acquire() changes the flag from true to false.
    public void testRelease(){
        ewok.acquire();
        ewok.release();
        assertTrue(ewok.isAvailable());
    }

    @Test
    public void testSerialNumber(){
        Integer ewokSerialNumber = ewok.getSerialNumber();
        assertFalse(ewokSerialNumber==null);
        assertTrue(ewokSerialNumber instanceof Integer);
        assertFalse(ewokSerialNumber<0);
    }





}
