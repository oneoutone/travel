package travel.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import travel.entity.AuthCode;
import travel.entity.Report;

import java.util.Date;
import java.util.List;

public interface ReportMapper {

    @Insert("INSERT INTO report(start, end, type, url, created) VALUES(#{start}, #{end}, #{type}, #{url}, #{created})")
    int insertReport(Report report);

    @Select("<script>SELECT * FROM report WHERE 1=1  <if test = 'start != null'> AND start &gt;= #{start}</if> <if test = 'end != null'>AND end &lt;= #{end} </if> <if test = 'type != \"\"'> AND type = #{type}</if> ORDER BY created DESC LIMIT #{from},#{size}</script>")
    List<Report> findReport(@Param("start") Date start, @Param("end") Date end, @Param("type") String type, @Param("from") int from, @Param("size") int size);

    @Select("<script>SELECT count(*) FROM report WHERE 1=1  <if test = 'start != null'> AND start &gt;= #{start}</if> <if test = 'end != null'>AND end &lt;= #{end} </if> <if test = 'type != \"\"'> AND type = #{type}</if></script>")
    int findCount(@Param("start") Date start, @Param("end") Date end, @Param("type") String type);
}
