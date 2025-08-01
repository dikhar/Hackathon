package com.smartcart.controller;

import com.smartcart.entity.Cart;
import com.smartcart.entity.CartData;
import com.smartcart.service.AiRecommendationService;
import com.smartcart.service.ShoppingCartServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import static com.smartcart.commonConstant.CommonConstant.SC_POST_URL;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Component
@RequiredArgsConstructor
public class ShoppingCartControllerImpl implements ShoppingCartController {

    private final AiRecommendationService aiRecommendationService;
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
