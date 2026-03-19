package dao;

import model.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
    void create(User user);
    Optional<User> findById(int id);
    Optional<User> findByUsername(String username);
    Optional<User> findByFullName(String fullName);
    List<User> findAll();
    void update(User user);
    void delete(int id);
}
