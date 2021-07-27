package com.example.testingapp.dao;

import com.example.testingapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserDao extends JpaRepository<User,Integer> {

    User findByUsername(String username);

    Optional<User> findByVerificationCode(String code);

    @Query("select u from User u where u.enabled=true and u.isDeleted=false")
    List<User> findVerifiedUsers();

    @Query("select u from User u where u.isDeleted=false ")
    List<User> findUsers();

    @Query("SELECT u from User u where u.email like :email")
    Optional<User> findByEmail(String email);

    @Query("SELECT u from User u where u.email like :email")
    List<User> findAllByEmail(String email);

    @Query("SELECT u.email from User u where u.email like :email")
    List<String> findEmail(String email);
}
