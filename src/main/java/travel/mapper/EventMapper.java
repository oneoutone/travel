package travel.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.poi.ss.formula.functions.Even;
import travel.entity.DataSourceRequest;
import travel.entity.Event;
import travel.entity.EventArticle;
import travel.entity.Purchase;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public interface EventMapper {
    @Insert("INSERT INTO event(title, publish_time, industry, location, created) VALUES(#{title}, #{publish_time}, #{industry}, #{location}, #{created})")
    int insertEvent(Event event);

    @Insert("INSERT INTO eventArticle(articleId, title, content, publish_time, source_name, postive, eventId, created) VALUES(#{articleId}, #{title}, #{content}, #{publish_time}, #{source_name}, #{postive}, #{eventId}, #{created})")
    int insertEventArticle(EventArticle eventArticle);

    @Select("SELECT * FROM event WHERE id=#{id}")
    Event findEventById(@Param("id") long id);

    @Select("SELECT * FROM eventArticle WHERE id=#{id}")
    EventArticle findEventArticleById(@Param("id") long id);

    @Delete("DELETE FROM event WHERE id=#{id}")
    int deleteEvent(long id);

    @Delete("DELETE FROM eventArticle WHERE id=#{id}")
    int deleteEventArticle(long id);

    @Select("<script>SELECT * FROM event WHERE 1=1 <if test = 'filter != \"\"'> AND title LIKE concat(concat('%',#{filter}),'%')</if> ORDER BY created desc LIMIT #{start},#{size}</script>")
    List<Event> findEventList(@Param("filter") String filter, @Param("start") int start, @Param("size") int size);

    @Select("<script>SELECT count(*) FROM event  WHERE 1=1 <if test = 'filter != \"\"'> AND title LIKE concat(concat('%',#{filter}),'%')</if></script>")
    int findEventCount(@Param("filter") String filter);

    @Select("SELECT * FROM eventArticle where eventId = #{eventId} ORDER BY created desc LIMIT #{start},#{size}")
    List<EventArticle> findEventArticleList(@Param("eventId") long eventId, @Param("start") int start, @Param("size") int size);

    @Select("SELECT count(*) FROM eventArticle where eventId = #{eventId}")
    int findEventArticleCount(@Param("eventId") long eventId);

}

