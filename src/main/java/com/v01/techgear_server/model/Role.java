package com.v01.techgear_server.model;

import java.util.HashSet;
import java.util.Set;

import com.v01.techgear_server.enums.Roles;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "There is no role in db, please check again")
    @Enumerated(EnumType.STRING)
    @Column(length=20)
    private Roles roleType;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
}
