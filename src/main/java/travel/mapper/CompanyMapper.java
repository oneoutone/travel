package travel.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.StatementType;
import travel.entity.Batch;
import travel.entity.Company;
import travel.entity.User;

public interface CompanyMapper {

    @Insert("INSERT INTO company(name, regNo, created, acNo, bank, address,phone, contactPeople, type, managerId, adminId, status, location) VALUES(#{name}, #{regNo}, #{created}, #{acNo}, #{bank}, #{address}, #{phone}, #{contactPeople}, #{type}, #{managerId}, #{adminId}, #{status}, #{location})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")//加入该注解可以保持对象后，查看对象插入id
    int insertCompany(Company company);

    @Update("UPDATE company SET adminId=#{adminId} where id=#{id}")
    int updateAdminId(@Param("adminId") long adminId, @Param("id") long id);

    @Select("SELECT * FROM company WHERE id = #{id}")
    Company findById(@Param("id") long id);

    @Update("<script>UPDATE company <set><if test = 'name != \"\"'>name = #{name},</if><if test = 'regNo != \"\"'>regNo = #{regNo},</if><if test = 'acNo != \"\"'>acNo = #{acNo},</if><if test = 'bank != \"\"'>bank = #{bank},</if><if test = 'address != \"\"'>address = #{address},</if><if test = 'phone != \"\"'>phone = #{phone},</if><if test = 'contactPeople != \"\"'>contactPeople = #{contactPeople},</if><if test = 'managerId != \"\"'>managerId = #{managerId},</if><if test = 'status != \"\"'>status = #{status},</if><if test = 'location != \"\"'>location = #{location}</if></set> where id=#{id}</script>")
    int updateCompany(Company company);

}
