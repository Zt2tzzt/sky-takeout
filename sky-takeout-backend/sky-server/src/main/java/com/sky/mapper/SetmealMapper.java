package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

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

    /**
     * 此方法用于：新增套餐，同时插入菜品数据
     *
     * @param setmealDTO 套餐数据
     */
    @AutoFill(OperationType.INSERT)
    int insertWithDish(SetmealDTO setmealDTO);

    /**
     * 此方法用于：分页查询套餐
     *
     * @param setmealPageQueryDTO 分页查询条件
     * @return Page<Setmeal>
     */
    Page<Setmeal> selectWithPage(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 此方法用于：批量删除套餐
     *
     * @param ids 套餐 id 集合
     * @return int
     */
    int deleteBatchById(List<Long> ids);

    /**
     * 此方法用于：根据套餐 id 查询套餐数据
     *
     * @param id 套餐 id
     * @return SetmealVO
     */
    @Select("SELECT s.id, s.category_id, s.name, s.price, s.status, s.description, s.image,  s.update_time, c.name category_name FROM setmeal s LEFT JOIN category c ON s.category_id = c.id WHERE s.id = #{id}")
    SetmealVO selectById(Long id);

    /**
     * 此方法用于：修改套餐信息
     *
     * @param setmealDTO 套餐数据
     * @return int
     */
    int update(SetmealDTO setmealDTO);

    /**
     * 此方法用于：修改套餐状态
     *
     * @param status 状态
     * @param id     套餐 id
     * @return int
     */
    @Update("UPDATE setmeal SET status = #{status} WHERE id = #{id}")
    int updateStatus(int status, Long id);

    /**
     * 此方法用于：根据条件统计套餐数量
     *
     * @param map 查询条件
     * @return Integer
     */
    Integer countByMap(Map<String, Integer> map);
}
