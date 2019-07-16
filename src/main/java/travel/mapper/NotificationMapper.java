package travel.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import travel.entity.Notification;

import java.util.List;

public interface NotificationMapper {
    @Insert("INSERT INTO notification(articleId, userId, created) VALUES(#{articleId}, #{userId}, #{created})")
    int insertNotification(Notification notification);

    @Select("SELECT count(*) FROM notification WHERE userId=#{userId} AND articleId=#{articleId}")
    int findCount(@Param("userId") long userId, @Param("articleId") String articleId);
}
