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
        thread.join(1000);
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
        thread.join(2000);

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

    @Test
        public void testUseToolLocksProperly() throws Exception {
            Base base = new Base();
            ExecutorService executor = Executors.newFixedThreadPool(2);

            Future<?> t1 = executor.submit(() -> base.useTool("Task 1"));
            Future<?> t2 = executor.submit(() -> base.useTool("Task 2"));

            t1.get();
            t2.get();

            executor.shutdown();
            assertTrue(executor.awaitTermination(3, TimeUnit.SECONDS));
        }

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
}


//    private Base base;
//
//    @Before
//    public void setUp() {
//        base = new Base();
//    }
//
//    @Test
//    public void testAddSupplies() {
//        base.addSupplies(10);
//        base.addSupplies(5);
//        assertEquals("Supplies should total 15.", 15, base.getSupplyCount());
//    }
//
//
//
//    @Test
//    public void testToolLocking() throws Exception {
//        ExecutorService executor = Executors.newFixedThreadPool(2);
//        Future<?> f1 = executor.submit(() -> base.useTool("fortification"));
//        Future<?> f2 = executor.submit(() -> base.useTool("repairs"));
//
//        f1.get(); // Wait for first task
//        f2.get(); // Wait for second task
//
//        // If locking was incorrect, both threads might access the tool at the same time,
//        // causing unpredictable failures.
//        assertTrue("Tool usage completed without deadlocks.", true);
//
//        executor.shutdown();
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void testAddInvalidSupplies() {
//        base.addSupplies(-5); // Should throw IllegalArgumentException
//    }
//
//
//    @Test
//    public void testConcurrentSupplyAddition() throws Exception {
//        ExecutorService executor = Executors.newFixedThreadPool(3);
//        Future<?> f1 = executor.submit(() -> base.addSupplies(5));
//        Future<?> f2 = executor.submit(() -> base.addSupplies(10));
//        Future<?> f3 = executor.submit(() -> base.addSupplies(15));
//
//        f1.get();
//        f2.get();
//        f3.get();
//
//        assertEquals("Total supplies should be 30.", 30, base.getSupplyCount());
//        executor.shutdown();
//    }
//
//    @Test
//    public void testToolReentrantLocking() {
//        base.useTool("repair");
//        base.useTool("fortification"); // Should not cause issues
//        assertTrue("Tool usage completed successfully.", true);
//    }
//
//
//    @Test
//    public void testInterruptDuringToolUse() throws Exception {
//        Thread thread = new Thread(() -> {
//            try {
//                base.useTool("fortification");
//            } catch (Exception e) {
//                fail("Thread should handle interruption gracefully.");
//            }
//        });
//
//        thread.start();
//        Thread.sleep(500); // Give time for the tool use to start
//        thread.interrupt();
//        thread.join();
//
//        assertTrue("Tool usage should complete despite interruption.", true);
//    }
//
//    @Test
//    public void testSurvivorGathersSupplies() throws Exception {
//        Survivor survivor = new GatheringSurvivor(base);
//        Thread thread = new Thread(survivor);
//
//        thread.start();
//        Thread.sleep(50); // Give time for at least one action
//        survivor.stop();
//        thread.interrupt(); // Stop the Survivor
//        thread.join(); // Wait for it to finish
//
//        assertTrue("Base should have more than supply.", base.getSupplyCount()>0);
//    }
//
//
//    @Test
//    public void testSurvivorUsesTool() throws Exception {
//        Survivor survivor = new Survivor("Bob", base);
//        Thread thread = new Thread(() -> base.useTool("fortification"));
//
//        thread.start();
//        thread.join();
//
//        assertTrue("Survivor should successfully use a tool.", true);
//    }
//
//    @Test
//    public void testMultipleSurvivors() throws Exception {
//        ExecutorService executor = Executors.newFixedThreadPool(3);
//        Survivor s1 = new GatheringSurvivor(base);
//        Survivor s2 = new GatheringSurvivor(base);
//        Survivor s3 = new GatheringSurvivor(base);
//
//        Future<?> f1 = executor.submit(s1);
//        Future<?> f2 = executor.submit(s2);
//        Future<?> f3 = executor.submit(s3);
//
//        Thread.sleep(100); // Allow survivors to perform actions
//
//        assertTrue("Base should have supplies from multiple survivors.", base.getSupplyCount() > 0);
//
//        executor.shutdownNow();
//    }
//
//    @Test
//    public void testSurvivorInterruption() throws Exception {
//        Survivor survivor = new GatheringSurvivor( base);
//        Thread thread = new Thread(survivor);
//
//        thread.start();
//        Thread.sleep(500); // Give some time for survivor to start
//        survivor.stop();
//        thread.interrupt();
//        thread.join();
//
//        survivor.stop();
//        assertTrue("Survivor should handle interruption gracefully.", true);
//    }
//
//    private class GatheringSurvivor extends Survivor {
//        public GatheringSurvivor(Base base) {
//            super(base);
//        }
//
//        @Override
//        protected void performAction() { // Override to force supply gathering
//            base.addSupplies(1);
//        }
//    }
//
//

