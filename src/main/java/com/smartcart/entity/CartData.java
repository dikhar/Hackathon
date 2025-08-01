package com.smartcart.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartData {
    private String cartItemId;
    private String itemName;
    private Integer counter;
    private String url;
    private Integer totalPrice;
}
