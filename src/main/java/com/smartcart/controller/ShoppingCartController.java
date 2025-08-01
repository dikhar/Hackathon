package com.smartcart.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smartcart.entity.Cart;
import com.smartcart.service.ShoppingCartService;

@RestController
@RequestMapping("/api/v1/shopping-cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping("/items")
    public List<Cart> shoppingCart() {
        return shoppingCartService.getCartItems();
    }
}
