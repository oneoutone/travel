package travel.mapper;

import org.apache.ibatis.annotations.Insert;
import travel.entity.JWTToken;
import travel.entity.User;

public interface JWTTokenMapper {
    @Insert("INSERT INTO jwt_token(token, host, userId, expire) VALUES(#{token}, #{host}, #{userId}, #{expire})")
    int insertToken(JWTToken jwtToken);
}
