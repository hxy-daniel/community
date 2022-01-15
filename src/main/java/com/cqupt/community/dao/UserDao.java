package com.cqupt.community.dao;

import com.cqupt.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {

    public User getUserById (int userId);

    public int insertUser (User user);

    public User selectByName (String username);

    public User selectByEmail (String email);

    public int updateUserStatus (int id, int status);

    public int updateUserHeaderUrl(int userId, String headerUrl);

    public int updateUserPassword(int userId, String password);
}
