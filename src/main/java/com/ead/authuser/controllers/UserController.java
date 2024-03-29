package com.ead.authuser.controllers;

import com.ead.authuser.dtos.UserDTO;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.ead.authuser.specifications.SpecificationTemplate;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.log4j.Log4j2;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

import static com.ead.authuser.utils.DateUtils.getLocalDateTimeNow;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Log4j2
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserModel>> getAll(SpecificationTemplate.UserSpec spec, @PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.ASC) Pageable pageable, @RequestParam(required = false) UUID courseId) {
        Page<UserModel> userModelPage = null;
        if (courseId != null) {
            userModelPage = userService.findAll(pageable, SpecificationTemplate.userCourseId(courseId).and(spec));
        } else {
            userModelPage = userService.findAll(pageable, spec);
        }
        if (!userModelPage.isEmpty()) {
            for (UserModel userModel : userModelPage.toList()) {
                userModel.add(linkTo(methodOn(UserController.class).getById(userModel.getUserId())).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(userModelPage);
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
    public ResponseEntity<Object> updateUser(@PathVariable(value = "id") UUID id, @RequestBody @Validated(UserDTO.UserView.UserPut.class) @JsonView(UserDTO.UserView.UserPut.class) UserDTO userDto) {
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
    public ResponseEntity<Object> updateUserPassword(@PathVariable(value = "id") UUID id, @RequestBody @Validated(UserDTO.UserView.PasswordPut.class) @JsonView(UserDTO.UserView.PasswordPut.class) UserDTO userDto) {
        log.debug("PUT updateUser userDto received {}", userDto.toString());
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
        log.debug("PUT updatedUser userId saved {}", userModel.getUserId());
        log.info("PUT updatedUser successfully for userId {}", userModel.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully");
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Object> updateUserImage(@PathVariable(value = "id") UUID id, @RequestBody @Validated(UserDTO.UserView.ImagePut.class) @JsonView(UserDTO.UserView.ImagePut.class) UserDTO userDto) {
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
