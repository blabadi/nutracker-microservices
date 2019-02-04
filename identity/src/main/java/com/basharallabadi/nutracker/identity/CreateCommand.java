package com.basharallabadi.nutracker.identity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommand {
    private String name;
    private String email;
    private String password;
    private List<String> roles;
}
