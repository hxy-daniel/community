package com.cqupt.community.service;

import com.cqupt.community.dao.UserDao;
import com.cqupt.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserDao userDao;
    public User getUserById(int userId) {
        return userDao.getUserById(userId);
    }
}
