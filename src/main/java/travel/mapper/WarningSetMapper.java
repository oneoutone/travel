package travel.mapper;

import org.apache.ibatis.annotations.*;
import travel.entity.KeyWord;
import travel.entity.WarningSet;

import java.util.List;

public interface WarningSetMapper {
    @Insert("INSERT INTO warning_set(userId, sourceId, sourceName, sourceUrl, readNum, discussNum, shareNum, negValue, created, type) VALUES(#{userId}, #{sourceId}, #{sourceName}, #{sourceUrl}, #{readNum}, #{discussNum}, #{shareNum}, #{negValue}, #{created}, #{type})")
    int insertWarningSet(WarningSet warningSet);

    @Update("UPDATE warning_set SET readNum = #{readNum},discussNum = #{discussNum},shareNum = #{shareNum},negValue = #{negValue} WHERE id=#{id}")
    int updateWarningSet(WarningSet warningSet);

    @Update("UPDATE warning_set SET readNum = #{readNum},discussNum = #{discussNum},shareNum = #{shareNum},negValue = #{negValue} where userId = #{userId}")
    int updateWarningSetAll(WarningSet warningSet);

    @Select("SELECT count(*) FROM warning_set WHERE userId = #{userId}")
    int findCountByUserId(@Param("userId") long userId);

    @Select("SELECT * FROM warning_set WHERE userId = #{userId}")
    List<WarningSet> findByUserId(@Param("userId") long userId);

    @Select("SELECT * FROM warning_set WHERE id = #{id}")
    WarningSet findById(@Param("id") long id);

    @Select("SELECT * FROM warning_set WHERE userId = #{userId} AND type='default'")
    List<WarningSet> findDefaultByUserId(@Param("userId") long userId);

    @Select("SELECT * FROM warning_set WHERE userId = #{userId} AND type='specify'")
    List<WarningSet> findSpecifyByUserId(@Param("userId") long userId);

    @Select("SELECT * FROM warning_set WHERE userId = #{userId} AND sourceName = #{sourceName}")
    List<WarningSet> findDefaultBySourceName(@Param("userId") long userId, @Param("sourceName") String sourceName);

    @Delete("DELETE FROM warning_set WHERE id = #{id}")
    int deleteById(@Param("id") long id);

    @Select("<script>SELECT * FROM warning_set WHERE userId = #{userId} <if test = 'filter != \"\"'> AND sourceUrl LIKE concat(concat('%',#{filter}),'%')</if>  <if test = 'type != \"\"'> AND type = #{type}</if>ORDER BY created desc LIMIT #{start},#{size}</script>")
    List<WarningSet> findByUserIdAndFilter(@Param("userId") long userId, @Param("start") int start, @Param("size") int size, @Param("filter") String word, @Param("type") String type);

    @Select("<script>SELECT count(*) FROM warning_set WHERE userId = #{userId} <if test = 'type != \"\"'> AND type = #{type}</if></script>")
    int findCountByUserIdAndFilter(@Param("userId") long userId, @Param("filter") String filter , @Param("type") String type);


}
