package com.kristina.ecom.app;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
// concrete java class

public class ComputerBase implements Computer {
  // private static final String NAME = "Default Computer";
  // private static final double PRICE = 700.0;
  private static final int SIZE = 100;
  private static List<Integer> ids = new Random().ints(1, SIZE+1).distinct().limit(SIZE).boxed().collect((Collectors.toList()));


  private String orderID;
  private String description;
  private double price;
  private List<Product> components;

  // Default constructor
  public ComputerBase() {
    // the constructor needs to be placed before anything else
      this(getID(), new ArrayList<Product>());
  }

  // Constructor with parameters
  public ComputerBase(String orderID, List<Product> components) {
    Product computer = new ProductService().getComputer();
    this.orderID = orderID;
    this.description = computer.getName();
    this.price = computer.getPrice();
    this.components = components;
  }

  @Override
  public String getDescription() {
    return this.description;
  }

  @Override
  public double getPrice() {
    return this.price;
  }

  @Override
  public String getOrderID() {
    return this.orderID;
  }

  @Override
  public List<Product> getComponents() {
    return components;
  }

  // @Override
  // public void setComponents(List<Product> components) {
  //   this.components = components;
  // }


  @Override
  public String toString() {
    return "ComputerBase [orderID=" + orderID + ", description=" + description + ", price=" + price + ", components="
        + components + "]";
  }

  private static String getID() {
    // Random rand = new Random();
    // System.out.println(java.util.Arrays.toString(ids.toArray()));

    return Integer.toString(ids.remove(0));
  }
}
