package com.smartcart.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.smartcart.entity.Cart;

public interface ShoppingCartRepo extends MongoRepository<Cart, String> {
}
