package com.bhuvancom.ecom.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Email(regexp = ".*@.*\\..*", message = "Email should be valid")
    @Column(unique = true, nullable = false)
    private String userEmail;
    @Column(nullable = false)
    private String userName;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String userPassword;
    @Column(nullable = false)
    private String address;
}
