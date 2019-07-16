package travel.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import travel.entity.Advice;
import travel.entity.Article;

import java.util.HashMap;
import java.util.List;

public interface AdviceMapper {
    @Insert("INSERT INTO advice(title, content, userId, managerId, created) VALUES(#{title}, #{content}, #{userId}, #{managerId}, #{created})")
    int insertAdvice(Advice advice);

    @Select("SELECT count(*) FROM advice WHERE userId = #{userId}")
    int findAdviceCountByUser( @Param("userId") long userId);

    @Select("SELECT *  FROM advice WHERE userId = #{userId} ORDER BY created desc LIMIT #{start},#{size}")
    List<Advice> findAdviceListByUserId( @Param("userId") long userId, @Param("start") int start, @Param("size") int size);
}

