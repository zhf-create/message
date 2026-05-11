package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.User;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("select * from user where username = #{username}")
    User getUserByName(String username);

    @Insert("insert into user(username,password,email,create_time,update_time)" +
            " values(#{username},#{password},#{email},now(),now())")
    void add(String username, String password, String email);

    @Insert("insert into user(username,password,email,nickname,create_time,update_time)" +
            " values(#{username},#{password},#{email},COALESCE(#{nickname},''),now(),now())")
    void insertUser(User user);

    @Delete("delete from user where id=#{id}")
    void deleteUser(Integer id);

    @Update("update user set username=#{username},email=#{email},nickname=#{nickname},user_pic=#{userPic},update_time=#{updateTime} where id=#{id}")
    void updateUser(User user);

    @Select("select * from user")
    List<User> getAllUsers();

    @Select("select * from user where id=#{id}")
    User getUserById(Integer id);
}
