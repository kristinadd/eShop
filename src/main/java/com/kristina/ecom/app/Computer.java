package com.kristina.ecom.app;

// anything in interfae is automatically public, no need to make it public with a keyword
import java.util.List;

public interface Computer {
  String getDescription();
  double getPrice();
  String getOrderID();
  List<Product> getComponents();
}
