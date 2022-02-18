package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.concurrent.TimeUnit;


import static org.junit.jupiter.api.Assertions.*;


public class FutureTest {

    private Future<String> future;

    @BeforeEach
    public void setUp(){
        future = new Future<>();
    }

    @Test
    public void testResolve(){
        String str = "someResult";
        future.resolve(str);
        assertTrue(future.isDone());
        assertTrue(str.equals(future.get()));
    }

    @Test
    public void testGet() {
        assertTrue(true);
        String str= "someResult";
        future.resolve(str);
        assertEquals(str,future.get());
    }

    @Test
    public void testIsDone() {
        assertFalse(future.isDone());
        String str= "someResult";
        future.resolve(str);
        assertTrue(future.isDone());
    }

    @Test
    public void testGet2() {
        assertTrue(true);
        String str= "someResult";
        future.resolve(str);
        assertEquals(str,future.get(1, TimeUnit.SECONDS));
    }
}
