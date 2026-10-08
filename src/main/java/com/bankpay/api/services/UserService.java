package com.bankpay.api.services;

import com.bankpay.api.domain.user.User;
import com.bankpay.api.domain.user.UserType;
import com.bankpay.api.dtos.UserDto;
import com.bankpay.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;


import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;
    public void validateTransaction(User sender, BigDecimal amount) throws Exception {
        if (sender.getUserType() == UserType.MERCHANT) {
            throw new Exception("Merchants cannot send money");
        }
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new Exception("Insufficient balance");
        }
    }
    public User findUserById(Long id) throws Exception{
        return repository.findUserById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public User createUser(UserDto data) {
        User newUser = new User(data);
        this.saveUser(newUser);
        return newUser;
    }

    public List<User> getAllUser() {
        return repository.findAll();
    }

    public void saveUser(User user){
        repository.save(user);
    }


}
