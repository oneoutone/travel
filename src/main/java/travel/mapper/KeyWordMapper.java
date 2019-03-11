package travel.mapper;

import org.apache.ibatis.annotations.*;
import travel.entity.KeyWord;

import java.util.Date;
import java.util.List;

public interface KeyWordMapper {
    @Insert("INSERT INTO key_word(word, status, userId, created, modified) VALUES(#{word}, #{status}, #{userId}, #{created}, #{modified})")
    int inserKeyWord(KeyWord keyWord);

    @Select("<script>SELECT count(*) FROM key_word WHERE userId = #{userId} <if test = 'word != \"\"'> AND word like #{word}</if></script>")
    int findCountByUserId(@Param("userId") long userId, @Param("word") String word);

    @Select("<script>SELECT * FROM key_word WHERE userId = #{userId} <if test = 'word != \"\"'> AND word like #{word}</if> ORDER BY created desc LIMIT #{start},#{size}</script>")
    List<KeyWord> findByUserId(@Param("userId") long userId, @Param("start") int start, @Param("size") int size, @Param("word") String word);

    @Update("UPDATE key_word SET status = #{status}, modified = #{modified} WHERE id=#{id}")
    int updateStatusById(@Param("status") String status, @Param("modified") Date modified, @Param("id") long id);

    @Update("UPDATE key_word SET status = #{status}, modified = #{modified} WHERE word=#{word} AND userId=#{userId}")
    int updateStatusByWordAndUserId(@Param("status") String status, @Param("modified") Date modified, @Param("word") String word, @Param("userId") long userId);

    @Delete("DELETE FROM key_word WHERE id = #{id}")
    int deleteById(@Param("id") long id);

    @Delete("DELETE FROM key_word WHERE word=#{word} AND userId=#{userId}")
    int deleteByWordAndUserId(@Param("word") String word, @Param("userId") long userId);

    @Select("SELECT count(*) FROM key_word WHERE word=#{word} AND userId=#{userId}")
    int findCountByWordAndUserId(@Param("word") String word, @Param("userId") long userId);

    @Select("SELECT * FROM key_word WHERE userId=#{userId}")
    List<KeyWord> findAll(@Param("userId") long userId);

}
