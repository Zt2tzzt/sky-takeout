package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     *
     * @param username
     * @return
     */
    @Select("SELECT * FROM employee WHERE username = #{username}")
    Employee getByUsername(String username);

    /**
     * 此方法用于：插入员工
     *
     * @param employee 要插入的员工对象
     * @return 操作记录数
     */
    @Insert("INSERT INTO employee(name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user) " +
            "VALUES (#{name}, #{username},#{password}, #{phone}, #{sex}, #{idNumber}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser}) ")
    @AutoFill(OperationType.INSERT)
    int insert(Employee employee);

    /**
     * 此方法用于：分页查询员工信息
     *
     * @param employeePageQueryDTO 查询条件
     * @return Page<Employee>
     */
    Page<Employee> selectEmpByPage(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 此方法用于：修改员工信息
     *
     * @param employee 要修改的员工对象
     */
    @AutoFill(OperationType.UPDATE)
    int update(Employee employee);

    /**
     * 此方法用于：根据id查询员工信息
     *
     * @param id 员工id
     * @return 员工信息
     */
    @Select("SELECT id, name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user FROM employee WHERE id = #{id}")
    Employee selectEmpById(Long id);
}
