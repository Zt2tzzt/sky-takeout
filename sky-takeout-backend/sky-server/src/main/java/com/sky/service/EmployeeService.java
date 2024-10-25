package com.sky.service;

import com.github.pagehelper.Page;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;

public interface EmployeeService {

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 此方法用于：新增员工的业务方法
     *
     * @param employeeDTO 新增的员工信息
     * @return 操作记录数
     */
    int save(EmployeeDTO employeeDTO);

    /**
     * 此方法用于：分页查询员工信息
     *
     * @param employeePageQueryDTO 分页查询条件
     * @return PageResult<Object>
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 此方法用于：启用/禁用员工账号
     *
     * @param status 启用/禁用状态
     * @param id     员工 Id
     * @return 操作记录数
     */
    int enableAndDisable(Integer status, Long id);

    /**
     * 此方法用于：根据 Id 查询员工信息
     *
     * @return 员工信息
     */
    Employee empById(Long id);

    /**
     * 此方法用于：编辑员工信息
     *
     * @param employeeDTO 编辑的员工信息
     * @return 操作记录数
     */
    int modifyEmp(EmployeeDTO employeeDTO);
}
