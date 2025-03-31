package com.comp301.a07zombie;

public interface ISurvivor extends Runnable {
  void stop(); // Stops the survivor thread

  @Override
  void run(); // Defines how the survivor executes tasks asynchronously
}
