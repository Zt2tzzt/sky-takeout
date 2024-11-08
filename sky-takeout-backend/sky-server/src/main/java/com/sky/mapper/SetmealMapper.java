package com.sky.mapper;

import com.sky.entity.Setmeal;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {
    /**
     * 此方法用于：根据分类id查询套餐的数量
     *
     * @param id 分类id
     * @return 套餐数量
     */
    @Select("SELECT COUNT(id) FROM setmeal WHERE category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 动态条件查询套餐
     *
     * @param setmeal 套餐
     * @return List<Setmeal>
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 此方法用于：根据套餐 id 查询菜品选项
     *
     * @param setmealId 套餐 id
     * @return List<DishItemVO>
     */
    @Select("SELECT sd.name, sd.copies, d.image, d.description " +
            "FROM setmeal_dish sd LEFT JOIN dish d ON sd.dish_id = d.id " +
            "WHERE sd.setmeal_id = #{setmealId}")
    List<DishItemVO> selectDishItemBySetmealId(Long setmealId);
}
