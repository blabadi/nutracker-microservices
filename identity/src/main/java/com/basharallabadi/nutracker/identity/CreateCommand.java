package com.basharallabadi.nutracker.identity;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreateCommand {
    private String name;
    private String email;
    private String password;
    private boolean active;
}
