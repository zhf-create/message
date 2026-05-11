package org.example.service;

import org.example.pojo.User;

import java.util.List;

public interface UserService {

    User getUserByName(String username);

    void register(String username, String password, String email);

    void insertUser(User user);

    void deleteUser(Integer id);

    void updateUser(User user);

    User login(String username, String password);

    List<User> getAllUsers();

    User getUserById(Integer id);
}
