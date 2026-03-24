package service;

import model.User;

import java.util.Optional;

public interface UserService {
    void register(String username, String password, String fullName);
    String login(String username, String password);
    void logout();
    Optional<User> getCurrentUser();
    void changePassword(String oldPassword, String newPassword);
}
