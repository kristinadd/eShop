package com.kristina.ecom.ui;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kristina.ecom.app.OrderService;

@RestController
@RequestMapping("ecom/order")
public class OrderAPI {
  private OrderService service = new OrderService();

  public OrderAPI() {
    service = new OrderService();
  }

  @GetMapping() {
    
  }
}
