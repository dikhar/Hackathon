package com.smartcart.service;

import com.smartcart.entity.Cart;
import com.smartcart.entity.CartData;
import java.util.List;

public interface ShoppingCartService {

    List<CartData> getCartItems();
    String createCartItems(List<Cart> cartList);
}
