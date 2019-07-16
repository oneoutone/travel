package travel.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import travel.entity.Data;
import org.apache.ibatis.annotations.ResultMap;
import travel.entity.DataRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public interface DataMapper {
    @Insert("INSERT INTO data(articleId, title, content, publish_time, source_name, postive, type) VALUES(#{articleId}, #{title}, #{content}, #{publish_time}, #{source_name}, #{postive}, #{type})")
    int insertData(Data data);

    @Select("SELECT count(*) FROM data WHERE articleId = #{articleId}")
    int findCountByArticleId(@Param("articleId") String articleId);

    @Select("SELECT type , count(*) as count FROM data WHERE publish_time >= #{start} and publish_time<#{end} group by type")
    List<HashMap> findData(Date start, Date end);

}
