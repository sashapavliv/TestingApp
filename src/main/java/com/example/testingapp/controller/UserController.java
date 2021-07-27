package com.example.testingapp.controller;

import com.example.testingapp.dto.AuthRequest;
import com.example.testingapp.dto.AuthResponse;
import com.example.testingapp.dto.UserDto;
import com.example.testingapp.entity.User;
import com.example.testingapp.service.JwtService;
import com.example.testingapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/user")
    public String register(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @GetMapping("/user")
    public List<User> getUsers(){
        return userService.getAllUsers();
    }
    @GetMapping("/user/{email}")
    public List<User> getUserByEmail(@PathVariable String email){
        return userService.getUserByEmail(email);
    }

    @PutMapping("/user/{id}")
    public User updateUser(@PathVariable int id,@RequestBody User user){
        return userService.updateUser(id,user);
    }
    @PutMapping("/user/softDelete/{id}")
    public User softDelete(@PathVariable int id){
        return userService.softDeleteUser(id);
    }
    @PostMapping("/user/changePassword")
    public User changePassword(@RequestBody User user){
        return userService.changePassword(user);
    }

    @GetMapping("/user/verified")
    public List<User> getVerifiedUsers(){
        return userService.getVerifiedUsers();
    }

    @GetMapping("/user/verification")
    public void verifyUser(@RequestParam String code){
        userService.verifyUser(code);
    }

    @PostMapping("/user/forgotPassword")
    public void forgotPassword(@RequestBody User user){
        userService.forgotPassword(user);
    }

    @PostMapping("/token")
    public AuthResponse generateToken(@RequestBody AuthRequest authRequest) {
        final Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (!authenticate.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No user with such username: " + authRequest.getUsername());
        }
        final String token =
                jwtService.generateToken(authRequest.getUsername(), "My Token");
        return new AuthResponse(token);
    }
}
