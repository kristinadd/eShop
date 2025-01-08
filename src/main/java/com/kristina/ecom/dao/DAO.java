package com.kristina.ecom.dao;

import java.sql.SQLException;
import java.util.List;

public interface DAO<K, V> {
  int create(V v) throws SQLException;
  List<V> readAll() throws SQLException;
  V read(K k) throws SQLException;
  int update(V v) throws SQLException;
  int delete(K k) throws SQLException;
}
