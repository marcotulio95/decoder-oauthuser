package com.ead.authuser.controllers;

import com.ead.authuser.dtos.UserDto;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

import static com.ead.authuser.utils.DateUtils.getLocalDateTimeNow;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserModel>> getAll(@PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll(pageable));
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

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable(value = "id") UUID id,
                                             @RequestBody @Validated(UserDto.UserView.UserPut.class)
                                             @JsonView(UserDto.UserView.UserPut.class) UserDto userDto) {
        final Optional<UserModel> userModelOptional = userService.findById(id);
        if (userModelOptional.isPresent()) {
            var userModel = userModelOptional.get();
            userModel.setFullName(userDto.getFullName());
            userModel.setPhoneNumber(userDto.getCpf());
            userModel.setCpf(userDto.getCpf());
            userModel.setLastUpdateDate(getLocalDateTimeNow());
            userService.save(userModel);
            return ResponseEntity.status(HttpStatus.OK).body(userModel);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
        }
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Object> updateUserPassword(@PathVariable(value = "id") UUID id,
                                                     @RequestBody @Validated(UserDto.UserView.PasswordPut.class) @JsonView(UserDto.UserView.PasswordPut.class) UserDto userDto) {
        final Optional<UserModel> userModelOptional = userService.findById(id);
        if (!userModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
        }

        if (!userModelOptional.get().getPassword().equals(userDto.getOldPassword())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: mismatched password");
        }
        var userModel = userModelOptional.get();
        userModel.setPassword(userModel.getPassword());
        userModel.setLastUpdateDate(getLocalDateTimeNow());
        userService.save(userModel);
        return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully");
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Object> updateUserImage(@PathVariable(value = "id") UUID id,
                                                  @RequestBody @Validated(UserDto.UserView.ImagePut.class) @JsonView(UserDto.UserView.ImagePut.class) UserDto userDto) {
        final Optional<UserModel> userModelOptional = userService.findById(id);
        if (!userModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
        }
        var userModel = userModelOptional.get();
        userModel.setImageUrl(userDto.getImageUrl());
        userModel.setLastUpdateDate(getLocalDateTimeNow());
        userService.save(userModel);
        return ResponseEntity.status(HttpStatus.OK).body(userModel);
    }

}
