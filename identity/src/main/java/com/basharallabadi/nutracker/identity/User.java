package com.basharallabadi.nutracker.identity;


import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
public class User {
    private String id;
    private String name;
    private String email;
    private String password;
    private boolean active;
    private Date createdAt;
    private Profile profile;
    private Set<String> roles = new HashSet<>();
}