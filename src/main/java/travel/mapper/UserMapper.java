package travel.mapper;

import org.apache.ibatis.annotations.*;
import travel.entity.User;

import java.util.List;

public interface UserMapper {

    @Insert("INSERT INTO user(username, password, name, phone, created, modified) VALUES(#{username}, #{password}, #{name}, #{phone}, #{created}, #{modified})")
    int insertUser(User user);

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(@Param("id") long id);

    @Select("SELECT * FROM user WHERE username = #{username} AND password = #{password}")
    User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    @Select("SELECT * FROM user")
    List<User> findUsers();

    @Update("<script>UPDATE user <set><if test = 'name != null'>name = #{name},</if><if test = 'phone != null'>phone = #{phone}</if></set> where id=#{id}</script>")
    int updateUser(User user);

    @Delete("DELETE FROM user WHERE id=#{id}")
    int deleteUser(long id);
}
