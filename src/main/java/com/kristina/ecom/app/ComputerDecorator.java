package com.kristina.ecom.app;

import java.util.List;

public class ComputerDecorator implements Computer{
  private Computer computer;

  public ComputerDecorator(Computer computer) {
    this.computer = computer;
  }

  @Override
  public String getDescription() {
    return this.computer.getDescription();
  }

  @Override
  public double getPrice() {
    return this.computer.getPrice();
  }
  
  @Override
  public String getOrderID() {
    return this.computer.getOrderID();
  }

  @Override
  public List<Product> getComponents() {
    return this.computer.getComponents();
  }

  @Override
  public String toString() {
    return "ComputerDecorator [computer=" + computer + ", getDescription()=" + getDescription() + ", getPrice()="
        + getPrice() + ", getOrderID()=" + getOrderID() + ", getComponents()=" + getComponents() + "]";
  }

  public Computer getComputer() {
    return computer;
  }
}
