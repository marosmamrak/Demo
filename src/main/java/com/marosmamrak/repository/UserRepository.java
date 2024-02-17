package com.marosmamrak.repository;

import com.marosmamrak.model.User;

import java.sql.SQLException;
import java.util.List;

public interface UserRepository {
    void addUser(int userId, String userGuid, String userName) throws SQLException;
    List<User> getAllUsers() throws SQLException;
    void deleteAllUsers() throws SQLException;
}
