package com.cqupt.community.dao;

import com.cqupt.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {

    public User getUserById (int userId);
}
