package com.v01.techgear_server.model;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "userPhoneNos")
public class UserPhoneNo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Size(max = 10)
    @Column(name = "phoneNo")
    private String phoneNo;

    @Column(name = "countryCode")
    private String countryCode;

    @OneToOne
    @JoinColumn(name="user_id")
    private User users;

}
