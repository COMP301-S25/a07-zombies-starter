package edu.unc.comp301;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Base implements IBase {
  private int supplyCount;
  private Lock lock;
  private volatile boolean isUnderAttack;

  public Base() {
    supplyCount = 0;
    lock = new ReentrantLock();
  }

  @Override
  public synchronized void addSupplies(int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException();
    }
    supplyCount += amount;
    System.out.println(
        Thread.currentThread().getName() + " added " + amount + "supplies. Total: " + supplyCount);
  }

  @Override
  public synchronized int getSupplyCount() {
    return supplyCount;
  }

  @Override
  public void useTool(String task) {
    lock.lock();
    try {
      System.out.println(Thread.currentThread().getName() + " is using the tool");
      System.out.println(task);
      Thread.sleep(1000);
      System.out.println("The tool is no longer in use");
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      lock.unlock();
    }
  }

  public synchronized void startAttack() {
    this.isUnderAttack = true;
    System.out.println("Zombies are approaching");
  }

  public synchronized void endAttack() {
    this.isUnderAttack = false;
    System.out.println("The survivors have successfully repelled the zombies.");
  }

  public synchronized boolean isUnderAttack() {
    return this.isUnderAttack;
  }
}
