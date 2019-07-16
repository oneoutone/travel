package travel.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import travel.entity.Article;
import travel.entity.ArticleData;
import travel.entity.Data;
import travel.entity.DataSource;

import java.util.List;

public interface ArticleDataMapper {
    @Insert("INSERT INTO data1(articleId, title, content, publish_time, source_name, postive, negtive, type, articletype, sentiment, source_url, locations) VALUES(#{articleId}, #{title}, #{content}, #{publish_time}, #{source_name}, #{postive}, #{negtive}, #{type}, #{articletype}, #{sentiment}, #{source_url}, #{locations})")
    int insert(ArticleData articleData);

    @Select("SELECT count(*) FROM data1 WHERE articleId = #{articleId} OR title=#{title}")
    int findCountByArticleId(@Param("articleId") String articleId, @Param("title") String title);

    @Select("<script>SELECT * FROM data1 WHERE 1=1 <if test = 'location != \"\"'> AND locations LIKE concat(concat('%',#{location}),'%')</if> LIMIT #{start},#{size}</script>")
    List<DataSource> findData(@Param("location") String location, @Param("start") int start, @Param("size") int size);

    @Select("<script>SELECT count(*) FROM data1 WHERE 1=1 <if test = 'location != \"\"'> AND locations LIKE concat(concat('%',#{location}),'%')</if></script>")
    int findCount(@Param("location") String location);

}
