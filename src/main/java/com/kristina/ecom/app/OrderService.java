package com.kristina.ecom.app;

import java.util.ArrayList;
import java.util.List;
import com.kristina.ecom.res.DAO;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class OrderService {
  private DAO<String, Order> dao; // interface
  private DAO<Integer, Product> daoP;

  public OrderService() {
    dao = new OrderDAOMySql();
    daoP = new ProductDAOMySql();
  }

  public int updateProductsInOrder(Order order, Product product) {
    int rows = 0;
    try {
      rows = ((OrderDAOMySql)dao).updateProductsInOrder(order, product);
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    return rows;
  }

  public int create(Order order) {
    int rows  = 0;
    try {
      rows = dao.create(order);
      Product stock;
      for (Product p : order.getProducts()) {
        stock = daoP.read(p.getId());
        stock.setQuantity(stock.getQuantity() - p.getQuantity());
        daoP.update(stock);
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    }

    return rows;
  }

  public List<Order> getAll() {
    List<Order> orders = new ArrayList<>();
    try {
      orders = dao.readAll();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }

    return orders;
  }

  public Order get(String id) {
    Order order = null;
    try {
      order = dao.read(id);
    } catch (SQLException ex) {
      ex.printStackTrace();
    }

    return order;
  }

  public int delete(String id) {
    int rows = 0;
    try {
      rows = dao.delete(id);
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    return rows;
  }

  public int cancel(String id) {
    int rows = 0;
    try {
      ProductService pService  = new ProductService();
      dao.read(id).getProducts().forEach(p -> {
        Product product = pService.get(p.getId());
        product.setQuantity(product.getQuantity() + p.getQuantity());
        pService.update(product);
      });

      rows = dao.delete(id);
    } catch ( SQLException ex) {
      System.out.println("Error cancelling the order");
    }
    return rows;
  }

  public int update (Order order) {
    int rows = 0;
    try {
      rows = dao.update(order);
    } catch (SQLException ex) {
      ex.printStackTrace();
    }

    return rows;
  }

  public boolean updateProductInOrder(Order order) { // new order
    try {
        // if used only once, don't declare it 
        Order oldOrder = dao.read(order.getId());
        Product  productFromStock;
        int difference;
        
        if (order.getProducts().size() == oldOrder.getProducts().size()) {
          for (Product product : order.getProducts()) {
            difference = product.getQuantity() - getProductQuantityById(oldOrder.getProducts(), product.getId());
            productFromStock = daoP.read(product.getId());
            productFromStock.setQuantity(productFromStock.getQuantity() - difference);
            daoP.update(productFromStock); // Update stock
          }
      }
        // Update the order in the database
        order.update();
        dao.update(order); // Persist changes to the database
        return true; // Success
    } catch (SQLException ex) {
        ex.printStackTrace();
        return false; // Failure
    }
  }

    public boolean addProductToOrder(Order order, int productId, int quantity) {
    try {
        // Fetch the product from stock
        Product productFromStock = daoP.read(productId);
        if (productFromStock == null) {
            System.out.println("Product not found.");
            return false;
        }
        // Check if the requested quantity is available
        if (quantity > productFromStock.getQuantity()) {
            System.out.println("Not enough stock available.");
            return false;
        }
        // Clone the product to create a copy for the order
        Product productInOrder = (Product) productFromStock.clone();
        productInOrder.setQuantity(quantity);
        // Add the product to the order
        order.getProducts().add(productInOrder);
        // Update the stock in the database
        productFromStock.setQuantity(productFromStock.getQuantity() - quantity);
        daoP.update(productFromStock);
        // Update the order in the database
        order.update();
        dao.update(order);
        return true; // Product added successfully
    } catch (CloneNotSupportedException | SQLException e) {
        e.printStackTrace();
        return false; // Failure in adding the product
    }
  }

  // get the product quantity by its id in the oder 
  private int getProductQuantityById(List<Product> products, int id){
    for (Product p : products) 
      if (p.getId() == id)
        return p.getQuantity();

    return 0; 
  } 
}
