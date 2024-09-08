package com.v01.techgear_server.dto;

import java.util.Set;

import com.v01.techgear_server.enums.Roles;

import lombok.Data;

@Data
public class RolesDTO {
    private Long id;
    private Roles roleType;
    private Set<UserDTO> users;
}
