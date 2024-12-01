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

  // public void deleteProductFromOrder(Order order) {
  //   System.out.println("Select the product to delete:");
    
  //   for (int i = 0; i < order.getProducts().size(); i++) {
  //     System.out.println((i + 1) + ": " + order.getProducts().get(i));
  //   }
  //   int productIndex = sc.nextInt() - 1;
  //   if (productIndex < 0 || productIndex >= order.getProducts().size()) {
  //     System.out.println("Invalid selection. Please try again.");
  //     return;
  //   }
  //   Product product = order.getProducts().get(productIndex);
  //   order.getProducts().remove(productIndex);
  //   order.update(); // memory
  //   // service.update(order); // database
  //   // if (service.delete(order.getId(), product.getId()) > 1 ) {
  //   //   System.out.println("Product deleted from the order");
  //   // } else {
  //   //   System.out.println("Delete failed");
  //   // }
  // }

  public void deleteProductFromOrder(Order order) {
    System.out.println("Select the product to delete:");

    for (int i = 0; i < order.getProducts().size(); i++) {
        System.out.println((i + 1) + ": " + order.getProducts().get(i));
    }

    int productIndex = sc.nextInt() - 1;

    boolean success = service.deleteProductFromOrder(order, productIndex);

    if (success) {
        System.out.println("Product deleted successfully.");
    } else {
        System.out.println("Invalid selection or deletion failed. Please try again.");
    }
  }



  public void addProductToOrder(Order order) {
    ProductService productService = new ProductService();
    List<Product> products =  productService.getAll();
    for (int i =0; i < products.size(); i++) {
      System.out.println((i+1) + " " + products.get(i));
    }
    System.out.println("Select the product to add to the order:");
    int productIndex = sc.nextInt(); // index of the product in the list
    System.out.println("What quantity do you want: ");
    int quantity = sc.nextInt();
    Product productFromStock = products.get(productIndex -1); 
    Product productInOrder = null;

    if (quantity > productFromStock.getQuantity()) {
        System.out.println("Sorry! Not enough stock. The stock has only " + productFromStock.getQuantity() + " products");
    } else {
      System.out.println("Product added to the order");
      try {
          productInOrder = (Product) productFromStock.clone(); // creating a copy of the object
          productInOrder.setQuantity(quantity);
      } catch (CloneNotSupportedException e) {
          e.printStackTrace();
      }
      order.getProducts().add(productInOrder); // pass the copy of the object not the original object 
      order.setDescription(order.getDescription() + productInOrder.getName() + " ");
      order.setTotal(order.getTotal() + (float) productInOrder.getPrice() * quantity);
      order.setDate(LocalDateTime.now());
      service.updateProductsInOrder(order, productInOrder);
      order.update();
      service.update(order);
      productFromStock.setQuantity(productFromStock.getQuantity() - quantity);
      productService.update(productFromStock);
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
    // sc.nextLine();

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
    System.out.println("Select the product to be updated:");
    int i = 1; 
    for (Product p : order.getProducts()) {
      System.out.println(i++ + ": " + p);
    }

    int productIndex = sc.nextInt();
    Product productFromOrder = order.getProducts().get(productIndex - 1);
    ProductService productService = new ProductService();
    Product productFromStock = productService.get(productFromOrder.getId());
    System.out.println("What quantity do you want: ");
    int quantityFromUser = sc.nextInt();

    if (quantityFromUser == 0) { // remove
      System.out.println("Delete the product from the order and return the product to the stock");
      productFromStock.setQuantity(productFromStock.getQuantity() + productFromOrder.getQuantity());
      productFromOrder.setQuantity(0);
      productService.update(productFromStock);
      order.getProducts().remove(productIndex - 1);
      order.update();
      // service.deleteProductFromOrder(order.getId(), productFromOrder.getId()); needs fix !
    } else if (quantityFromUser > productFromOrder.getQuantity()) {  // increase
        int difference = quantityFromUser - productFromOrder.getQuantity();
        if (productFromStock.getQuantity() >= difference) {
          productFromOrder.setQuantity(quantityFromUser);
          productFromStock.setQuantity(productFromStock.getQuantity() - difference);
          productService.update(productFromStock);
          order.update();
          service.update(order);
          System.out.println("The new quantity of the order is: " + productFromOrder.getQuantity());
        } else {
          System.out.println("Sorry! Not enough stock. The stock has only " + productFromStock.getQuantity() + " products");
        }
    } else { // decrease
      int difference = productFromOrder.getQuantity() - quantityFromUser;
      productFromOrder.setQuantity(quantityFromUser);
      productFromStock.setQuantity(productFromStock.getQuantity() + difference);
      order.update();
      service.update(order);
      productService.update(productFromStock);
      System.out.println("The new quantity of the product in the order is: " + productFromOrder.getQuantity());
    }
  }
}


// FE doesn't need to know what's updated
// BE that needs to handle the update logic
// add, delete, update products in order

