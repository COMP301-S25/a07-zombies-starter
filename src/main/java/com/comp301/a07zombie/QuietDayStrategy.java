package com.comp301.a07zombie;

public class QuietDayStrategy implements IDayStrategy {

  @Override
  public void execute(Base base, int durationMilliseconds) throws InterruptedException {
    Thread.sleep(durationMilliseconds);
  }
}
