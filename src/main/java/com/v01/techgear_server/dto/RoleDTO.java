package com.v01.techgear_server.dto;

import com.v01.techgear_server.enums.Roles;

@lombok.Data
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class RoleDTO {
    private Integer id;
    private Roles roles;
}
