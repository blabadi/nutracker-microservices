package com.basharallabadi.nutracker.identity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

//    @Autowired
//    UserRepoApi userRepo;

    public Optional<User> getByName(String name) {
        User u = User.builder()
                .id("12345678abc")
                .email("bashar@bashar.com")
                .name("bashar")
                .active(true)
                .createdAt(new Date())
                .roles(Set.of("USER"))
                .build();
        return Optional.of(u);
    }

    public void createUser(CreateCommand user) {
        //check existing
        Optional<User> existing = this.getByName(user.getName());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("user name is used");
        }

        // set attributes
        User toCreate = User.builder()
            .email(user.getEmail())
            .name(user.getName())
            .active(true)
            .createdAt(new Date())
            .roles(Set.of("USER"))
            .password(new BCryptPasswordEncoder().encode(user.getPassword()))
            .build();
    }

    public void updateProfile(String username, Profile profile) {
//        userRepo.updateProfile(username, profile);
    }

    public Optional<User> getFullInfo(String username) {
       return this.getByName(username);
    }
}