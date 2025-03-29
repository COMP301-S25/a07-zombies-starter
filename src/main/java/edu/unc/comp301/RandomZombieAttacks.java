package edu.unc.comp301;

public class RandomZombieAttacks implements IDayStrategy{
    @Override
    public void execute(Base base, int durationMilliseconds) throws InterruptedException {
        int unit = durationMilliseconds / 5;
        Thread.sleep(unit);
        base.startAttack();
        Thread.sleep(unit);
        base.endAttack();
        Thread.sleep(unit);
        base.startAttack();
        Thread.sleep(unit);
        base.endAttack();
        Thread.sleep(unit);
    }
}
