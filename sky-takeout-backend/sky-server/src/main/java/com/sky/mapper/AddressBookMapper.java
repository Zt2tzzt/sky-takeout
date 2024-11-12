package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressBookMapper {

    /**
     * 此方法用于：条件查询
     *
     * @param addressBook 查询条件
     * @return List<AddressBook>
     */
    List<AddressBook> list(AddressBook addressBook);

    /**
     * 此方法用于：新增
     *
     * @param addressBook 新增对象
     */
    @Insert("INSERT INTO address_book" +
            "        (user_id, consignee, phone, sex, province_code, province_name, city_code, city_name, district_code," +
            "         district_name, detail, label, is_default)" +
            "        VALUES (#{userId}, #{consignee}, #{phone}, #{sex}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}," +
            "                #{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})")
    void insert(AddressBook addressBook);

    /**
     * 此方法用于：根据id查询
     *
     * @param id 地址簿 id
     * @return AddressBook
     */
    @Select("SELECT * FROM address_book WHERE id = #{id}")
    AddressBook getById(Long id);

    /**
     * 此方法用于：根据id修改
     *
     * @param addressBook 修改对象
     */
    void update(AddressBook addressBook);

    /**
     * 此方法用于：根据 用户id修改 是否默认地址
     *
     * @param addressBook 地址簿对象
     */
    @Update("UPDATE address_book SET is_default = #{isDefault} WHERE user_id = #{userId}")
    void updateIsDefaultByUserId(AddressBook addressBook);

    /**
     * 此方法用于：根据id删除地址
     *
     * @param id 地址簿 id
     */
    @Delete("DELETE FROM address_book WHERE id = #{id}")
    void deleteById(Long id);

}
