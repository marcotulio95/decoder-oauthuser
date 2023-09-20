package com.ead.authuser.services;

import com.ead.authuser.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    List<UserModel> findAll();

    Optional<UserModel> findById(UUID id);

    void delete(UserModel userModel);

    void save(UserModel userModel);

    boolean findByUserName(String username);

    boolean findByEmail(String email);

	Page<UserModel> findAll(Pageable pageable);
}
