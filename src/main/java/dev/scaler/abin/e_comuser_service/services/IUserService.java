package dev.scaler.abin.e_comuser_service.services;

import dev.scaler.abin.e_comuser_service.dtos.UserDto;

import java.util.List;

public interface IUserService {
    UserDto getUserDetails(Long userId);

    UserDto setUserRoles(Long userId, List<Long> roleIds);


}
