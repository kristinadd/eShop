package com.kristina.ecom.domain;

import java.util.List;
public interface Computer {
  String getDescription();
  double getPrice();
  String getOrderID();
  List<Product> getComponents();
}
