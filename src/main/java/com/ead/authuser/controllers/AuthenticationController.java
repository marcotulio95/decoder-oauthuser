package com.ead.authuser.controllers;

import com.ead.authuser.dtos.UserDTO;
import com.ead.authuser.enums.UserStatus;
import com.ead.authuser.enums.UserType;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ead.authuser.utils.DateUtils.getLocalDateTimeNow;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/auth")
@Log4j2
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@RequestBody @Validated(UserDTO.UserView.RegistrationPost.class) @JsonView(UserDTO.UserView.RegistrationPost.class) UserDTO userDTO) {
        log.debug("POST registerUser userDto received {}", userDTO.toString());
        if (userService.findByUserName(userDTO.getUsername())) {
            log.warn("username {} already exists", userDTO.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: username already exists!");
        }
        if (userService.findByEmail(userDTO.getEmail())) {
            log.warn("email {} already exists", userDTO.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: email already exists!");
        }
        var userModel = new UserModel();
        BeanUtils.copyProperties(userDTO, userModel);
        userModel.setUserStatus(UserStatus.ACTIVE);
        userModel.setUserType(UserType.STUDENT);
        userModel.setCreationDate(getLocalDateTimeNow());
        userModel.setLastUpdateDate(getLocalDateTimeNow());
        userService.save(userModel);
        log.debug("POST registerUser userId saved {}", userModel.getUserId());
        log.info("User saved successfully for userId {}", userModel.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(userModel);
    }

}
