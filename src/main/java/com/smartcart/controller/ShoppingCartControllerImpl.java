package com.smartcart.controller;

import com.smartcart.entity.Cart;
import com.smartcart.service.ShoppingCartServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShoppingCartControllerImpl implements ShoppingCartController {

    private final ShoppingCartServiceImpl shoppingCartService;
    @Override
    public List<Cart> getShoppingCart() {
        return shoppingCartService.getCartItems();
    }

    @Override
    public String createShoppingCart( Cart cart) {
        return  shoppingCartService.createCartItems(cart);
    }
}
