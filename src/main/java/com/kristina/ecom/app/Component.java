package com.kristina.ecom.app;

public class Component extends ComputerDecorator {
  private String description;
  private double price;

  public Component(Computer computer) {
    super(computer);
  }

  public Component(Computer computer, Product product) {
    super(computer);
    this.description = product.getName();
    this.price = product.getPrice();
    if (super.getComponents().contains(product)) {
      // indexOf --> get the index in the array
      Product p = super.getComponents().get(super.getComponents().indexOf(product));
       p.setQuantity(p.getQuantity() + product.getQuantity());
    } else
      super.getComponents().add(product);
  }

  @Override
  public String getDescription() {
    return super.getDescription() + " + " + this.description;
  }

  @Override
  public double getPrice() {
    return super.getPrice() + this.price;
  }

  @Override
  public String toString() {
    // return "OrderID@" + this.getOrderID() + ": " + this.getDescription() + " $" + this.getPrice();
    return String.format("OrderID@%s: %s s%.2f", this.getOrderID(), this.getDescription(), this.getPrice());
  }
}
