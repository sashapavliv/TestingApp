package com.example.testingapp.service;


import com.example.testingapp.dto.UserDto;
import com.example.testingapp.entity.User;

import java.util.List;

public interface UserService {

    String createUser(UserDto userDto);

    List<User> getAllUsers();

    List<User> getVerifiedUsers();

    List<User> getUserByEmail(String email);

    User changePassword(User user);

    void forgotPassword(User user);

    User updateUser(int id,User user);

    User softDeleteUser(int id);

    void verifyUser(String code);


}
