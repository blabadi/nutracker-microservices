package com.basharallabadi.nutracker.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping(value="/{name}", method = RequestMethod.GET)
    public User login(@PathVariable String name){
        return userService.getByName(name).orElseThrow(() -> new RuntimeException("not found"));
    }

    @RequestMapping(value="/", method = RequestMethod.POST)
    public void register(@RequestBody CreateCommand u){
        this.userService.createUser(u);
    }

    @RequestMapping(value="/{name}/profile", method = RequestMethod.PUT)
    public void updateProfile(@RequestBody Profile profile, User activeUser){
        this.userService.updateProfile(activeUser.getName(), profile);
    }
}