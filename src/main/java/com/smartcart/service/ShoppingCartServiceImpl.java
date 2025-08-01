package com.smartcart.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.smartcart.entity.Cart;
import com.smartcart.repository.ShoppingCartRepo;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepo shoppingCartRepo;

    public List<Cart> getCartItems() {
        return shoppingCartRepo.findAll();
    }

    @Override
    public String createCartItems(Cart cart) {
        // post implementation
        return "";
    }
}
