package com.smartcart.controller;

import com.smartcart.entity.Cart;
import com.smartcart.entity.CartData;
import com.smartcart.service.ShoppingCartServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShoppingCartControllerImpl implements ShoppingCartController {

    private final ShoppingCartServiceImpl shoppingCartService;
    @Override
    public List<CartData> getShoppingCart() {
        return shoppingCartService.getCartItems();
    }

    @Override
    public String createShoppingCart(List<Cart> cart) {
        return  shoppingCartService.createCartItems(cart);
    }
}
