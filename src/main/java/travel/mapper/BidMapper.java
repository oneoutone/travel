package travel.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import travel.entity.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public interface BidMapper {
    @Insert("INSERT INTO bidsInfo(name, regNo, acNo, bank, address, phone,contactPeople, status, productName, type, industry,keyWords,productAdvantage, processAdvantage,url,userId,created) VALUES(#{name}, #{regNo}, #{acNo}, #{bank}, #{address}, #{phone}, #{contactPeople}, #{status}, #{productName}, #{type}, #{industry},#{keyWords},#{productAdvantage}, #{processAdvantage},#{url},#{userId},#{created})")
    int insertBid(BidsInfo bidsInfo);

    @Update("<script>UPDATE bidsInfo <set><if test = 'productName != null'>productName = #{productName},</if><if test = 'industry != null'>industry = #{industry},</if><if test = 'keyWords != null'>keyWords = #{keyWords},</if><if test = 'productAdvantage != null'>productAdvantage = #{productAdvantage},</if><if test = 'url != null'>url = #{url},</if><if test = 'processAdvantage != null'>processAdvantage = #{processAdvantage}</if></set> where id=#{id}</script>")
    int updateBid(BidsInfo bidsInfo);

    @Insert("INSERT INTO supplier(status, userId, created) VALUES(#{status}, #{userId}, #{created})")
    int insertSupplier(Supplier supplier);

    @Insert("INSERT INTO buyer(status, userId, created) VALUES(#{status}, #{userId}, #{created})")
    int insertBuyer(Buyer buyer);

    @Insert("INSERT INTO distributor(status, userId, created) VALUES(#{status}, #{userId}, #{created})")
    int insertDistributor(Distributor distributor);

    @Select("SELECT * FROM bidsInfo WHERE userId=#{userId}")
    BidsInfo findBid(@Param("userId") long userId);

    @Select("SELECT * FROM bidsInfo WHERE id=#{id}")
    BidsInfo findBidById(@Param("id") long id);

    @Select("SELECT * FROM supplier WHERE userId=#{userId}")
    Supplier findSupplier(@Param("userId") long userId);

    @Select("SELECT * FROM buyer WHERE userId=#{userId}")
    Buyer findBuyer(@Param("userId") long userId);

    @Select("SELECT * FROM distributor WHERE userId=#{userId}")
    Distributor findDistributor(@Param("userId") long userId);

    @Select("<script>SELECT b.status as status, b.id as buyerId, a.* FROM bidsInfo a, buyer b  WHERE a.userId=b.userId <if test = 'filter != \"\"'> AND a.name LIKE concat(concat('%',#{filter}),'%')</if>  <if test = 'status != \"\"'> AND b.status = #{status}</if>ORDER BY b.created desc LIMIT #{start},#{size}</script>")
    List<HashMap> findBuyerList(@Param("start") int start, @Param("size") int size, @Param("filter") String filter, @Param("status") String status);

    @Select("<script>SELECT count(*) FROM bidsInfo a, buyer b WHERE a.userId = b.userId <if test = 'filter != \"\"'> AND a.name LIKE concat(concat('%',#{filter}),'%')</if><if test = 'status != \"\"'> AND b.status = #{status}</if></script>")
    int findBuyerCount( @Param("filter") String filter , @Param("status") String status);

    @Select("<script>SELECT b.status as status, b.id as supplierId, a.* FROM bidsInfo a, supplier b  WHERE a.userId=b.userId <if test = 'filter != \"\"'> AND a.name LIKE concat(concat('%',#{filter}),'%')</if>  <if test = 'status != \"\"'> AND b.status = #{status}</if>ORDER BY b.created desc LIMIT #{start},#{size}</script>")
    List<HashMap> findSupplierList(@Param("start") int start, @Param("size") int size, @Param("filter") String filter, @Param("status") String status);

    @Select("<script>SELECT count(*) FROM bidsInfo a, supplier b WHERE a.userId = b.userId <if test = 'filter != \"\"'> AND a.name LIKE concat(concat('%',#{filter}),'%')</if><if test = 'status != \"\"'> AND b.status = #{status}</if></script>")
    int findSupplierCount( @Param("filter") String filter , @Param("status") String status);

    @Select("<script>SELECT  b.status as status, b.id as distributorId, a.* FROM bidsInfo a, distributor b  WHERE a.userId=b.userId <if test = 'filter != \"\"'> AND a.name LIKE concat(concat('%',#{filter}),'%')</if>  <if test = 'status != \"\"'> AND b.status = #{status}</if>ORDER BY b.created desc LIMIT #{start},#{size}</script>")
    List<HashMap> findDistributorList(@Param("start") int start, @Param("size") int size, @Param("filter") String filter, @Param("status") String status);

    @Select("<script>SELECT count(*) FROM bidsInfo a, distributor b WHERE a.userId = b.userId <if test = 'filter != \"\"'> AND a.name LIKE concat(concat('%',#{filter}),'%')</if><if test = 'status != \"\"'> AND b.status = #{status}</if></script>")
    int findDistributorCount( @Param("filter") String filter , @Param("status") String status);

    @Update("UPDATE buyer SET status=#{status} where id = #{id}")
    int updateBuyer(@Param("status") String status, @Param("id") long id);

    @Update("UPDATE supplier SET status=#{status} where id = #{id}")
    int updateSupplier(@Param("status") String status, @Param("id") long id);

    @Update("UPDATE distributor SET status=#{status} where id = #{id}")
    int updateDistributor(@Param("status") String status, @Param("id") long id);

    @Insert("INSERT INTO purchase(name, detail, industry, location, status, type,userId, price, start, end) VALUES(#{name}, #{detail}, #{industry}, #{location}, #{status}, #{type}, #{userId}, #{price}, #{start}, #{end})")
    int insertPurchase(Purchase purchase);

    @Select("SELECT * FROM purchase WHERE id=#{id}")
    Purchase findPurchaseById(@Param("id") long id);

    @Update("<script>UPDATE purchase <set><if test = 'name != null'>name = #{name},</if><if test = 'detail != null'>detail = #{detail},</if><if test = 'industry != null'>industry = #{industry},</if><if test = 'location != null'>location = #{location},</if><if test = 'status != null'>status = #{status},</if><if test = 'type != null'>type = #{type},</if><if test = 'price != null'>price = #{price},</if><if test = 'start != null'>start = #{start},</if><if test = 'end != null'>end = #{end}</if></set> where id=#{id}</script>")
    int updatePurchase(Purchase purchase);

    @Select("<script>SELECT * FROM purchase WHERE 1=1 <if test = 'filter != \"\"'> AND name LIKE concat(concat('%',#{filter}),'%')</if>  <if test = 'status != \"\"'> AND status = #{status}</if><if test = 'type != \"\"'> AND type = #{type}</if><if test = 'userId != 0'> AND userId = #{userId}</if>ORDER BY start asc LIMIT #{start},#{size}</script>")
    List<Purchase> findPurchaseList(@Param("start") int start, @Param("size") int size, @Param("filter") String filter, @Param("status") String status, @Param("type") String type, @Param("userId") long userId);

    @Select("<script>SELECT count(*) FROM purchase WHERE 1=1 <if test = 'filter != \"\"'> AND name LIKE concat(concat('%',#{filter}),'%')</if>  <if test = 'status != \"\"'> AND status = #{status}</if><if test = 'type != \"\"'> AND type = #{type}</if><if test = 'userId != 0'> AND userId = #{userId}</if></script>")
    int findPurchaseCount(@Param("filter") String filter, @Param("status") String status, @Param("type") String type, @Param("userId") long userId);

    @Insert("INSERT INTO purchaseApply(purchaseId, bidInfoId, type) VALUES(#{purchaseId}, #{bidInfoId}, #{type})")
    int insertPurchaseApply(PurchaseApply purchaseApply);

    @Select("<script>(SELECT b.userId as offerId, a.* FROM bidsInfo a, supplier b WHERE a.userId=b.userId and b.status='normal' <if test = 'filter != \"\"'> AND a.name LIKE concat(concat('%',#{filter}),'%')</if>) union (SELECT b.userId as offerId, a.* FROM bidsInfo a, distributor b WHERE a.userId=b.userId and b.status='normal'<if test = 'filter != \"\"'> AND a.name LIKE concat(concat('%',#{filter}),'%')</if>) ORDER BY created desc LIMIT #{start},#{size}</script>")
    List<HashMap> findOfferList(@Param("filter") String filter,@Param("start") int start, @Param("size") int size);

    @Select("<script>SELECT count(*) from ((SELECT b.userId as offerId, a.* FROM bidsInfo a, supplier b WHERE a.userId=b.userId and b.status='normal' <if test = 'filter != \"\"'> AND a.name LIKE concat(concat('%',#{filter}),'%')</if>) union (SELECT b.userId as offerId, a.* FROM bidsInfo a, distributor b WHERE a.userId=b.userId and b.status='normal'<if test = 'filter != \"\"'> AND a.name LIKE concat(concat('%',#{filter}),'%')</if>)) d</script>")
    int findOfferCount(@Param("filter") String filter);

    @Select("SELECT * FROM purchaseApply WHERE purchaseId=#{purchaseId} AND bidInfoId=#{bidInfoId}")
    PurchaseApply findPurchaseApply(@Param("purchaseId") long purchaseId, @Param("bidInfoId") long bidInfoId);

    @Select("<script>SELECT b.type as origin, a.* FROM bidsInfo a, purchaseApply b WHERE a.id=b.bidInfoId and b.purchaseId =#{purchaseId} <if test = 'filter != \"\"'> AND a.name LIKE concat(concat('%',#{filter}),'%')</if>  <if test = 'type != \"\"'> AND b.type=#{type}</if> LIMIT #{start},#{size}</script>")
    List<HashMap> findApplierList(@Param("purchaseId") long purchaseId, @Param("type") String type, @Param("filter") String filter,@Param("start") int start, @Param("size") int size);

    @Select("<script>SELECT count(*) FROM bidsInfo a, purchaseApply b WHERE a.id=b.bidInfoId and b.purchaseId =#{purchaseId}  <if test = 'filter != \"\"'> AND a.name LIKE concat(concat('%',#{filter}),'%')</if>  <if test = 'type != \"\"'> AND b.type=#{type}</if></script>")
    int findApplierCount(@Param("purchaseId") long purchaseId,@Param("type") String type, @Param("filter") String filter);

    @Select("<script>SELECT a.name as purchaseName, a.detail as detail, a.industry as industry1, a.location as location1, a.price as price1,  b.* FROM purchase a, bidInfo b WHERE a.userId = b.userId AND a.status='已发布' <if test = 'filter != \"\"'> AND a.name LIKE concat(concat('%',#{filter}),'%')</if>ORDER BY a.start asc LIMIT #{start},#{size}</script>")
    List<HashMap> findPurchaseBuyerList(@Param("start") int start, @Param("size") int size, @Param("filter") String filter);

    @Select("<script>SELECT count(*) FROM purchase a, bidInfo b WHERE a.userId = b.userId AND a.status='已发布' <if test = 'filter != \"\"'> AND a.name LIKE concat(concat('%',#{filter}),'%')</if></script>")
    int findPurchaseBuyerCount(@Param("filter") String filter);

}
