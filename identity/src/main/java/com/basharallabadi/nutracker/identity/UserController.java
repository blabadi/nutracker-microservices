package com.basharallabadi.nutracker.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping(value="/{name}", method = RequestMethod.GET)
    public Mono<User> login(@PathVariable String name){
        return userService.getByName(name)
                .switchIfEmpty(Mono.error(new NotFound()));
    }

    @PostMapping()
    public Mono<User> register(@RequestBody CreateCommand u){
        return this.userService.createUser(u);
    }

    @RequestMapping(value="/{name}/profile", method = RequestMethod.PUT)
    public void updateProfile(@RequestBody Profile profile, User activeUser){
        this.userService.updateProfile(activeUser.getName(), profile);
    }
}