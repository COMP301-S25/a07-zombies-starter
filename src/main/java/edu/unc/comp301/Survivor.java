package edu.unc.comp301;

public class Survivor implements ISurvivor, Runnable {
  private final Base base;
  private volatile boolean stopFlag;

  public Survivor(Base base) {
    this.base = base;
    this.stopFlag = false;
  }

  @Override
  public void stop() {
    this.stopFlag = true;
  }

  @Override
  public void run() {
    this.stopFlag = false;
    while (!stopFlag && !Thread.currentThread().isInterrupted()) {
      if (base.isUnderAttack()) {
        try {
          Thread.sleep(2000);
          System.out.println(Thread.currentThread().getName() + " is defending their base.");
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      } else {
        try {
          this.performAction();
        } catch (InterruptedException e) {
          this.stopFlag = true;
          Thread.currentThread().interrupt();
          break;
        }
      }
    }
  }

  protected void performAction() throws InterruptedException {
    int random = (int) (Math.random() * 3) + 1;
    switch (random) {
      case 1 -> this.fortify();
      case 2 -> this.scavenge();
      case 3 -> this.rest();
    }
  }

  protected void fortify() throws InterruptedException {
    base.useTool("fortification");
    System.out.println("Fortifying the base");
    Thread.sleep(1000);
  }

  protected void scavenge() throws InterruptedException {
    System.out.println(Thread.currentThread().getName() + " is scavenging");
    int randomSleep = (int) (Math.random() * 4) + 1;
    Thread.sleep(1000 * randomSleep);
    int randomSupplies = (int) (Math.random() * 10) + 1;
    base.addSupplies(randomSupplies);
  }

  protected void rest() throws InterruptedException {
    System.out.println(Thread.currentThread().getName() + " is taking a sleep");
    Thread.sleep(2000);
  }
}
