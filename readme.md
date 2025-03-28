# a07-survivor

## Introduction

This assignment provides practice with asynchronous programming and synchronization in Java. You will implement two classes, `Base` and `Survivor`, which work together in a multithreaded survival simulation.

In this simulation, multiple Survivors will perform tasks asynchronously, interacting with a shared Base object to gather supplies and fortify defenses. Since multiple survivors will be accessing shared resources simultaneously, you must use synchronization techniques to ensure data consistency and avoid race conditions.

You will be provided with two interfaces, IBase and ISurvivor, which define the required functionality for the Base and Survivor classes. Your task is to implement these classes according to the specifications below.

## Novice

### Base
The `Base` class represents the survivors' home, where supplies are stored and tools are used. It must be thread-safe because multiple survivors may interact with it at the same time.

#### Interface: `IBase`
```
public interface IBase {
  void addSupplies(int amount);  // Adds supplies to the base
  int getSupplyCount();  // Returns the current supply count
  void useTool(String task);  // Allows a survivor to use a tool
}
```

### Class Specification: `Base`
Your Base class must implement IBase and include the following functionality:

### `addSupplies(int amount)`
The addSupplies method should be `synchronized` so that multiple survivors cannot modify the supply count at the same time. It takes an integer amount representing the number of supplies to add. If the amount is less than or equal to zero, the method should throw an `IllegalArgumentException`. Otherwise, it should increase the total supply count and print a message indicating how many supplies were added and the new total.  You can get the name of the current caller by using `Thread.currentThread().getName()`.

Example output:
`Survivor-1 added 5 supplies. Total: 15`

### `getSupplyCount()`
Standard getter with a special case.  The getSupplyCount method should also be `synchronized` since it reads the shared supply count. This method returns an integer representing the current total number of supplies.

### `useTool(String task)`
The useTool method allows a survivor to perform a task using a tool. This method should not be synchronized, but instead, it should use a `Lock` to ensure that only one survivor can use a tool at a time. The method takes a string describing the task and should print a message indicating that the survivor is using a tool. To simulate the time required for tool use, the method should call Thread.sleep(1000). After that, it should print a message indicating that the tool is no longer in use. The lock should always be released properly, even if an exception occurs.


## Survivor
The Survivor class represents an individual survivor who performs actions asynchronously. Each survivor runs as a separate thread, performing tasks such as gathering supplies, fortifying the base, and resting.

### Interface: `ISurvivor`

```
public interface ISurvivor extends Runnable {
  void stop();  // Stops the survivor thread
  @Override
  void run();  // Inherited from Runnable. Defines how the survivor executes tasks asynchronously
}
```

### Class Specification: Survivor

### Protected methods

In addition to the exposed methods of the interface, the Survivor should be able to perform specific `protected` actions to help the community survive. The action methods should throw `InterruptedException`

- `fortify()`
- `scavenge()`
- `rest()`
- `performAction()` (calls the other actions)

You may create other private methods as you deem necessary, but these must exist as protected (for testing purposes).

### `performAction()`
The performAction method determines what the survivor does at any given moment. This method should randomly select one of three possible actions: scavenging for supplies, fortifying the base, or resting.

### `fortify()`
The fortify method represents a survivor using a tool to strengthen the base. This method should call the useTool method of Base with the string "fortification". It should also print a message indicating that the survivor is fortifying the base and take 2 seconds to complete.

### `scavenge()`
The scavenge method represents a survivor searching for supplies. It should print a message indicating that the survivor is scavenging and then sleep for a random amount of time between one and four seconds to simulate the time taken to find supplies. After that, the survivor should add a random number of supplies, between one and ten, to the base.

### `rest()`
The rest method represents a survivor taking a break. It should print a message indicating that the survivor is resting and then sleep for two seconds.

### Public methods:

### `run()`
The run method defines the behavior of a survivor when it is running as a thread. The method should continuously execute until the survivor is stopped. In each iteration of its loop, the survivor should call performAction to randomly choose an action. Here is where you have to deal with the InterruptException.  If the survivor is interrupted, it must signal that the survivor needs to stop to ensure proper thread handling.

### `stop()`
The stop method provides a way to stop the survivor safely. Instead of terminating the thread forcefully, this method should set a `volatile boolean` flag to false, allowing the survivor's run loop to exit cleanly.

_Pro Tips: Your implementation should ensure that Survivor runs as a separate thread by implementing Runnable. The survivor should check a volatile boolean flag to determine when to stop running. If the survivor is interrupted, it should exit the loop immediately. Synchronization must be used in Base to prevent race conditions when updating supplies. The useTool method should use a Lock instead of the synchronized keyword so that only one survivor can use a tool at a time. Randomization should be used to determine which action a survivor takes when calling performAction._


## Adept


### **ZombieApocalypse Simulation**

The `ZombieApocalypse` class is responsible for managing the entire survival simulation. It initializes survivors, runs the simulation for a set period, and then stops all survivor threads. This class provides structure for handling **asynchronous survivor actions**.

This class consists of three methods plus main:

- **`startSimulation(int numSurvivors)`**: Initializes the base and survivors, then starts each survivor in its own thread.
- **`simulateDayNightCycle(int milliseconds,IDayStrategy events)`**: Allows the simulation to run for a specified duration before stopping.
- **`endSimulation()`**: Stops all survivors and ensures that their threads terminate properly.
-
- The **`main()`** method calls these methods in order to run the full simulation.

We are also going to have a few instance variables.  To keep things simple, we are going to make everything static.  There is only one zombieApocalpse, and this is the last base on earth.

Add the following instance variables to this class:
```
    protected static Base base;
    protected static Survivor[] survivors;
    protected static Thread[] survivorThreads;
```

### **`public static void main(String[] args)`**

The `main` method is the entry point of the program. It starts the simulation by calling `startSimulation(5)`, which creates **five survivors**. Then, it calls `simulateDayNightCycle(10000)`, which allows the simulation to continue running for **10 seconds** before stopping. Finally, it calls `endSimulation()` to cleanly stop all survivor threads and exit the program.




### **`public static void startSimulation(int numSurvivors)`**

This method sets up the survival environment by initializing the base and creating survivor threads. It first instantiates a `Base` object, which serves as the shared resource that all survivors interact with. Then, it creates an array of `Survivor` objects and an array of `Thread` objects, both sized according to the `numSurvivors` parameter.

For each survivor, the method creates a corresponding thread, assigns it a name (e.g., `"Survivor-1"`), and starts it. At this point, all survivors begin running concurrently, performing actions asynchronously.


### **`private static void simulateDayNightCycle(int milliseconds, IDayStrategy events)`**

This method allows time to pass in the simulation before stopping. We want to begin with a quiet day, so we will inject a QuietDayStrategy object.  Take a look at the given class.  It simply calls `Thread.sleep(milliseconds)`, pausing the main thread while survivor threads continue their actions. If the thread is interrupted during this sleep period, an exception is thrown.


### **`private static void endSimulation()`**

This method stops all survivors and ensures their threads terminate properly. First, it prints `"Simulation ending..."`, then loops through all survivors and calls `stop()` on each one, signaling them to exit their run loops.

Next, it loops through all survivor threads and calls `join()`, ensuring that each thread has completely finished before the program exits. If an interruption occurs during this process, the program restores the thread’s interrupted status.

Once all survivor threads have stopped, it prints `"All survivors have stopped. Simulation over."` and the program terminates cleanly.


## Jedi

**Zombie Attack!!!**

## Adding Zombie Attacks to the Base Class

To simulate a true zombie apocalypse, the `Base` class needs to be able to track whether the base is currently under attack by zombies. This allows other parts of the program, such as the survivors, to respond appropriately when danger is near. To make this possible, you’ll need to add a single instance variable  to the `Base` class, along with three methods that manage and report on the attack status.

Start by adding a private instance variable to the class called `isUnderAttack`. This variable should store a boolean value indicating whether or not the base is currently under attack . It will need to be updated by other parts of the simulation, such as the zombie attack logic in the `ZombieApocalypse` class, and read by survivor threads to determine how to behave.

Once the variable is in place, you’ll need to implement three synchronized methods that manage its state: `startAttack()`, `endAttack()`, and `isUnderAttack()`.


`isUnderAttack()` should return a boolean indicating whether the base is currently under attack. Survivor threads will call this method repeatedly to decide whether to keep doing their normal activities or take defensive action instead.

`startAttack()` should mark the beginning of an attack. When this method is called, the base should update its internal state to reflect that it is under attack, and it should print a message to indicate that zombies are approaching. This method might be called from a simulation loop that randomly triggers attacks during the course of the program.

`endAttack()` should end the attack. When this method is called, the base should update its internal state to indicate that the attack is over, and it should print a message showing that the survivors have successfully repelled the zombies.

All three methods should be synchronized to ensure that updates and reads of the attack status are thread-safe. Since survivor threads may be checking the status at the same time that another thread is starting or ending an attack, synchronization is required to prevent race conditions and inconsistent behavior.

After making these additions, the base will be able to participate in the zombie attack simulation. You can then move on to modifying the survivor class so that each survivor can behave differently when an attack is underway.

## Adding Zombie Attacks to the Survivor Class

If the base is under attack, when the Surivor is finished with their previous action, they should not pick another action.  Instead, they should spend the next 2 seconds (2000 milliseconds) defending the base.  It should print out that the survivor is performing this action.

## Adding Zombie Attacks to the Zombie Apocalypse (obviously)

Instead of making the day's events a `QuietDayStrategy`, let's add in some fun.  Replace it with a RandomZombieAttacks strategy, and see how your survivors react.


### Adding a Zombie Attack Strategy

To make the zombie attack portion of your simulation both flexible and testable, you'll be implementing a strategy class that controls when zombie attacks begin and end. This strategy will be used during the day-night cycle of the simulation, and its job is to manage the timing and frequency of zombie attacks in a way that fits within the total time allocated for the simulation. Instead of hardcoding the attack logic directly inside your simulation loop, you'll encapsulate it in a separate class. This will make it easier to test, reuse, and replace with other strategies if needed.

Start by creating a new class that implements the `ZombieAttackStrategy` interface. This interface should already be defined for you and includes a single method called `execute`. The method takes in a `Base` object and an integer representing the total number of milliseconds the simulation should run. Your job is to implement this method so that it triggers exactly two zombie attacks, with each attack taking up a portion of the total simulation time.

The total time passed into the method must be used exactly. That means the combination of all attack durations and the gaps between them should add up to the total duration. To achieve this, divide the total time into equal parts for gaps and attacks. Each attack should last for one-fifth of the total duration. The remaining time should be evenly split into three gaps: one before the first attack, one between the two attacks, and one after the second attack. During each gap, the method should sleep. When it’s time for an attack, the method should call `startAttack()` on the base, sleep for the attack duration, and then call `endAttack()`.

This method should throw an `InterruptedException`, which is already declared in the method signature. You don’t need to catch it — just let it propagate if it occurs. This makes your method more testable and avoids hiding potential timing issues during execution. When you're finished, your implementation will make sure that the simulation lasts exactly the amount of time requested and that zombie attacks are properly spaced and managed within that period.

Once you’ve implemented the class, you’ll be able to plug it into the simulation by passing it into the `simulateDayNightCycle` method. This design will also allow you to create alternate strategies (days) in the future, such as ones with random timing, different numbers of attacks, etc.

### Make your own day

Finally, you get a chance to bend the future to your wishes.  Make a new strategy called MyDayMyWayStrategy, and decide when and how often zombies will attack that day.


Happy Hunting!