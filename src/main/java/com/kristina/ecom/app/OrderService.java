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

  // // handilng order
  // public int update (Order order) {
  //   int rows = 0;
  //   try {
  //     rows = dao.update(order);
  //   } catch (SQLException ex) {
  //     ex.printStackTrace();
  //   }

  //   return rows;
  // }
  // handling the products in the order
  public boolean update(Order order) {
    try {
      // Update order
        dao.update(order);

        // Update stock
        Order oldOrder = dao.read(order.getId()); // if used only once, don't declare it 
        Product  productFromStock;
        int difference;
        
      if (order.getProducts().size() == oldOrder.getProducts().size()) {
        for (Product product : order.getProducts()) {
          difference = product.getQuantity() - getProductQuantityById(oldOrder.getProducts(), product.getId());
          productFromStock = daoP.read(product.getId());
          productFromStock.setQuantity(productFromStock.getQuantity() - difference);
          daoP.update(productFromStock);
        }
      } else {
        Order originalOrder = get(order.getId());
        List<Product> originalProducts = originalOrder.getProducts();

        List<Product> newProducts = order.getProducts();

        for (Product product : originalProducts) {
          if (newProducts.indexOf(product) == -1) { // the product is not in there
            int oldQuantity = product.getQuantity();
            // just return the quantity to stock from the original product
            // ... need to finish
          }
        }
      }
      return true;
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
  }

  // public int updateProductsInOrder(Order order, Product product) {
  //   int rows = 0;
  //   try {
  //     rows = ((OrderDAOMySql)dao).updateProductsInOrder(order, product);
  //   } catch (SQLException ex) {
  //     ex.printStackTrace();
  //   }
  //   return rows;
  // }

  // // my version 
  // public boolean addProductToOrder(Order order) {
  //   try {
  //       order.update();
  //       dao.update(order);
  //       return true;
  //   } catch (SQLException e) {
  //       e.printStackTrace();
  //       return false; 
  //   }
  // }

  // get the product quantity by its id in the oder 
  private int getProductQuantityById(List<Product> products, int id){
    for (Product p : products) 
      if (p.getId() == id)
        return p.getQuantity();

    return 0; 
  } 
}
