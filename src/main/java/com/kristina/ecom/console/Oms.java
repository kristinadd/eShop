package com.kristina.ecom.console;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.kristina.ecom.app.Order;
import com.kristina.ecom.app.OrderService;
import com.kristina.ecom.app.ProductService;
import com.kristina.ecom.app.Product;

public class Oms {
  private static Oms instance = new Oms();
  private Scanner sc;
  private OrderService service;

  private Oms() {
    sc = new Scanner(System.in);
    service = new OrderService();
  }

  public static Oms instance() {
    return instance;
  }

  public void oms() {
    while (true) {
      menu();
      int c = sc.nextInt();
      switch (c) {
        case 1:
          all();
          break;
        case 2:
          delete();
          break;
        case 4:
          read();
          break;
        case 5:
          orderUpdateMenu();
          break;
        case 6:
          cancel();
          break;
        case 7:
          return;
        default:
          System.out.println("Invalid choice. Please try again.");
      }
    }
  }

  private void menu() {
    String[] omsMenu = {
      "1: All orders",
      "2: Delete",
      "4: Read",
      "5: Update",
      "6: Cancel",
      "7: Return to main menu"
    };
    
    System.out.println("\n*** Order Management System ***");
    Arrays.stream(omsMenu).forEach(System.out::println);
  }

  private void orderUpdateMenu() {
    String[] orderUpdateMenu = {
      "1: Delete a product from the order",
      "2: Add a product to the order",
      "3: Update existing product in the order",
    };
    
    System.out.println("\n*** Order Update Menu ***");
    Arrays.stream(orderUpdateMenu).forEach(System.out::println);
  }

  public void all() {
    Arrays.stream(service.getAll().toArray()).forEach(
      order -> System.out.println(((Order) order).getId() + ":" + order));
  }

  public void read() {
    System.out.print("Which order would you like to read: ");
    String id = sc.next();
    System.out.println(service.get(id));
  }

  public void delete() {
    all();
    System.out.print("Which order would you like to delete: ");
    String id = sc.next();

    if (service.delete(id) > 0)
      System.out.println("Order deleted");
    else
      System.out.println("Delete failed");
  }

  public void cancel() {
    System.out.println("*** Select an order to cancel ***");
    all();
    if (service.cancel(sc.next()) == 0)
      System.out.println("Order canceled");
    else
      System.out.println("Order failed");
  }

  public void update() {
    System.out.println("*** Select an order to update ***");
    all();
    int OrderID = sc.nextInt();
    Order order = service.get(String.valueOf(OrderID));
    sc.nextLine();

    System.out.print("All products in the order:\n");
    int counter = 1;
    for (Product product : order.getProducts()) {
      System.out.println(counter++ + " : " + product);
    }

    String datetime = LocalDateTime.now().toString();
    if (!datetime.isEmpty()) {
      order.setDate(LocalDateTime.parse(datetime));
    }

    // Product in the stock
    System.out.println("Select the product to be updated:");
    int productIndex = sc.nextInt();
    Product productFromOrder = order.getProducts().get(productIndex - 1);
    ProductService productService = new ProductService();
    Product productFromStock = productService.get(productFromOrder.getId());

    // User input for quantity
    System.out.println("What quantity do you want: ");
    int quantityFromUser = sc.nextInt();

    // System.out.println("################# check variables #########################");
    // System.out.println(productFromOrder);
    // System.out.println(productFromStock);
    // System.out.println(quantityFromUser);

    // when a user want to update the order and add new products to the order
    // select update (submenue)
      // delete a product from the order
      // add a product to the order
      // update existing product in the order

    if (quantityFromUser == 0) { // only positive numbers
      System.out.println("Delete the product from the order and return the product to the stock");
      productFromStock.setQuantity(productFromStock.getQuantity() + productFromOrder.getQuantity());
      productFromOrder.setQuantity(0);
      productService.update(productFromStock);
      order.getProducts().remove(productIndex - 1);
      service.delete(order.getId(), productFromOrder.getId());
      // delete the product from the order
    } else if (quantityFromUser > productFromOrder.getQuantity()) {
        // when user increases the count of the product
        int difference = quantityFromUser - productFromOrder.getQuantity();
        if (productFromStock.getQuantity() >= difference) {
          productFromOrder.setQuantity(quantityFromUser);
          productFromStock.setQuantity(productFromStock.getQuantity() - difference);
          productService.update(productFromStock);
          System.out.println("The new quantity of the order is: " + productFromOrder.getQuantity());
        } else {
          System.out.println("Sorry! Not enough stock. The stock has only " + productFromStock.getQuantity() + " products");
        }
     // when user decreases the count of the product
    } else {
      int difference = productFromOrder.getQuantity() - quantityFromUser;
      productFromOrder.setQuantity(quantityFromUser);
      productFromStock.setQuantity(productFromStock.getQuantity() + difference);
      productService.update(productFromStock);
      System.out.println("The new quantity of the product in the order is: " + productFromOrder.getQuantity());
    }

    if (service.update(order) == 1) {
      System.out.println("Order updated");
    } else {
      System.out.println("Update failed");
    }
  }
}
