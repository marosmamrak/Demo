package com.marosmamrak.repository;

import com.marosmamrak.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {
    private final Connection connection;

    public UserRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addUser(int userId, String userGuid, String userName) throws SQLException {
        String query = "INSERT INTO USERS (USER_ID, USER_GUID, USER_NAME) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, userGuid);
            preparedStatement.setString(3, userName);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM USERS";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int userId = resultSet.getInt("USER_ID");
                String userGuid = resultSet.getString("USER_GUID");
                String userName = resultSet.getString("USER_NAME");
                users.add(new User(userId, userGuid, userName));
            }
        }
        return users;
    }

    @Override
    public void deleteAllUsers() throws SQLException {
        String query = "DELETE FROM USERS";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        }
    }
}