package travel.mapper;

import javafx.util.converter.DateStringConverter;
import org.apache.ibatis.annotations.*;
import travel.entity.DataSource;
import travel.entity.KeyWord;

import java.util.Date;
import java.util.List;

public interface DataSourceMapper {

    @Insert("INSERT INTO data_source(userId, sourceId, sourceName, sourceUrl, created, type, channel) VALUES(#{userId}, #{sourceId}, #{sourceName}, #{sourceUrl}, #{created}, #{type}, #{channel})")
    int insertDataSource(DataSource dataSource);

    @Select("<script>SELECT count(*) FROM data_source WHERE userId = #{userId} <if test = 'filter != \"\"'> AND sourceUrl LIKE concat(concat('%',#{filter}),'%')</if></script>")
    int findCountByUserId(@Param("userId") long userId, @Param("filter") String filter);

    @Select("<script>SELECT * FROM data_source WHERE userId = #{userId} <if test = 'filter != \"\"'> AND sourceUrl LIKE concat(concat('%',#{filter}),'%')</if> ORDER BY created desc LIMIT #{start},#{size}</script>")
    List<DataSource> findByUserId(@Param("userId") long userId, @Param("start") int start, @Param("size") int size, @Param("filter") String filter);

    @Select("SELECT count(*) FROM data_source WHERE sourceUrl = #{sourceUrl} AND userId = #{userId}")
    int findCountByUrl(@Param("sourceUrl") String sourceUrl, @Param("userId") long userId);

    @Delete("DELETE FROM data_source  WHERE id = #{id}")
    int deleteById(@Param("id") long id);

    @Select("SELECT * FROM data_source WHERE userId=#{userId}")
    List<DataSource> findAll(@Param("userId") long userId);

    @Select("SELECT * FROM data_source WHERE userId=#{userId} AND sourceUrl = #{url}")
    List<DataSource> findByUserAndUrl(@Param("userId") long userId, @Param("url") String url);
}
