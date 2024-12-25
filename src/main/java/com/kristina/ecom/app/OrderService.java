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

  public boolean update(Order order) {
    try {
        Order oldOrder = dao.read(order.getId());
        Product  productFromStock;
        int difference;
        List<Product> oldProducts = oldOrder.getProducts();
        List<Product> newOrderProducts = order.getProducts();

        // update existing product
      if (order.getProducts().size() == oldOrder.getProducts().size()) {
        for (Product product : order.getProducts()) {
          difference = product.getQuantity() - getProductQuantityById(oldOrder.getProducts(), product.getId());
          productFromStock = daoP.read(product.getId());
          productFromStock.setQuantity(productFromStock.getQuantity() - difference);
          daoP.update(productFromStock);
        }
      } else if (order.getProducts().size() < oldOrder.getProducts().size()) {
        // delete product from order
        // increase the stock
        for (Product product : oldProducts) {
         if (!newOrderProducts.contains(product)) {
          productFromStock = daoP.read(product.getId());
          productFromStock.setQuantity(productFromStock.getQuantity() + product.getQuantity());
          daoP.update(productFromStock);
         }
        }
      } else  if (order.getProducts().size() > oldOrder.getProducts().size()){
        // add new product to the order
        // need to decrease the stock
        for (Product product : newOrderProducts) {
          if (!oldProducts.contains(product)) {
           productFromStock = daoP.read(product.getId());
           productFromStock.setQuantity(productFromStock.getQuantity() - product.getQuantity());
           daoP.update(productFromStock);
          }
         }
      } else {
        System.out.println("Something went wrong and no condition matched");
      }
      dao.update(order);
      return true;
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
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
