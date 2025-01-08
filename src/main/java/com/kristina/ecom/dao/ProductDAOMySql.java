package com.kristina.ecom.dao;

import javax.sql.DataSource;

import com.kristina.ecom.domain.Product;

import java.util.List;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.PreparedStatement;

public class ProductDAOMySql implements DAO<Integer, Product> {

  private DataSource datasource;

  public ProductDAOMySql() {
    this.datasource = DataSourceFactory.instance().getDataSource();
  }

  @Override
  public int create(Product product) throws SQLException {
    Connection conn = datasource.getConnection();
    String query = "INSERT INTO product (type, name, price, quantity, image) VALUES(? ,?, ?, ?, ?)";

    PreparedStatement stat = conn.prepareStatement(query);
    stat.setString(1, product.getType());
    stat.setString(2, product.getName());
    stat.setDouble(3, product.getPrice());
    stat.setInt(4, product.getQuantity());
    stat.setString(5, product.getImg());
    int rows =  stat.executeUpdate();

    conn.close();
    return rows;
  }

  @Override
  public List<Product> readAll() throws SQLException{
    List<Product> products = new ArrayList<>();
    Connection conn = datasource.getConnection();

    String query = "SELECT * FROM product";
    Statement stat = conn.createStatement();
    ResultSet rs = stat.executeQuery(query);
      while (rs.next()) {
        Product product = new Product(rs.getInt(1), 
                                      rs.getString(2), 
                                      rs.getString(3), 
                                      rs.getDouble(4), 
                                      rs.getInt(5), 
                                      rs.getString(6)
                                      );
        products.add(product);
      }

      conn.close();
      return products;
  }

  @Override
  public Product read(Integer id) throws SQLException{
    Product product = null;

    String query = "SELECT * FROM product WHERE id=" + id;
    Connection conn = datasource.getConnection();
    Statement stat = conn.createStatement();
    ResultSet rs = stat.executeQuery(query);
    if (rs.next())
      product = new Product(rs.getInt(1), 
                            rs.getString(2), 
                            rs.getString(3), 
                            rs.getDouble(4), 
                            rs.getInt(5), 
                            rs.getString(6)
                            );
    return product;
  }

  @Override
  public int update(Product product) throws SQLException {
    String query = "UPDATE product SET type=?, name=?, price=?, quantity=?, image=? WHERE id=?";
    int rows = 0;

    Connection conn = datasource.getConnection();
    PreparedStatement stat = conn.prepareStatement(query);
    stat.setString(1,product.getType());
    stat.setString(2,product.getName());
    stat.setDouble(3, product.getPrice());
    stat.setInt(4, product.getQuantity());
    stat.setString(5, product.getImg());
    stat.setInt(6, product.getId());
    stat.executeUpdate();
    rows = stat.executeUpdate();

    conn.close();
    return rows;
  }

  @Override
  public int delete(Integer id) throws SQLException {
    String query = "DELETE FROM product WHERE id=" + id;
    int rows = 0;

    Connection conn = datasource.getConnection();
    Statement stat = conn.createStatement();
    rows = stat.executeUpdate(query);

    conn.close();
    return rows;
  }
}
