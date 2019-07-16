package travel.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import travel.entity.Article;
import travel.entity.AuthCode;
import travel.entity.Notification;

import java.util.Date;
import java.util.List;

public interface ArticleMapper {
    @Insert("INSERT INTO article(articleId, title, publish_time, source_name, postive, userId, type, status, created) VALUES(#{articleId}, #{title}, #{publish_time}, #{source_name}, #{postive}, #{userId}, #{type}, #{status}, #{created})")
    int insertArticle(Article article);

    @Select("SELECT * FROM article WHERE userId = #{userId} ORDER BY created desc LIMIT 5")
    List<Article> findByUserId(@Param("userId") long userId);

    @Select("SELECT * FROM article WHERE userId = #{userId} and articleId=#{articleId}")
    List<Article> findArticleById(@Param("userId") long userId, @Param("articleId") String articleId);

    @Select("SELECT * FROM article WHERE userId = #{userId} and status=#{status} and publish_time >= #{start} and publish_time < #{end}")
    List<Article> findReadNum(@Param("userId") long userId, @Param("status") String status, @Param("start") Date start, @Param("end") Date end);

    @Update("UPDATE article SET status='2' where articleId=#{articleId}")
    int updateStatus( @Param("articleId") String articleId);

    @Insert("INSERT INTO notification(articleId, userId, taskId) VALUES(#{articleId}, #{userId}, #{taskId})")
    int insertNotification(Notification notification);

    @Select("SELECT count(*) FROM notification WHERE userId = #{userId} AND articleId=#{articleId}")
    int findNotificationCount(@Param("userId") long userId, @Param("articleId") String articleId);
};
