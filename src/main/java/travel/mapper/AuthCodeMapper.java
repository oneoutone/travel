package travel.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import travel.entity.AuthCode;

import java.util.Date;
import java.util.List;

public interface AuthCodeMapper {
    @Insert("INSERT INTO auth_code(phone, code, channel, expire) VALUES(#{phone}, #{code}, #{channel}, #{expire})")
    int insertAuthCode(AuthCode authCOde);

    @Select("SELECT * FROM auth_code WHERE code = #{code} AND phone= #{phone} AND expire > #{expire} AND used = 0")
    List<AuthCode> findValidCode(@Param("code") String code, @Param("phone") String phone, @Param("expire") Date expire);

    @Update("UPDATE auth_code SET used = #{used} WHERE id = #{id}")
    int setUsed(@Param("used") Boolean used, @Param("id") long id);
}
