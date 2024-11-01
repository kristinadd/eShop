package com.kristina.ecom.app;

import javax.sql.DataSource;
import com.kristina.ecom.res.DAO;
import com.kristina.ecom.res.DataSourceFactory;
import java.util.List;
import java.util.stream.Collectors;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.Timestamp;


public class OrderDAOMySql implements DAO<String, Order> {

  private DataSource datasource;

  public OrderDAOMySql() {
    this.datasource = DataSourceFactory.instance().getDataSource();
  }

  @Override public int create(Order order) throws SQLException {
      Connection conn = datasource.getConnection();
      String query = "INSERT INTO porder VALUES(? ,?, ?, ?)";
      String query2 = "INSERT INTO orderDetails VALUES(?, ?, ?)";
  
      conn.setAutoCommit(false);

      PreparedStatement stat = conn.prepareStatement(query);
      stat.setString(1, order.getId());
      stat.setString(2, order.getDescription());
      stat.setFloat(3, order.getPrice());
      stat.setTimestamp(4,  Timestamp.valueOf(order.getDate()));
      int rows =  stat.executeUpdate();

      stat = conn.prepareStatement(query2);
      for (Product product : order.getProducts()) {
        stat.setString(1, order.getId());
        stat.setInt(2, product.getId());
        stat.setInt(3, product.getQuantity());
        stat.executeUpdate();
      }

      conn.commit();
      conn.close();
  
      return rows;
    }

    @Override public List<Order> readAll() throws SQLException {
      List<Order> orders = new ArrayList<>();
      Connection connection = datasource.getConnection();
      String query = "SELECT * FROM porder";
      Statement stat = connection.createStatement();
      ResultSet rs = stat.executeQuery(query);
      while (rs.next()) {
        Order order = new Order(rs.getString(1), 
                                rs.getString(2), 
                                rs.getFloat(3), 
                                rs.getTimestamp(4).toLocalDateTime(),
                                 new ArrayList<Product>()
                              );
        orders.add(order);
      }

      connection.close();
      return orders;
    }

    @Override
    public Order read(String id) throws SQLException {
        Order order = null;
        String orderQuery = "SELECT * FROM porder WHERE id = ?";
        String productsQuery = "SELECT product.id, product.name, product.price, orderDetails.quantity " +
                               "FROM orderDetails " +
                               "JOIN product ON orderDetails.pid = product.id " +
                               "WHERE orderDetails.oid = ?";
        
        Connection conn = datasource.getConnection();
        try {
            conn.setAutoCommit(false);
            PreparedStatement orderStmt = conn.prepareStatement(orderQuery);
            orderStmt.setString(1, id);
            ResultSet orderRs = orderStmt.executeQuery();
            
            if (orderRs.next()) {
                order = new Order(
                    orderRs.getString("id"), 
                    orderRs.getString("description"), 
                    orderRs.getFloat("total"), 
                    orderRs.getTimestamp("date_time").toLocalDateTime(), 
                    new ArrayList<>()
                );
            }
            
            if (order != null) {
                PreparedStatement productsStmt = conn.prepareStatement(productsQuery);
                productsStmt.setString(1, id);
                ResultSet productsRs = productsStmt.executeQuery();
                
                List<Product> products = new ArrayList<>();
                while (productsRs.next()) {
                    Product product = new Product(
                        productsRs.getInt("id"), 
                        productsRs.getString("name"), 
                        productsRs.getFloat("price"), 
                        productsRs.getInt("quantity")
                    );
                    products.add(product);
                }
                order.setProducts(products);
            }
    
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
        
        return order;
    }
    
    @Override public int delete(String id) throws SQLException{
      int rows = 0;
      Connection conn = datasource.getConnection();
      conn.setAutoCommit(false);
      String query1 = "DELETE FROM orderDetails WHERE oid=" + id;

      Statement stat = conn.createStatement();
      int rows1 = stat.executeUpdate(query1);

      rows = rows1;
      String query2 = "DELETE FROM porder WHERE id=" + id;
      int rows2 = stat.executeUpdate(query2);
      rows += rows2;

      conn.commit();
      conn.close();
      return rows;
    }

    // this delete is for specific item in the orderDetails table
    public int delete(String oid, int pid) throws SQLException {
      int rows = 0;
      Connection conn = datasource.getConnection();
      String query = "DELETE FROM orderDetails WHERE oid=? AND pid=?";

      PreparedStatement stat = conn.prepareStatement(query);
      stat.setString(1, oid); 
      stat.setInt(2, pid); 
      rows = stat.executeUpdate();

      conn.close();
      return rows;
    }

  public int update(Order order) throws SQLException {
    String orderDetailsQuery = "UPDATE orderDetails SET quantity=? WHERE oid=? AND pid=?";
    String orderQuery = "UPDATE porder SET description=?, total=?, date_time=? WHERE id=?";
    int rows = 0;

    Connection conn = datasource.getConnection();
    conn.setAutoCommit(false);

    // OrderDetails table
    PreparedStatement stat = conn.prepareStatement(orderDetailsQuery);
    for (Product product : order.getProducts()) {
      stat.setInt(3, product.getId());
      stat.setString(2, order.getId());
      stat.setInt(1, product.getQuantity());
      stat.executeUpdate();
    }

    // Order table
    stat = conn.prepareStatement(orderQuery);
    stat.setString(1,order.getDescription());
    stat.setFloat(2, order.getPrice());
    stat.setTimestamp(3, Timestamp.valueOf(order.getDate()));
    stat.setString(4, order.getId());
    stat.executeUpdate();
    rows = stat.executeUpdate();

    conn.commit();
    conn.close();
    return rows;
  }
}
