package com.ead.authuser.controllers;

import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserModel>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable(value = "id") UUID id) {
        final Optional<UserModel> userModelOptional = userService.findById(id);
        return userModelOptional.isPresent() ? ResponseEntity.status(HttpStatus.OK).body(userModelOptional.get()) : ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable(value = "id") UUID id) {
        final Optional<UserModel> userModelOptional = userService.findById(id);
        if (userModelOptional.isPresent()) {
            userService.delete(userModelOptional.get());
            return ResponseEntity.status(HttpStatus.OK).body("user deleted");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
        }
    }

}
