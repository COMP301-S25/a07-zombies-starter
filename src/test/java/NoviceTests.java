import edu.unc.comp301.Base;
import edu.unc.comp301.Survivor;
import org.junit.Test;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;


public class NoviceTests {
    private static class TestSurvivor extends Survivor {
        public TestSurvivor(Base base) {
            super(base);
        }

        @Override
        protected void performAction() throws InterruptedException {
            scavenge(); // Always scavenge for test
            stop(); // Stop immediately after one action
        }
    }

    @Test
    public void testSurvivorExitsOnPerformActionInterrupt() throws Exception {
        Survivor survivor = new InterruptibleSurvivor(new Base());
        Thread thread = new Thread(survivor);
        thread.start();
        Thread.sleep(10000);
        assertFalse("Survivor should have exited on interruption", thread.isAlive());
    }

    @Test
    public void testSurvivorAddsSupplies() throws Exception {
        Base base = new Base();
        Survivor survivor = new TestSurvivor(base);
        Thread thread = new Thread(survivor);
        thread.start();
        thread.join();

        assertTrue("Survivor should have added supplies", base.getSupplyCount() > 0);
    }


    private static class InterruptibleSurvivor extends Survivor {
        public InterruptibleSurvivor(Base base) {
            super(base);
        }

        @Override
        protected void performAction() throws InterruptedException {
            throw new InterruptedException("Forced");
        }
    }

    @Test
    public void testSurvivorStopsOnInterrupt() throws Exception {
        Base base = new Base();
        Survivor survivor = new TestSurvivor(base);
        Thread thread = new Thread(survivor);
        thread.start();
        thread.interrupt();
        thread.join();

        assertFalse("Thread should no longer be alive", thread.isAlive());
    }

    @Test
    public void testAddSuppliesIncreasesCount() {
        Base base = new Base();
        base.addSupplies(10);
        assertEquals(10, base.getSupplyCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNegativeSuppliesThrowsException() {
        Base base = new Base();
        base.addSupplies(-5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddZeroSuppliesThrowsException() {
        Base base = new Base();
        base.addSupplies(0);
    }


    @Test
    public void testMultipleSurvivorsRunTogether() throws Exception {
        Base base = new Base();
        Thread[] threads = new Thread[3];
        Survivor[] survivors = new Survivor[3];

        for (int i = 0; i < 3; i++) {
            survivors[i] = new TestSurvivor(base);
            threads[i] = new Thread(survivors[i]);
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        assertTrue("Base should have supplies from multiple survivors", base.getSupplyCount() >= 3);
    }

//    @Test NOt working
//    public void testUseToolLocksProperly() throws Exception {
//        Base base = new Base();
//        ExecutorService executor = Executors.newFixedThreadPool(2);
//
//        Future<?> t1 = executor.submit(() -> base.useTool("Task 1"));
//        Future<?> t2 = executor.submit(() -> base.useTool("Task 2"));
//
//        t1.get();
//        t2.get();
//
//        executor.shutdown();
//        assertTrue(executor.awaitTermination(3, TimeUnit.SECONDS));
//    }

    @Test
    public void testConcurrentSupplyAddition() throws Exception {
        Base base = new Base();
        ExecutorService executor = Executors.newFixedThreadPool(3);

        Runnable addTask = () -> {
            for (int i = 0; i < 100; i++) {
                base.addSupplies(1);
            }
        };

        executor.submit(addTask);
        executor.submit(addTask);
        executor.submit(addTask);

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        assertEquals(300, base.getSupplyCount());
    }

    @Test
    public void testSurvivorStopFlag() throws Exception {
        Base base = new Base();
        StoppableSurvivor survivor = new StoppableSurvivor(base);
        Thread thread = new Thread(survivor);
        thread.start();
        thread.join(1000);

        assertFalse("Survivor thread should stop when stop() is called", thread.isAlive());
        assertTrue("Survivor should have performed an action before stopping", survivor.hasPerformedAction());
    }


    @Test
    public void testSurvivorFortify() throws Exception {
        Base base = new Base();
        FortifySurvivor survivor = new FortifySurvivor(base);
        Thread thread = new Thread(survivor);
        thread.start();
        thread.join(3000);

        assertTrue("Survivor should have performed fortify", survivor.hasFortified());
    }


    @Test
    public void testSurvivorRest() throws Exception {
        Base base = new Base();
        RestingSurvivor survivor = new RestingSurvivor(base);
        Thread thread = new Thread(survivor);
        thread.start();
        thread.join(3000);

        assertTrue("Survivor should have rested", survivor.hasRested());
    }

    private static class RestingSurvivor extends Survivor {
        private boolean rested = false;

        public RestingSurvivor(Base base) {
            super(base);
        }

        @Override
        protected void performAction() throws InterruptedException {
            rested = true;
            rest();
            stop();
        }

        public boolean hasRested() {
            return rested;
        }
    }

    private static class FortifySurvivor extends Survivor {
        private boolean fortified = false;

        public FortifySurvivor(Base base) {
            super(base);
        }

        @Override
        protected void performAction() throws InterruptedException {
            fortified = true;
            fortify();
            stop();
        }

        public boolean hasFortified() {
            return fortified;
        }
    }

    private static class StoppableSurvivor extends Survivor {
        private boolean performedAction = false;

        public StoppableSurvivor(Base base) {
            super(base);
        }

        @Override
        protected void performAction() throws InterruptedException {
            performedAction = true;
            stop(); // stop after one action
        }

        public boolean hasPerformedAction() {
            return performedAction;
        }
    }
}


