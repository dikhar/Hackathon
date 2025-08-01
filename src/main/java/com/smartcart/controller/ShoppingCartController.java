package com.smartcart.controller;

import com.smartcart.entity.Cart;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.smartcart.commonConstant.CommonConstant.Main_URL;
import static com.smartcart.commonConstant.CommonConstant.SC_GET_URL;
import static com.smartcart.commonConstant.CommonConstant.SC_POST_URL;

@RestController
@RequestMapping(Main_URL)
public interface ShoppingCartController {
    @GetMapping(SC_GET_URL)
    List<Cart> getShoppingCart();

    @PostMapping(SC_POST_URL)
    String createShoppingCart(@RequestBody Cart cart);
}
