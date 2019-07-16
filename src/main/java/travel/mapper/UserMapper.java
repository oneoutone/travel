package travel.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.StatementType;
import travel.entity.DataRequest;
import travel.entity.User;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public interface UserMapper {

    @Insert("INSERT INTO user(username, password, name, phone, created, modified, specify_source, specify_warn_setting, roles, companyId) VALUES(#{username}, #{password}, #{name}, #{phone}, #{created}, #{modified}, #{specify_source}, #{specify_warn_setting}, #{roles}, #{companyId})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")//加入该注解可以保持对象后，查看对象插入id
    int insertUser(User user);

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(@Param("id") long id);

    @Select("SELECT * FROM user WHERE phone = #{phone}")
    List<User> findByPhone(@Param("phone") String phone);

    @Select("SELECT id, username, name, phone, roles FROM user WHERE roles is not null")
    List<User> findManagers();

    @Select("SELECT * FROM user WHERE username = #{username} AND password = #{password}")
    User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    @Select("SELECT * from user where openId =#{openId}")
    User findByOpenId(@Param("openId") String openId);

    @Select("SELECT * FROM user")
    List<User> findUsers();

    @Select("SELECT * FROM user where companyId > 0")
    List<User> findUsersWithCompany();

    @Select("SELECT * FROM user where roles Like '%manager%'")
    List<User> findManager();

    @Update("<script>UPDATE user <set><if test = 'name != null'>name = #{name},</if><if test = 'phone != null'>phone = #{phone},</if><if test = 'password != null'>password = #{password},</if><if test = 'roles != null'>roles = #{roles}</if></set> where id=#{id}</script>")
    int updateUser(User user);

    @Update("UPDATE user SET email_warn=#{email_warn}, wechat_warn=#{wechat_warn}, message_warn=#{message_warn}, app_warn=#{app_warn} where id=#{id}")
    int updateUserWarn(User user);

    @Update("UPDATE user SET openId=#{openId}, headerUrl=#{headerUrl} where id=#{id}")
    int updateWechatInfo(User user);

    @Update("UPDATE user SET warn_emails=#{warn_emails}, warn_phones=#{warn_phones} where id=#{id}")
    int updateWarnInfo(User user);

    @Update("UPDATE user SET password=#{password}, modified=#{modified} where id=#{id}")
    int updatePassword(User user);

    @Update("UPDATE user SET specify_source=#{specify_source} where id=#{id}")
    int updateSpecifySource(@Param("specify_source") boolean specify_source, @Param("id") long id);

    @Update("UPDATE user SET specify_warn_setting=#{specifyWarning} where id=#{id}")
    int updateSpecifyWarning(@Param("specifyWarning") boolean specifyWarning, @Param("id") long id);

    @Delete("DELETE FROM user WHERE id=#{id}")
    int deleteUser(long id);

    @Select("<script>SELECT count(*) FROM company WHERE type = #{type} <if test = 'filter != \"\"'> AND name like concat(concat('%',#{filter}),'%')</if><if test = 'status != \"\"'> AND status = #{status}</if><if test = 'managerId != 0'> AND managerId = #{managerId}</if></script>")
    int findCompanyCount(@Param("type") String type, @Param("filter") String filter, @Param("status") String status, @Param("managerId") long managerId);

    @Select("<script>SELECT *, user.name as nickname FROM company, user WHERE company.type=#{type} <if test = 'filter != \"\"'> AND company.name like concat(concat('%',#{filter}),'%')</if><if test = 'status != \"\"'> AND company.status = #{status}</if><if test = 'managerId != 0'> AND company.managerId = #{managerId}</if> AND company.adminId = user.id ORDER BY user.created desc LIMIT #{start},#{size}</script>")
    List<HashMap> findCompanyList(@Param("type") String type, @Param("filter") String filter, @Param("status") String status, @Param("managerId") long managerId, @Param("start") int start, @Param("size") int size);

}
