package com.kristina.ecom.app;

public class Product implements Cloneable {
  private int id;
  private String type;
  private String name;
  private double price;
  private String img;
  private int quantity;

  public Product() {
    // default constructor, just empty
  }

  public Product(String type, String name, double price, String img) {
    this(0, type, name, price, 0, img);
  }

  public Product(String type, String name, double price, int quantity) {
    this(0,  type, name, price, quantity, " "); // default image of nothing 
  }

  public Product(String type, String name, double price, int quantity, String img) {
    this(0, type, name, price, quantity, img);
  }

  // constructor for the read method
  public Product( int id, String name, double price, int quantity) {
    this(id,  " ", name, price, quantity, " ");
  }

  public Product(int id, String type, String name, double price, int quantity, String img) {
    this.id = id;
    this.type = type;
    this.name = name;
    this.price = price;
    this.quantity = quantity;
    this.img = img;
  }

  public int getId() {
    return this.id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getPrice() {
    return this.price;
  }

  public void setPrice(double price) {
     this.price = price;
  }

  public int getQuantity() {
    return this.quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
 }

  public String getImg() {
    return this.img;
  }

  @Override
  public String toString() {
    return String.format("%s, %.2f, %d", this.name, this.price, this.quantity);
  }

  // @Override
  // public Product clone() {
  //   return new Product(this.id, this.name, this.price, this.quantity, this.img);
  // }

  @Override 
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  @Override 
  public boolean equals(Object obj) {
    // two products are  == if they have the same type
    if (!(obj instanceof Product))
      return false;

      //  and the same product id
    return ((Product) obj).getId() ==  this.getId();
  };
}
