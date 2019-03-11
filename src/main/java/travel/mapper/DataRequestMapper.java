package travel.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import travel.entity.DataRequest;

import java.util.Date;
import java.util.List;

public interface DataRequestMapper {
    @Insert("INSERT INTO data_request(jobName,source, keyWord, type, remark, message, phone,email,companyMobile,companyEmail,name,position,company,userId,created, status) VALUES(#{jobName}, #{source}, #{keyWord}, #{type}, #{remark}, #{message}, #{phone},#{email},#{companyMobile},#{companyEmail},#{name},#{position},#{company},#{userId},#{created}, #{status})")
    int insertDataRequest(DataRequest dataRequest);

    @Select("SELECT count(*) FROM data_request WHERE userId = #{userId}")
    int findCountByUserId(@Param("userId") long userId);

    @Select("SELECT * FROM data_request WHERE userId = #{userId} ORDER BY created desc LIMIT #{start},#{size}")
    List<DataRequest> findByUserId(@Param("userId") long userId, @Param("start") int start, @Param("size") int size);
}
