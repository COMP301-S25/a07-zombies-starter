package com.comp301.a07zombie;

public class Survivor implements ISurvivor {

  public Survivor(Base base) {}

  @Override
  public void stop() {}

  @Override
  public void run() {}

  protected void performAction() throws InterruptedException {}

  protected void scavenge() throws InterruptedException {}

  protected void fortify() throws InterruptedException {}

  protected void rest() throws InterruptedException {}
}
