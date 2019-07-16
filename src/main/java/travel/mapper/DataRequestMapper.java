package travel.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import travel.entity.DataRequest;

import java.util.Date;
import java.util.List;

public interface DataRequestMapper {
    @Insert("INSERT INTO data_request(jobName,source, keyWord, type, remark, message, phone,email,companyMobile,companyEmail,name,position,company,userId,created, status) VALUES(#{jobName}, #{source}, #{keyWord}, #{type}, #{remark}, #{message}, #{phone},#{email},#{companyMobile},#{companyEmail},#{name},#{position},#{company},#{userId},#{created}, #{status})")
    int insertDataRequest(DataRequest dataRequest);

    @Select("SELECT count(*) FROM data_request WHERE userId = #{userId}")
    int findCountByUserId(@Param("userId") long userId);

    @Select("SELECT * FROM data_request WHERE id = #{id}")
    DataRequest findById(@Param("id") long id);

    @Select("SELECT * FROM data_request WHERE status=#{status}")
    List<DataRequest> findAll(@Param("status") String status);

    @Select("SELECT * FROM data_request WHERE userId = #{userId} ORDER BY created desc LIMIT #{start},#{size}")
    List<DataRequest> findByUserId(@Param("userId") long userId, @Param("start") int start, @Param("size") int size);

    @Select("<script>SELECT * FROM data_request WHERE 1=1 <if test = 'name != \"\"'> AND jobName LIKE concat(concat('%',#{name}),'%')</if> <if test = 'status != \"\"'> AND status = #{status}</if> ORDER BY created desc LIMIT #{start},#{size}</script>")
    List<DataRequest> findByAllList(@Param("name") String name, @Param("status") String status, @Param("start") int start, @Param("size") int size);

    @Select("<script>SELECT count(*) FROM data_request WHERE  1=1 <if test = 'name != \"\"'> AND jobName LIKE concat(concat('%',#{name}),'%')</if> <if test = 'status != \"\"'> AND status = #{status}</if></script>")
    int findByAllCount(@Param("name") String name, @Param("status") String status);

    @Update("UPDATE data_request SET status=#{status} where id=#{id}")
    int updateStatus(@Param("status") String status, @Param("id") long id);

    @Update("UPDATE data_request SET urls=#{urls} where id=#{id}")
    int updateUrls(@Param("urls") String urls, @Param("id") long id);

    @Update("UPDATE data_request SET words=#{words} where id=#{id}")
    int updateWords(@Param("words") String words, @Param("id") long id);
}
