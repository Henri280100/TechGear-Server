package com.v01.techgear_server.dto;

import java.time.LocalDateTime;

import com.v01.techgear_server.enums.AddressTypes;
import com.v01.techgear_server.model.UserAddress;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User Data Transfer Object")
public class UserAddressDTO {
    @Schema(description = "User Address ID", example = "123")
    private Long addressId;

    @Schema(description = "Address Line 1", example = "123 Main Street")
    private String addressLineOne;

    @Schema(description = "Address Line 2", example = "Apt 4B")
    private String addressLineTwo;

    @Schema(description = "City", example = "New York")
    private String city;

    @Schema(description = "State/Province", example = "NY")
    private String stateProvince;
    @Schema(description = "Zip/Postal Code", example = "10001")
    private String zipPostalCode;
    @Schema(description = "Country", example = "USA")
    private String country;

    @Schema(description = "Address Type", example = "HOME")
    private AddressTypes type;

    @Schema(description = "Is Address Primary", example = "true")
    private boolean primaryAddress;

    @Schema(description = "User associated with the address")
    private UserDTO user;
    
    @Schema(description = "Creation Timestamp")
    private LocalDateTime createdAt;
    
    @Schema(description = "Last Update Timestamp")
    private LocalDateTime updatedAt;

    public static UserAddressDTO fromEntity(UserAddress entity) {
        if (entity == null) {
            return null;
        }
        
        return UserAddressDTO.builder()
        .addressId(entity.getAddressId())
        .addressLineOne(entity.getAddressLineOne())
        .addressLineTwo(entity.getAddressLineTwo())
        .city(entity.getCity())
        .stateProvince(entity.getStateProvince())
        .zipPostalCode(entity.getZipPostalCode())
        .country(entity.getCountry())
        .type(entity.getAddressType())
        .primaryAddress(entity.isPrimaryAddress())
        .user(UserDTO.fromEntity(entity.getUser()))
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .build();
    }

    public UserAddress toEntity() {
        UserAddress userAddress = new UserAddress();
        userAddress.setAddressId(this.addressId);
        userAddress.setAddressLineOne(this.addressLineOne);
        userAddress.setAddressLineTwo(this.addressLineTwo);
        userAddress.setCity(this.city);
        userAddress.setStateProvince(this.stateProvince);
        userAddress.setZipPostalCode(this.zipPostalCode);
        userAddress.setCountry(this.country);
        userAddress.setAddressType(this.type);
        userAddress.setPrimaryAddress(this.primaryAddress);
        userAddress.setCreatedAt(this.createdAt);
        userAddress.setUpdatedAt(this.updatedAt);
        
        if (this.user != null) {
            userAddress.setUser(this.getUser().toEntity());
        }
        
        return userAddress;
    }
}
