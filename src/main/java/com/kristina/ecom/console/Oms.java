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

  public void admin() {
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
        case 3:
          read();
          break;
        case 4:
          update();
          break;
        case 5:
          cancel();
          break;
        case 6:
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
      "3: Read",
      "4: Update",
      "5: Cancel",
      "6: Return to main menu"
    };
    
    System.out.println("\n*** Order Management System ***");
    Arrays.stream(omsMenu).forEach(System.out::println);
  }

  private void productUpdateMenu(Order order) {
    String[] orderUpdateMenu = {
      "1: Delete a product from the order",
      "2: Add a product to the order",
      "3: Update existing product in the order",
      "4: Done"
    };

    System.out.println("\n*** Order Update Menu ***");
    System.out.println(order);
    Arrays.stream(orderUpdateMenu).forEach(System.out::println);
  }

  public void all() {
    Arrays.stream(service.getAll().toArray()).forEach(System.out::println);
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

  public void deleteProductFromOrder(Order order) {
    int productIndex;
    boolean invalid;
    do {
      System.out.println("Select the product to delete:");
      for (int i = 0; i < order.getProducts().size(); i++) {
          System.out.println((i + 1) + ": " + order.getProducts().get(i));
      }
    productIndex = sc.nextInt() - 1;
    invalid = productIndex < 0 || productIndex >= order.getProducts().size();
    if (invalid)
      System.out.println("Invalid product, please choose again");
    } while (invalid);

    // remove the product from the order
    order.getProducts().remove(productIndex);
    order.update();
  }

  public void addProductToOrder(Order order) {
    int productIndex;
    boolean invalid;
    do {
      System.out.println("Select the product to be added:");
      for (int i = 0; i < order.getProducts().size(); i++) {
          System.out.println((i + 1) + ": " + order.getProducts().get(i));
      }
    productIndex = sc.nextInt() - 1;
    invalid = productIndex < 0 || productIndex >= order.getProducts().size();
    if (invalid)
      System.out.println("Invalid product, please choose again");
    } while (invalid);

    System.out.println("Enter the quantity:");
    int quantity = sc.nextInt();
    // check the quantity on the FE, and give feedback to the user as soon as possible
    if (quantity < 1) {
      System.out.println("The quantity needs to be more than 1 in order to add the product");
    } else { // quantity is more than 0
      // then i need a productFromStock to compare it to 
      // check the quantity in stock for the specific product; available , not available
    }

    // Clone the product to create a copy for the order
    Product productInOrder = (Product) productFromStock.clone();
    productInOrder.setQuantity(quantity);
    order.getProducts().add(productInOrder);
    productFromStock.setQuantity(productFromStock.getQuantity() - quantity);
    
    boolean success = service.updateProductInOrder(order);
    // Display the result
    if (success) {
        System.out.println("Product added successfully.");
    } else {
        System.out.println("Failed to add product. Please check stock or try again.");
    }
  }
  
  public void cancel() {
    System.out.println("*** Select an order to cancel ***");
    all();
    if (service.cancel(sc.next()) > 0)
      System.out.println("Order canceled");
    else
      System.out.println("Order failed");
  }

  public void update() {
    System.out.println("*** Select an order to update ***");
    all();
    Order order = service.get(String.valueOf(sc.nextInt())); // 33
    order.setDate(LocalDateTime.now());
    boolean isDirty = false;
    boolean updating = true;
    while (updating) {
      productUpdateMenu(order);
      int c = sc.nextInt();

      switch (c) {
        case 1:
          deleteProductFromOrder(order);
          isDirty = true;
          break;
        case 2:
        addProductToOrder(order);
        isDirty = true;
          break;
        case 3:
          updateProducts(order);
          isDirty = true;
          break;
        case 4:
          updating = false;
          break;
          // return; it works but thats not the correct way to write it
        default:
          System.out.println("Invalid choice. Please try again.");
      }
    }

    if (isDirty == true) {
      if (service.update(order) == 1) {
        System.out.println("Order updated!");
      } else {
        System.out.println("Update failed");
      }
    } else {
      System.out.println("No change");
    } 
  }


  private void updateProducts(Order order) {
    // Get user input
    int productIndex;
    boolean invalid;
    do {
      System.out.println("Select the product to update:");
      for (int i = 0; i < order.getProducts().size(); i++) {
          System.out.println((i + 1) + ": " + order.getProducts().get(i));
      }
    productIndex = sc.nextInt() - 1;
    invalid = productIndex < 0 || productIndex >= order.getProducts().size();
    if (invalid)
      System.out.println("Invalid product, please choose again");
    } while (invalid);

    System.out.println("Enter the new quantity ( 0 to remove ):");
    // TbC: handle out of stock situation
    int newQuantity =  sc.nextInt();

    if (newQuantity == 0) 
      order.getProducts().remove(productIndex);
    else
      order.getProducts().get(productIndex).setQuantity(newQuantity);

    boolean success = service.updateProductInOrder(order);
    // Display result
    if (success) {
        System.out.println("Product updated successfully.");
    } else {
        System.out.println("Update failed. Please check stock or input validity.");
    }
  }
}
