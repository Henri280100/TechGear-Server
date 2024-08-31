package com.v01.techgear_server.schema;

import java.util.List;

import com.v01.techgear_server.generated.RolesSchema;
import com.v01.techgear_server.model.Role;

public class RolesMapper {
    public static RolesSchema mapToRolesSchema(List<Role> role) {
        com.v01.techgear_server.generated.Roles avroRoleEnum = com.v01.techgear_server.generated.Roles
                .valueOf(role.get(0).getRoleType().name());

        return RolesSchema.newBuilder()
                .setId(role.get(0).getId())
                .setRoleType(avroRoleEnum)
                .build();

    }
}
