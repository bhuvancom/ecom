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
    private String userEmail;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String userPassword;
    private String address;
}
