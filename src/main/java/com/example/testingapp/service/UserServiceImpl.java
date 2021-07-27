package com.example.testingapp.service;

import com.example.testingapp.dao.UserDao;
import com.example.testingapp.dto.UserDto;
import com.example.testingapp.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserDetailsService, UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private MailService mailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDao.findByUsername(username);
    }

    @Override
    public String createUser(UserDto userDto) {

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole("ROLE_USER");
        user.setEmail(userDto.getEmail());
        user.setLocation(userDto.getLocation());
        user.setEnabled(false);
        final String code = RandomStringUtils.randomNumeric(6);
        user.setVerificationCode(code);
        if(userDao.findAllByEmail(userDto.getEmail()).stream().noneMatch(email -> userDao.findEmail(userDto.getEmail()).contains(user.getEmail()))){
            userDao.save(user);
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with this email already exist, please enter other email");
        }
        userDao.save(user);
        mailService.sendVerificationCode(user);
        return String.format("User %s is registered", user.getUsername());
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findUsers();
    }

    @Override
    public List<User> getVerifiedUsers() {
        return userDao.findVerifiedUsers();
    }

    @Override
    public List<User> getUserByEmail(String email) {
        return userDao.findAllByEmail(email);
    }

    @Override
    public User changePassword(User user) {
        if(user.isDeleted()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User is deleted");
        }
        Optional<User> userDb = userDao.findByEmail(user.getEmail());
        userDb.get().setPassword(user.getPassword());
        return userDao.saveAndFlush(userDb.get());
    }

    @Override
    public void forgotPassword(User user) {
        if(user.isDeleted()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User is deleted");
        }
        mailService.sendUrl(user);
    }

    @Override
    public User updateUser(int id, User user) {
        if(user.isDeleted()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User is deleted");
        }
        Optional<User> userDb = userDao.findById(id);
        userDb.get().setId(id);
        userDb.get().setLocation(user.getLocation());

        if (!userDao.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found");
        }
        return userDao.saveAndFlush(userDb.get());
    }

    @Override
    public User softDeleteUser(int id) {
        if (!userDao.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found");
        }
        Optional<User> findUser = userDao.findById(id);
        if (!findUser.get().isDeleted()) {
            findUser.get().setDeleted(true);
        }
        return userDao.saveAndFlush(findUser.get());
    }


    @Override
    public void verifyUser(String code) {
        Optional<User> optionalUser = userDao.findByVerificationCode(code);
        User user = optionalUser.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NO user with code " + code));
        user.setEnabled(true);
        user.setVerificationCode(null);
        userDao.flush();
        log.info("User {} have been activated", user.getEmail());
    }
}
