package com.marosmamrak.model;

import java.util.Objects;

public class User {
    private final int userId;
    private final String userGuid;
    private final String userName;

    public User(int userId, String userGuid, String userName) {
        this.userId = userId;
        this.userGuid = userGuid;
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }


    public String getUserGuid() {
        return userGuid;
    }



    public String getUserName() {
        return userName;
    }


    // Equals, hashCode, and toString methods

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId &&
                userGuid.equals(user.userGuid) &&
                userName.equals(user.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userGuid, userName);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userGuid='" + userGuid + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
