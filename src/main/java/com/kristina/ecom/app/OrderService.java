package com.kristina.ecom.app;

import java.util.ArrayList;
import java.util.List;

import com.kristina.ecom.res.DAO;

import java.sql.SQLException;

public class OrderService {
  // this can be a ProductDAQMySql
  private DAO<String, Order> dao;
  private DAO<Integer, Product> daoP;

  public OrderService() {
    dao = new OrderDAOMySql();
    daoP = new ProductDAOMySql();
    // dao = new ProductDAOPostgress();
    // I can just switch the dtabase in here and the 
    // rest of the code will work due to the interface ProductDAO
  }
  public int create(Order order) {
    int rows  = 0;
    try {
      // insert order
      rows = dao.create(order);

      // update product stock
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
    // use the product service to have only one 
      Order order = dao.read(id);
      order.getProducts().forEach(p -> {
        ProductService stock = new ProductService();
        Product product = stock.get(p.getId());
        product.setQuantity(product.getQuantity() + p.getQuantity());
      });
    } catch ( SQLException ex) {
      ex.printStackTrace();
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
}
