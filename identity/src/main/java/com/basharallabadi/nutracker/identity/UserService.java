package com.basharallabadi.nutracker.identity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class UserService {


    private Map<String, User> users = new HashMap<>(Map.of("896494", User.builder()
            .id(String.valueOf(896494L))
            .email("admin@nutracker.com")
            .name("admin")
            .active(true)
            .createdAt(new Date())
            .roles(Set.of("ROLE_ADMIN"))
            .password(new BCryptPasswordEncoder().encode("admin"))
            .build()
    , "123", User.builder()
            .id(String.valueOf(123))
            .email("user1@nutracker.com")
            .name("user1")
            .active(true)
            .createdAt(new Date())
            .roles(Set.of("ROLE_USER"))
            .password(new BCryptPasswordEncoder().encode("pass1234"))
            .build()
        )
    );

    public Mono<User> getByName(String name) {
        return Mono.fromSupplier(() -> users).flatMap(
            (users) ->
                users.entrySet()
                    .stream()
                    .filter((pair) -> pair.getValue().getName().equals(name))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .map(Mono::just)
                    .orElseGet(Mono::empty)
        );
    }

    public Mono<User> createUser(CreateCommand user) {

        User toCreate = User.builder()
                .id(String.valueOf(new Random().nextLong()))
                .email(user.getEmail())
                .name(user.getName())
                .active(true)
                .createdAt(new Date())
                .roles(Set.of(user.getRoles().toArray(new String[0])))
                .password(new BCryptPasswordEncoder().encode(user.getPassword()))
                .build();

        //check existing
        return this.getByName(user.getName())
            // no user with same name
            .switchIfEmpty(Mono.fromCallable(() -> {
                users.put(toCreate.getId(), toCreate);
                return toCreate;
                })
            )
            // a user exists with same name
            .switchIfEmpty(Mono.error(new ConflictException()));

    }

    public void updateProfile(String username, Profile profile) {
//        userRepo.updateProfile(username, profile);
    }

    public Mono<User> getFullInfo(String username) {
       return this.getByName(username);
    }
}