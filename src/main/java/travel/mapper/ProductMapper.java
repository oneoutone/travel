package travel.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import travel.entity.Product;

import java.util.List;

public interface ProductMapper {
    @Select("SELECT * FROM product")
    List<Product> findAllProduct();

    @Select("SELECT * FROM product WHERE type = #{type}")
    List<Product> findAllProductByType(@Param("type") String type);

    @Select("SELECT * FROM product_book a, product b, WHERE a.userId = #{userId} AND b.id=a.productId")
    List<ResultMap> findProductBookByUserId(@Param("userId") long userId);
}
