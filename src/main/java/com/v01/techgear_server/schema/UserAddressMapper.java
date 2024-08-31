package com.v01.techgear_server.schema;

import com.v01.techgear_server.generated.UserAddressSchema;
import com.v01.techgear_server.model.UserAddress;

public class UserAddressMapper {
    public static UserAddressSchema mapToUserAddressSchema(UserAddress address) {
        return UserAddressSchema.newBuilder()
                .setAddressId(address.getAddressId())
                .setCountry(address.getCountry())
                .setLatitude(address.getLatitude())
                .setLongitude(address.getLongitude())
                .setAddressDetails(address.getAddressDetails())
                .build();
    }
}