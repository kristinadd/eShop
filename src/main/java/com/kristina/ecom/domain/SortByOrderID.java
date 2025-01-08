package com.kristina.ecom.domain;

import java.util.List;
import java.util.Comparator;
import java.util.Collections;

public class SortByOrderID implements SortStrategy {

  @Override 
  public void sort(List<Computer> cart) {

    Comparator<Computer> comparator = new Comparator<>() {
      
      @Override 
      public int compare(Computer c1, Computer c2) {
        return c2.getOrderID().compareTo(c1.getOrderID());
      }
    };
    Collections.sort(cart, comparator);
  }
}
