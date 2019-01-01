package com.basharallabadi.nutracker.authserver;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String id;
    private String name;
    private String email;
    private String password;
    private boolean active;
    private Date createdAt;
    private Set<String> roles = new HashSet<>();
}