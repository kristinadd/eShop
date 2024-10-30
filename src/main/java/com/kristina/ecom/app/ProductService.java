package com.kristina.ecom.app;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.kristina.ecom.res.DAO;
import java.sql.SQLException;

public class ProductService {
  private DAO<Integer, Product> dao;

  public ProductService() {
    dao = new ProductDAOMySql();
  }

  public List<Product> getAll() {
    List<Product> products = new ArrayList<>();
    try {
      products = dao.readAll()
      .stream()
      .filter(product -> product.getType()
      .equals("Component"))
      .collect(Collectors.toList());


    } catch (SQLException ex) {
      ex.printStackTrace();
    }

    return products;
  }

  public Product getComputer() {
    Product product = null;
    try {
      product = dao.readAll().stream()
            .filter(p -> p.getType()
            .equals("Compute"))
            .findFirst().orElse(null);
    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    return product;
  }

  public Product get(int id) {
    Product product = null;
    try {
      product = dao.read(id);
    } catch (SQLException ex) {
      ex.printStackTrace();
    }

    return product;
  }

  public int delete(int id) {
    int rows = 0;
    try {
      rows = dao.delete(id);
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    return rows;
  }

  public int create(Product product) {
    int rows  = 0;
    try {
      rows = dao.create(product);
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    return rows;
  }

  public int update (Product product) {
    int rows = 0;
    try {
      rows = dao.update(product);
    } catch (SQLException ex) {
      ex.printStackTrace();
    }

    return rows;
  }
}
