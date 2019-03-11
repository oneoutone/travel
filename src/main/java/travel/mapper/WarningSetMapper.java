package travel.mapper;

import org.apache.ibatis.annotations.*;
import travel.entity.WarningSet;

import java.util.List;

public interface WarningSetMapper {
    @Insert("INSERT INTO warning_set(userId, sourceId, sourceName, sourceUrl, readNum, discussNum, shareNum, negValue, created) VALUES(#{userId}, #{sourceId}, #{sourceName}, #{sourceUrl}, #{readNum}, #{discussNum}, #{shareNum}, #{negValue}, #{created})")
    int insertWarningSet(WarningSet warningSet);

    @Update("UPDATE warning_set SET readNum = #{readNum},discussNum = #{discussNum},shareNum = #{shareNum},negValue = #{negValue} WHERE id=#{id}")
    int updateWarningSet(WarningSet warningSet);

    @Select("SELECT count(*) FROM warning_set WHERE userId = #{userId}")
    int findCountByUserId(@Param("userId") long userId);

    @Select("SELECT * FROM warning_set WHERE userId = #{userId} LIMIT #{start},#{size}")
    List<WarningSet> findByUserId(@Param("userId") long userId, @Param("start") int start, @Param("size") int size);

    @Delete("DELETE FROM warning_set WHERE id = #{id}")
    int deleteById(@Param("id") long id);

}
