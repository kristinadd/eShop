package com.kristina.ecom.console;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.kristina.ecom.app.Component;
import com.kristina.ecom.app.Computer;
import com.kristina.ecom.app.ComputerBase;
import com.kristina.ecom.app.Order;
import com.kristina.ecom.app.OrderService;
import com.kristina.ecom.app.Product;
import com.kristina.ecom.app.ProductService;
import com.kristina.ecom.app.SortByOrderID;
import com.kristina.ecom.app.SortByPrice;
import com.kristina.ecom.app.SortStrategy;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.sql.SQLException;

public class MarketSpace {
  private static  MarketSpace instance = new MarketSpace();
  private Map<Integer, Product> products;
  private List<Computer> cart;
  private SortStrategy strategy, sortByOrderIDStrategy, sortByPriceStrategy;

  private MarketSpace() {
    products = new HashMap<>();
    cart = new ArrayList<>();
    sortByOrderIDStrategy = new SortByOrderID();
    sortByPriceStrategy = new SortByPrice();
  }

  public static MarketSpace instance() {
    return instance;
  }

  public void loadProducts() {
    try {
      Scanner sc = new Scanner(new File("resources/products.csv"));
      // needed to change it to the full path

      String[] tokens = null;
      int i = 1;
      while (sc.hasNextLine()) {
        tokens = sc.nextLine().split(",");
        Product product = new Product("Component", tokens[0], Double.parseDouble(tokens[1]), tokens[2]);
        products.put(i++, product);
      }
      } catch (FileNotFoundException ex){
        System.out.println("File not found");
      }
      System.out.println(products.size());
  }

  // public void loadDB() {
  //     // for (Product product : new ProductService().getAll())
  //     //   this.products.put(product.getId(), product);

  //       new ProductService().getAll().forEach((product) -> this.products.put(product.getId(), product));
  // }

  public void buy() {
    // load products
    new ProductService().getAll().forEach((product) -> this.products.put(product.getId(), product));

    Computer computer = new ComputerBase();
    Boolean cancel = false;

    Scanner sc = new Scanner(System.in);
    int c = 0;

    while (true) {
      System.out.printf("Current Build: %s, and total price is %.2f\n", computer.getDescription(), computer.getPrice());
      System.out.println("What component would you like to add?");
      menu();


      c = sc.nextInt();
      if (c == -1) {
        cancel = true;
        break;
      } 
      
      if (c == 0)
        break;

      if  (products.keySet().contains(c)) {
        Product product = products.get(c);

        if (product.getQuantity() == 0) {
          System.out.println("Out of stock. Select another product.");

        } else {
          //computer is decorator pathern
          Product p = new Product();
          try {
            p = (Product) product.clone(); // clone new product;
            p.setQuantity(1);
          } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
          } 
          computer = new Component(computer, p);
          product.setQuantity(product.getQuantity() - 1);
        }
      } else {
        System.out.println("Invalid choice. Please try again.");
        continue;
      }
    }

    if (!cancel) {
      cart.add(computer);
      
      Order order = new Order(computer);
      OrderService service = new OrderService();
      service.create(order);

      } else {
        System.out.println("Order is canceled!");
    }
  }

  public void getCart() {
    if (cart.isEmpty())
      System.out.println("No items");
    else
      System.out.print(Arrays.toString(cart.toArray()));
  }

  private void menu() {
    System.out.println("I'm in the MarketSpace class!");

    // for (int i=0; i<products.size(); i++)
    //   System.out.println((i+1) + ": " + products.get(i));

    // for (Map.Entry<Integer, Product> entry: products.entrySet())
    //     System.out.println(entry.getKey() + ": " + entry.getValue());

        products.forEach((k,v) -> System.out.println(k + ":" + v ));
        // ^ the same as the for loop above it 

        // -1 , 0 
      System.out.println(-1 + ": " + "Cancel");
      System.out.println(0 + ": " + "Done");


  }

  public void sort(String key) {
    if (cart.isEmpty()) {
      System.out.println("No items");
      return;
    }
    if (key.equals("ID"))
      this.strategy = this.sortByOrderIDStrategy;
    else if (key.equals("PRICE"))
      this.strategy = this.sortByPriceStrategy;

    this.strategy.sort(cart);
  }
}

  // products.values().forEach((p) -> {
      //   if (components.containsKey(p.getId())) {
      //     p.setQuantity(p.getQuantity() - components.get(p.getId()).getQuantity());
      //     // System.out.println(p);
      //     servicep.update(p);
      //   }