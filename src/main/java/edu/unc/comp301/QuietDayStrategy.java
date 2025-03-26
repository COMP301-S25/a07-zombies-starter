package edu.unc.comp301;


public class QuietDayStrategy implements IDayStrategy {

    @Override
    public void execute(Base base, int durationMilliseconds) throws InterruptedException {
        Thread.sleep(durationMilliseconds);
    }
}
