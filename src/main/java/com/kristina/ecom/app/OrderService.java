package com.kristina.ecom.app;

import java.util.ArrayList;
import java.util.List;
import com.kristina.ecom.res.DAO;
import java.sql.SQLException;

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

  public boolean deleteProductFromOrder(Order order, int productIndex) {
    try {
        // Validate product index
        if (productIndex < 0 || productIndex >= order.getProducts().size()) {
            return false; // Invalid product index
        }

        // Get the product to delete
        Product product = order.getProducts().get(productIndex);

        // Remove the product from the order
        order.getProducts().remove(productIndex);

        // Update the order in memory
        order.update();

        // Update the order in the database
        dao.update(order); // Update the order (description, total, etc.)

        // Delete the product from the orderDetails table in the database
        ((OrderDAOMySql) dao).delete(order.getId(), product.getId());

        return true; // Successful deletion
    } catch (SQLException ex) {
        ex.printStackTrace();
        return false; // Indicate failure
    }
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

  public boolean updateProductInOrder(Order order, int productIndex, int newQuantity) {
    try {
        // Validate product index
        if (productIndex < 0 || productIndex >= order.getProducts().size()) {
            return false; // Invalid product index
        }

        Product productFromOrder = order.getProducts().get(productIndex);
        Product productFromStock = daoP.read(productFromOrder.getId()); // Fetch product from stock

        if (newQuantity == 0) { // Remove product from the order
            productFromStock.setQuantity(productFromStock.getQuantity() + productFromOrder.getQuantity());
            daoP.update(productFromStock); // Update stock
            order.getProducts().remove(productIndex);
        } else if (newQuantity > productFromOrder.getQuantity()) { // Increase quantity
            int difference = newQuantity - productFromOrder.getQuantity();
            if (productFromStock.getQuantity() >= difference) {
                productFromOrder.setQuantity(newQuantity);
                productFromStock.setQuantity(productFromStock.getQuantity() - difference);
                daoP.update(productFromStock); // Update stock
            } else {
                return false; // Not enough stock
            }
        } else { // Decrease quantity
            int difference = productFromOrder.getQuantity() - newQuantity;
            productFromOrder.setQuantity(newQuantity);
            productFromStock.setQuantity(productFromStock.getQuantity() + difference);
            daoP.update(productFromStock); // Update stock
        }

        // Update the order in the database
        dao.update(order); // Persist changes to the database
        return true; // Success
    } catch (SQLException ex) {
        ex.printStackTrace();
        return false; // Failure
    }
  }
}
