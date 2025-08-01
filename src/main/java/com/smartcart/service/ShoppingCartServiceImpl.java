package com.smartcart.service;

import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import com.smartcart.entity.Cart;
import com.smartcart.entity.CartData;
import com.smartcart.repository.ShoppingCartRepo;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepo shoppingCartRepo;

    public List<CartData> getCartItems() {
        List<CartData> cartData = new ArrayList<>(List.of());
        List<Cart> carts = shoppingCartRepo.findAll();
        carts.forEach(cart -> {
            CartData data = new CartData();
            data.setCartItemId(cart.getCartData().getCartItemId());
            data.setItemName(cart.getCartData().getItemName());
            data.setUrl(cart.getCartData().getUrl());
            data.setTotalPrice(cart.getCartData().getTotalPrice());
            cartData.add(data);
        });
        return cartData;
    }

    @Override
    public String createCartItems(List<Cart> cartList) {
        for (Cart cartItem : cartList) {
            String cartId = cartItem.getUserDetails().getMobileNumber();
            String cartItemId = UUID.randomUUID().toString();
            cartItem.getCartData().setCartItemId(cartItemId);
            cartItem.setCartId(cartId);
            cartItem.getCartData().setCounter(1);
            shoppingCartRepo.save(cartItem);
        }
        return "Cart created successfully with ID: ";
    }
}
