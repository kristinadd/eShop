package com.kristina.ecom.ui;
import com.kristina.ecom.app.Product;
import com.kristina.ecom.app.ProductService;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController // annotation indicates that this class is a RESTful controller that handles HTTP requests and returns data directly in the HTTP response.
@RequestMapping("ecom/product") // annotation sets the base path for all the endpoints in this controller
public class ProductAPI {
  private ProductService service = new ProductService();

  public ProductAPI() {
    service = new ProductService();
  }


  @GetMapping(value="/getall", produces="application/json")
  public List<Product> getAll() {
    return service.getAll();
  }

  @GetMapping(value="/get/{id}", produces="application/json")
  public Product get(@PathVariable int id) {
    return service.get(id);
  }


  @PostMapping(value="/create", consumes="application/json")
  public int create(@RequestBody Product product) {
    return service.create(product);
  }

  @DeleteMapping(value="/delete/{id}")  // don't need to produce json at all
  public int delete(@PathVariable int id) {
    return service.delete(id);
  }

  @PutMapping(value="/update", produces="application/json")
  public int update (@RequestBody Product product) {
    return service.update(product);
  }

  @GetMapping(value="/getcomputer", produces="application/json")
  public Product getComputer() {
    return service.getComputer();
  }
}
