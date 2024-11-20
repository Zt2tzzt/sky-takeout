package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {
    /**
     * 根据分类id查询菜品数量
     *
     * @param categoryId 分类 Id
     * @return 查询记录数
     */
    @Select("SELECT COUNT(id) FROM dish WHERE category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 此方法用于：新增菜品
     *
     * @param dish 菜品对象
     * @return 插入记录数
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO dish (name, category_id, price, image, description, status, create_time, update_time, create_user, update_user)" +
            "VALUES (#{name}, #{categoryId}, #{price}, #{image}, #{description}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(OperationType.INSERT)
    int insert(Dish dish);

    /**
     * 此方法用于：分页查询菜品
     *
     * @param dishPageQueryDTO 分页查询条件
     * @return Page<DishVO>
     */
    Page<DishVO> selectWithPage(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 此方法用于：根据菜品id查询菜品数据
     *
     * @param id 菜品 Id
     * @return Dish
     */
    @Select("SELECT id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user FROM dish WHERE id = #{id}")
    Dish selectById(Long id);

    /**
     * 此方法用于：批量删除菜品
     *
     * @param ids 菜品 Id
     * @return 删除记录数
     */
    int deleteBatchById(List<Long> ids);

    /**
     * 根据菜品id查询菜品起售的个数
     *
     * @param ids 菜品id
     * @return 起售的个数
     */
    int countStatusByIds(List<Long> ids);

    /**
     * 此方法用于：修改菜品
     *
     * @param dish 菜品对象
     * @return 修改记录数
     */
    @AutoFill(OperationType.UPDATE)
    int update(Dish dish);

    /**
     * 此方法用于：根据分类 id 查询菜品
     *
     * @param categoryId 分类 Id
     * @return Dish
     */
    @Select("SELECT id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user FROM dish WHERE category_id = #{categoryId}")
    List<Dish> selectByCategoryId(Long categoryId);


    /**
     * 此方法用于：根据条件统计菜品数量
     *
     * @param map 查询条件
     * @return Integer
     */
    Integer countByMap(Map<String, Integer> map);
}
