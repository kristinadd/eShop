package com.kristina.ecom.console;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.kristina.ecom.domain.Computer;
import com.kristina.ecom.domain.Order;
import com.kristina.ecom.domain.SortByOrderID;
import com.kristina.ecom.domain.SortByPrice;
import com.kristina.ecom.domain.SortStrategy;
import com.kristina.ecom.service.OrderService;

public class ShoppingCart {
  private Scanner sc;
  private List<Computer> cart;
  private SortStrategy strategy, sortByOrderIDStrategy, sortByPriceStrategy;


  public ShoppingCart(List<Computer> cart) {
    sc = new Scanner(System.in);
    this.cart = cart;
    sortByOrderIDStrategy = new SortByOrderID();
    sortByPriceStrategy = new SortByPrice();
  }

  public void admin() {
    while (true) {
      menu();
      int c = sc.nextInt();
      switch (c) {
        case 1:
          getCart();
          break;
        case 2:
          sort("ID");
          break;
        case 3:
        sort("PRICE");
          break;
        case 4:
          checkOut();
          break;
        case 5:
          return;
        default:
          System.out.println("Invalid choice. Please try again.");
      }
    }
  }

  private void menu() {
    String[] items = {
      "See my shopping cart",
      "Sort by order ID (Descending order)",
      "Sort by order price (Descending order)",
      "Check out",
      "Return to main menu"
    };

    System.out.println("Shopping Cart details: ");

    for (int i = 0; i< items.length; i++) {
      // System.out.println(i+1 + " " + items[i]);
      System.out.printf("%d: %s\n", i+1, items[i]);
    }
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

  public void getCart() {
    if (cart.isEmpty())
      System.out.println("No items");
    else
      System.out.print(Arrays.toString(cart.toArray()));
  }

  public void checkOut() {
    OrderService service = new OrderService();

    for (Computer computer : cart) {
      Order order = new Order(computer);
      service.create(order);
    }
  }
}

// change the db to noSQL / Casandra
// start React 
