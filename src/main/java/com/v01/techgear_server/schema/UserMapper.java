package com.v01.techgear_server.schema;

import java.util.stream.Collectors;

import com.v01.techgear_server.generated.UserSchema;
import com.v01.techgear_server.model.Review;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.model.UserPhoneNo;

public class UserMapper {
    
    public static UserSchema mapToUserSchema(User user) {

        return UserSchema.newBuilder()
                .setUserId(user.getUserId())
                .setUserName(user.getUsername())
                .setPassword(user.getPassword())
                .setPhoneNumbers(
                        user.getPhoneNumbers().stream().map(UserPhoneNo::toString).collect(Collectors.toList()))
                .setAddresses(UserAddressMapper.mapToUserAddressSchema(user.getAddresses()))
                .setPasswordHistory(user.getPasswordHistory())
                .setReviews(user.getReviews().stream().map(Review::toString).collect(Collectors.toList()))
                .setRoles((RolesMapper.mapToRolesSchema(user.getRoles())))
                .build();
    }
}
