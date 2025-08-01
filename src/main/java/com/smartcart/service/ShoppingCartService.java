package com.smartcart.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.smartcart.entity.Cart;
import com.smartcart.repository.ShoppingCartRepo;

@Service
public class ShoppingCartService {
    private final ShoppingCartRepo shoppingCartRepo;

    public ShoppingCartService(ShoppingCartRepo shoppingCartRepo) {
        this.shoppingCartRepo = shoppingCartRepo;
    }

    public List<Cart> getCartItems() {
        return shoppingCartRepo.findAll();
    }
}
