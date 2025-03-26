package edu.unc.comp301;


public interface IDayStrategy {
    void execute(Base base, int durationMilliseconds) throws InterruptedException;
}
