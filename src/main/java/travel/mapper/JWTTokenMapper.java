package travel.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import travel.entity.JWTToken;
import travel.entity.User;

import java.util.Date;
import java.util.List;

public interface JWTTokenMapper {
    @Insert("INSERT INTO jwt_token(token, host, userId, expire) VALUES(#{token}, #{host}, #{userId}, #{expire})")
    int insertToken(JWTToken jwtToken);

    @Select("SELECT count(*) FROM jwt_token WHERE token = #{token} AND expire > #{expire}")
    int countByToken(@Param("token") String token, @Param("expire") Date expire);

    @Select("SELECT * FROM jwt_token WHERE token = #{token}")
    JWTToken findByToken(@Param("token") String token);

}
