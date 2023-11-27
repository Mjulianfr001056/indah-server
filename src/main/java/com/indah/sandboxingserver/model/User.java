package com.indah.sandboxingserver.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private String id;
    private String nama;
    private String email;
    private Role role;
}