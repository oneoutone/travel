package travel.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import travel.entity.Article;
import travel.entity.DataNotification;

import java.sql.Blob;
import java.util.Date;
import java.util.List;

public interface DataNotificationMapper {
    @Insert("INSERT INTO DataNotification(articleId, title, publish_time, source_name, postive, userId, requestId, content, created) VALUES(#{articleId}, #{title}, #{publish_time}, #{source_name}, #{postive}, #{userId}, #{requestId}, #{content}, #{created})")
    int insertDataNotification(DataNotification dataNotification);

    @Select("SELECT * FROM DataNotification WHERE requestId = #{requestId} and articleId=#{articleId}")
    List<DataNotification> findArticleById(@Param("requestId") long requestId, @Param("articleId") String articleId);
}
