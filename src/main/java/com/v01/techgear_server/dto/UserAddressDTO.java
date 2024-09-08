package com.v01.techgear_server.dto;

import lombok.Data;

@Data
public class UserAddressDTO {
    private Long addressId;
    private String country;
    private Double latitude;
    private Double longitude;
    private String addressDetails;
}
