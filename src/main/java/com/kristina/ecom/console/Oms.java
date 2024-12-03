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


  // public void addProductToOrder(Order order) {
  //   ProductService productService = new ProductService();
  //   List<Product> products =  productService.getAll();
  //   for (int i =0; i < products.size(); i++) {
  //     System.out.println((i+1) + " " + products.get(i));
  //   }
  //   System.out.println("Select the product to add to the order:");
  //   int productIndex = sc.nextInt(); // index of the product in the list
  //   System.out.println("What quantity do you want: ");
  //   int quantity = sc.nextInt();
  //   Product productFromStock = products.get(productIndex -1); 
  //   Product productInOrder = null;

  //   if (quantity > productFromStock.getQuantity()) {
  //       System.out.println("Sorry! Not enough stock. The stock has only " + productFromStock.getQuantity() + " products");
  //   } else {
  //     System.out.println("Product added to the order");
  //     try {
  //         productInOrder = (Product) productFromStock.clone(); // creating a copy of the object
  //         productInOrder.setQuantity(quantity);
  //     } catch (CloneNotSupportedException e) {
  //         e.printStackTrace();
  //     }
  //     order.getProducts().add(productInOrder); // pass the copy of the object not the original object 
  //     order.setDescription(order.getDescription() + productInOrder.getName() + " ");
  //     order.setTotal(order.getTotal() + (float) productInOrder.getPrice() * quantity);
  //     order.setDate(LocalDateTime.now());
  //     service.updateProductsInOrder(order, productInOrder);
  //     order.update();
  //     service.update(order);
  //     productFromStock.setQuantity(productFromStock.getQuantity() - quantity);
  //     productService.update(productFromStock);
  //   }
  // }

  public void addProductToOrder(Order order) {
    // Fetch all available products
    ProductService productService = new ProductService();
    List<Product> products = productService.getAll();
    // Display the available products
    System.out.println("Available products:");
    for (int i = 0; i < products.size(); i++) {
        System.out.println((i + 1) + ": " + products.get(i));
    }
    // Take user input for product selection and quantity
    System.out.println("Select the product to add to the order:");
    int productIndex = sc.nextInt() - 1;

    if (productIndex < 0 || productIndex >= products.size()) {
        System.out.println("Invalid product selection.");
        return;
    }
    Product selectedProduct = products.get(productIndex);
    System.out.println("Enter the quantity:");
    int quantity = sc.nextInt();
    // Delegate the addition to the service
    boolean success = service.addProductToOrder(order, selectedProduct.getId(), quantity);
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
    System.out.println("Select the product to update:");
    // Display products
    for (int i = 0; i < order.getProducts().size(); i++) {
        System.out.println((i + 1) + ": " + order.getProducts().get(i));
    }
    // Get user input
    int productIndex = sc.nextInt() - 1;
    System.out.println("Enter the new quantity (0 to remove):");
    int newQuantity = sc.nextInt();
    // Delegate the update to the service
    boolean success = service.updateProductInOrder(order, productIndex, newQuantity);
    // Display result
    if (success) {
        System.out.println("Product updated successfully.");
    } else {
        System.out.println("Update failed. Please check stock or input validity.");
    }
  }
}
