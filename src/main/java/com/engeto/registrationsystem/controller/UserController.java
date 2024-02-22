package com.engeto.registrationsystem.controller;

import com.engeto.registrationsystem.exception.RegistrationException;
import com.engeto.registrationsystem.model.User;
import com.engeto.registrationsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    UserService userService;


    @PostMapping
        public User createNewUser (@RequestBody User user) throws RegistrationException {
        return userService.createNewUser(user);
    }

    @GetMapping("")
    public String initialScreen() {
        return "Genesis Resources";
    }

    @GetMapping("/user/{id}")
        public User getUserById ( @PathVariable("id") Long id,
                                  @RequestParam(defaultValue = "false") String detail){
        try {
            userService.checkId(id);
        } catch (RegistrationException e) {
            System.err.println("Chyba při zadání ID: " + e.getLocalizedMessage());
        }
        if (detail.equals("true")) {
            return userService.getAllInfoOfUserById(id);
        }
        return userService.getShortInfoOfUserById(id);
    }

    @GetMapping("/users")
        public List<User> getAllUsers(@RequestParam(defaultValue = "false") String detail){
        if (detail.equals("true")) {
            return userService.getAllInfoOfUsers();
        }
        return userService.getShortInfoOfUsers();
    }

    @PutMapping("/user")
        public String updateUser(@RequestBody User user){
        String result;
        try {
            userService.checkId(user.getId());
        } catch (RegistrationException e) {
            System.err.println("Chyba při zadání ID: " + e.getLocalizedMessage());
        }
        userService.updateUser(user);
        return userService.checkUpdateUser(user);
    }

    @DeleteMapping("/user/{id}")
        public String deleteUser(@PathVariable("id") Long id){
        userService.deleteUserById(id);
        return userService.checkDeleteUser(id);
    }





}
