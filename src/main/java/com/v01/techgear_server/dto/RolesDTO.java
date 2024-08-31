package com.v01.techgear_server.dto;

import com.v01.techgear_server.enums.Roles;

import lombok.Data;

@Data
public class RolesDTO {
    private Long id;
    private Roles roleType;
}
