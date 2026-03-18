package model;

import java.time.LocalDateTime;

public class User {
    private Integer id;
    private String username;
    private String passwordHash;
    private String fullName;
    private LocalDateTime createdAt;

    public User() {
    }

    public User(Integer id, String username, String passwordHash, String fullName, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                       "id=" + id +
                       ", username='" + username + '\'' +
                       ", fullName='" + fullName + '\'' +
                       ", createdAt=" + createdAt +
                       '}';
    }
}
