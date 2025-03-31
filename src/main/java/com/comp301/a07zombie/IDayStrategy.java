package com.comp301.a07zombie;

public interface IDayStrategy {
  void execute(Base base, int durationMilliseconds) throws InterruptedException;
}
