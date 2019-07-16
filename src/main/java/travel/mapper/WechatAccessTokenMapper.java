package travel.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import travel.entity.WechatAccessToken;

import java.util.Date;

public interface WechatAccessTokenMapper {
    @Insert("INSERT INTO wechat_token(access_token,expire) VALUES(#{access_token}, #{expire})")
    int insertWechatAccessToken(WechatAccessToken wechatAccessToken);

    @Select("SELECT * FROM wechat_token WHERE expire > #{expire} LIMIT 1")
    WechatAccessToken findToken(@Param("expire") Date expire);

}
