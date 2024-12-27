package com.kristina.ecom.console;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.kristina.ecom.app.Product;

public class Test {

  public static void main(String[] args) {

    // Can do the same thing with Set
    Product p1 =  new Product(1, "Printer", 10, 10);
    //
    Product p2 =  new Product(2, "GPUs", 20, 10);
    Product p3 =  new Product(3, "USB", 30, 10);
    //
    Product p4 =  new Product(2, "GPUs", 20, 10);
    Product p5 =  new Product(3, "USB", 30, 10);
    //
    Product p6 =  new Product(4, "Microphone", 40, 10);


    List<Product> l1 = Arrays.asList(p2, p3);
    List<Product> l2 = Arrays.asList(p4, p5);

      // the common one // update
    List<Product> commanList = l1.stream().filter(s -> l2.contains(s)).collect(Collectors.toList());
    System.out.println(commanList);

    //in the first only // add 
    List<Product> onlyFisrt = l1.stream().filter(s -> !l2.contains(s)).collect(Collectors.toList());
    System.out.println(onlyFisrt);

    // is in the second list only // delete
    List<Product> onlySecond = l2.stream().filter(s -> !l1.contains(s)).collect(Collectors.toList());
    System.out.println(onlySecond);

  }
}
