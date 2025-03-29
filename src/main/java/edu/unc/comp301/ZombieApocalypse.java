package edu.unc.comp301;

public class ZombieApocalypse {
  protected static Base base;
  protected static Survivor[] survivors;
  protected static Thread[] survivorThreads;

  public static void main(String[] args) throws InterruptedException {
    startSimulation(5);
    IDayStrategy strategy = new RandomZombieAttacks();
    simulateDayNightCycle(10000, strategy);
    endSimulation();
  }

  public static void startSimulation(int numSurvivors) {
    base = new Base();
    survivors = new Survivor[numSurvivors];
    survivorThreads = new Thread[numSurvivors];
    for (int i = 0; i < numSurvivors; i++) {
      Survivor survivor = new Survivor(base);
      Thread thread = new Thread(survivor);
      thread.setName("Survivor-" + i);
      survivors[i] = survivor;
      survivorThreads[i] = thread;
      thread.start();
    }
  }

  private static void simulateDayNightCycle(int milliseconds, IDayStrategy events)
      throws InterruptedException {
    events.execute(base, milliseconds);
  }

  private static void endSimulation() {
    System.out.println("Simulation ending...");
    for (Survivor survivor : survivors) {
      survivor.stop();
    }
    for (Thread thread : survivorThreads) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
    System.out.println("All survivors have stopped. Simulation over.");
  }
}
