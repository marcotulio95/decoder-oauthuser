package com.ead.authuser.mapper;

import com.ead.authuser.dto.UserModelDto;
import com.ead.authuser.models.UserModel;

@Mapper(componentModel = "spring")
public interface UserModelMapper extends EntityMapper<UserModelDto, UserModel> {
}
