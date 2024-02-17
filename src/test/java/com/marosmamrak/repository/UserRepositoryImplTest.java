package com.marosmamrak.repository;

import com.marosmamrak.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserRepositoryImplTest {

    @Mock
    private Connection connection;
    @Mock
    private Statement statement;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;

    private UserRepositoryImpl userRepository;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        userRepository = new UserRepositoryImpl(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    void testAddUser() throws SQLException {
        // Arrange
        int userId = 1;
        String userGuid = "a1";
        String userName = "Robert";

        // Act
        userRepository.addUser(userId, userGuid, userName);

        // Assert
        verify(connection).prepareStatement(anyString());
        verify(preparedStatement).setInt(1, userId);
        verify(preparedStatement).setString(2, userGuid);
        verify(preparedStatement).setString(3, userName);
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testGetAllUsers() throws SQLException {
        // Arrange
        int userId1 = 1;
        String userGuid1 = "a1";
        String userName1 = "Robert";
        int userId2 = 2;
        String userGuid2 = "a2";
        String userName2 = "Martin";

        // Simulate ResultSet data
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("USER_ID")).thenReturn(userId1, userId2);
        when(resultSet.getString("USER_GUID")).thenReturn(userGuid1, userGuid2);
        when(resultSet.getString("USER_NAME")).thenReturn(userName1, userName2);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);

        // Act
        List<User> users = userRepository.getAllUsers();

        // Assert
        assertEquals(2, users.size());
        assertEquals(userId1, users.get(0).getUserId());
        assertEquals(userGuid1, users.get(0).getUserGuid());
        assertEquals(userName1, users.get(0).getUserName());
        assertEquals(userId2, users.get(1).getUserId());
        assertEquals(userGuid2, users.get(1).getUserGuid());
        assertEquals(userName2, users.get(1).getUserName());

        // Verify interactions
        verify(connection).createStatement();
        verify(statement).executeQuery("SELECT * FROM USERS");
        verify(resultSet, times(3)).next(); // Expected to move through each row and then return false
        verify(resultSet, times(2)).getInt("USER_ID");
        verify(resultSet, times(2)).getString("USER_GUID");
        verify(resultSet, times(2)).getString("USER_NAME");
    }

    @Test
    void testDeleteAllUsers() throws SQLException {
        // Act
        userRepository.deleteAllUsers();

        // Assert
        verify(connection).createStatement(); // Verifying interaction with the connection
        verify(connection.createStatement()).executeUpdate("DELETE FROM USERS"); // Verifying the delete statement execution
    }
}
