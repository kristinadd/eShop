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

        Order originalOrder = dao.read(order.getId());
        Product  productFromStock;
        int difference;
        
      if (order.getProducts().size() == originalOrder.getProducts().size()) {
        for (Product product : order.getProducts()) {
          difference = product.getQuantity() - getProductQuantityById(originalOrder.getProducts(), product.getId());
          productFromStock = daoP.read(product.getId());
          productFromStock.setQuantity(productFromStock.getQuantity() - difference);
          daoP.update(productFromStock);
        }
      } else {
        // Order originalOrder = get(order.getId()); // this would still work but better to use the dao, like above
        List<Product> originalProducts = originalOrder.getProducts();
        List<Product> newProducts = order.getProducts();

        for (Product originalProduct : originalProducts) {
          if (newProducts.contains(originalProduct)) { // Contains uses the equals method to compare objects, falling back to the default implementation if not overridden
            System.out.println("the product is in both orders");
          } else {
            originalProduct.setQuantity(originalProduct.getQuantity() + 3);
            daoP.update(originalProduct); 
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
