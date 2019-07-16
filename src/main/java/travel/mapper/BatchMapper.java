package travel.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import travel.entity.Batch;

import java.util.List;

public interface BatchMapper {
    @Insert("INSERT INTO batch(start, end, created, finished, status, count) VALUES(#{start}, #{end}, #{created}, #{finished}, #{status}, #{count})")
    int insertBatch(Batch batch);

    @Select("SELECT * FROM batch WHERE ORDER BY created desc LIMIT 1")
    List<Batch> findBatch();

    @Update("UPDATE batch SET status=#{status} where id=#{id}")
    int updateStatus(@Param("status") String status, @Param("id") long id);
};
