package com.v01.techgear_server.dto;

import lombok.Data;

@Data
public class UserAddressDTO {
    private Long addressId;
    private String country;
    private String addressDetails;
}
