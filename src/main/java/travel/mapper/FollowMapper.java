package travel.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import travel.entity.Advice;
import travel.entity.DataRequest;
import travel.entity.Follow;

import java.util.Date;
import java.util.List;

public interface FollowMapper {
    @Insert("INSERT INTO follow(articleId, title, content, publish_time, source_name, userId, postive, created) VALUES(#{articleId}, #{title}, #{content}, #{publish_time}, #{source_name}, #{userId}, #{postive}, #{created})")
    int insertFollow(Follow follow);

    @Select("SELECT * FROM follow WHERE userId=#{userId} ORDER BY created desc LIMIT #{start},#{size}")
    List<Follow> findByAllList(@Param("userId") long userId, @Param("start") int start, @Param("size") int size);

    @Select("SELECT count(*) FROM follow where userId=#{userId}")
    int findByAllCount(@Param("userId") long userId);

    @Select("SELECT * FROM follow where articleId=#{articleId} and userId=#{userId}")
    Follow findByArticleId(@Param("articleId") String articleId, @Param("userId") long userId);

    @Delete("DELETE FROM follow  WHERE userId = #{userId} AND articleId=#{articleId}")
    int deleteById(@Param("articleId") String articleId, @Param("userId") long userId);

}
