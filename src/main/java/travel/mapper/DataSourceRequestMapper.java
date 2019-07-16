package travel.mapper;

import org.apache.ibatis.annotations.*;
import travel.entity.DataSourceRequest;

import java.util.List;

public interface DataSourceRequestMapper {
    @Insert("INSERT INTO data_source_request(userId, sourceName, sourceUrl, created, type, channel, status) VALUES(#{userId}, #{sourceName}, #{sourceUrl}, #{created}, #{type}, #{channel}, #{status})")
    int insertDataSourceRequest(DataSourceRequest dataSource);

    @Select("SELECT count(*) FROM data_source_request WHERE userId = #{userId}")
    int findCountByUserId(@Param("userId") long userId);

    @Select("<script>SELECT count(*) FROM data_source_request WHERE userId = #{userId} <if test = 'type != \"\"'> AND type=#{type}</if> <if test = 'channel != \"\"'> AND channel=#{channel}</if><if test = 'sourceName != \"\"'> AND sourceName LIKE concat(concat('%',#{sourceName}),'%')</if></script>")
    int findCount(@Param("userId") long userId, @Param("type") String type, @Param("channel") String channel, @Param("sourceName") String sourceName);

    @Select("<script>SELECT * FROM data_source_request WHERE userId = #{userId} <if test = 'type != \"\"'> AND type=#{type}</if> <if test = 'channel != \"\"'> AND channel=#{channel}</if><if test = 'sourceName != \"\"'> AND sourceName LIKE concat(concat('%',#{sourceName}),'%')</if> ORDER BY created desc LIMIT #{start},#{size}</script>")
    List<DataSourceRequest> findByUserId(@Param("userId") long userId, @Param("start") int start, @Param("size") int size, @Param("type") String type, @Param("channel") String channel, @Param("sourceName") String sourceName);

    @Select("SELECT count(*) FROM data_source_request WHERE sourceUrl = #{sourceUrl} AND userId = #{userId}")
    int findCountByUrl(@Param("sourceUrl") String sourceUrl, @Param("userId") long userId);

    @Delete("DELETE FROM data_source_request  WHERE id = #{id}")
    int deleteById(@Param("id") long id);

    @Select("SELECT * FROM data_source_request WHERE userId=#{userId}")
    List<DataSourceRequest> findAll(@Param("userId") long userId);

    @Update("UPDATE data_source_request set status=#{status} where id=#{id}")
    int update(@Param("status") String status, @Param("id") long id);
}
