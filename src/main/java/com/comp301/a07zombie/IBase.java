package com.comp301.a07zombie;

public interface IBase {
  void addSupplies(int amount); // Adds supplies to the base

  int getSupplyCount(); // Returns the current supply count

  void useTool(String task); // Allows a survivor to use a tool
}
