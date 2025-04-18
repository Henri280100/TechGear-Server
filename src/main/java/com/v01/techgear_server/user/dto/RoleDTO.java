package com.v01.techgear_server.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Role Data Transfer Object")
public class RoleDTO {
    @Schema(description = "Role ID", example = "1")
    private Integer roleId;

    @Schema(description = "Role name", example = "ROLE_USER")
    private String roleName;

}
