package com.smartcart.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetails {
    private Long id;
    private String customerName;
    private String mobileNumber;
    private String email;
    private String address;
}
