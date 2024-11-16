package com.kristina.ecom.console;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import javax.swing.plaf.basic.BasicSplitPaneUI.KeyboardDownRightHandler;
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
        case 4:
          read();
          break;
        case 5:
          update();
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
    if (productIndex < 0 || productIndex >= order.getProducts().size()) {
      System.out.println("Invalid selection. Please try again.");
      return;
    }
    Product product = order.getProducts().get(productIndex);
    order.getProducts().remove(productIndex);
    if (service.delete(order.getId(), product.getId()) > 1 ) {
      System.out.println("Product deleted from the order");
    } else {
      System.out.println("Delete failed");
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
    Product product = products.get(productIndex -1);
    if (quantity > product.getQuantity()) {
        System.out.println("Sorry! Not enough stock. The stock has only " + product.getQuantity() + " products");
    } else {
      System.out.println("Product added to the order");
      try {
          Product productInOrder = (Product) product.clone();
          productInOrder.setQuantity(quantity);
      } catch (CloneNotSupportedException e) {
          e.printStackTrace();
      }
      order.getProducts().add(product); // passed by reference
      order.setDescription(order.getDescription() + product.getName() + " ");
      order.setTotal(order.getTotal() + (float) product.getPrice() * quantity);
      order.setDate(LocalDateTime.now());
      service.updateProductsInOrder(order, product);
      service.update(order); // update the description ...
      // Fix the description 
    }
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

    String datetime = LocalDateTime.now().toString();
    if (!datetime.isEmpty()) {
      order.setDate(LocalDateTime.parse(datetime));
    }

    productUpdate(order);

    if (service.update(order) == 1) {
      System.out.println("Order updated");
    } else {
      System.out.println("Update failed");
    }
  }

  private void productUpdateMenu(Order order) {
    String[] orderUpdateMenu = {
      "1: Delete a product from the order",
      "2: Add a product to the order",
      "3: Update existing product in the order",
      "4: Return to order managment menu"
    };

    System.out.println("\n*** Order Update Menu ***");
    System.out.println(order);
    Arrays.stream(orderUpdateMenu).forEach(System.out::println);
  }

  private void productUpdate(Order order) {
    while (true) {
      productUpdateMenu(order);
      int c = sc.nextInt();

      switch (c) {
        case 1:
          deleteProductFromOrder(order);
          break;
        case 2:
        addProductToOrder(order);
          break;
        case 3:
          updateProducts(order);
          break;
        case 4:
          return;
        default:
          System.out.println("Invalid choice. Please try again.");
      }
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

    if (quantityFromUser == 0) { //to do: only possitive numbers
      System.out.println("Delete the product from the order and return the product to the stock");
      productFromStock.setQuantity(productFromStock.getQuantity() + productFromOrder.getQuantity());
      productFromOrder.setQuantity(0);
      productService.update(productFromStock);
      order.getProducts().remove(productIndex - 1);
      service.delete(order.getId(), productFromOrder.getId());
    } else if (quantityFromUser > productFromOrder.getQuantity()) {
        int difference = quantityFromUser - productFromOrder.getQuantity();
        if (productFromStock.getQuantity() >= difference) {
          productFromOrder.setQuantity(quantityFromUser);
          productFromStock.setQuantity(productFromStock.getQuantity() - difference);
          productService.update(productFromStock);
          System.out.println("The new quantity of the order is: " + productFromOrder.getQuantity());
        } else {
          System.out.println("Sorry! Not enough stock. The stock has only " + productFromStock.getQuantity() + " products");
        }
    } else {
      int difference = productFromOrder.getQuantity() - quantityFromUser;
      productFromOrder.setQuantity(quantityFromUser);
      productFromStock.setQuantity(productFromStock.getQuantity() + difference);
      productService.update(productFromStock);
      System.out.println("The new quantity of the product in the order is: " + productFromOrder.getQuantity());
    }
  }
}
