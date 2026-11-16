package com.smarthirex.service;

import com.smarthirex.entity.User;
import com.smarthirex.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    public List<User> getAllUsers() {
        return repo.findAll();
    }

    public User getUserById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return repo.findByEmail(email);
    }

    public User saveUser(User user) {
        return repo.save(user);
    }

    public void deleteUser(Long id) {
        repo.deleteById(id);
    }
}
