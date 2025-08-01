package com.smartcart.service;

import com.smartcart.entity.Cart;
import java.util.List;

public interface ShoppingCartService {

    List<Cart> getCartItems();
    String createCartItems(Cart cart);
}
