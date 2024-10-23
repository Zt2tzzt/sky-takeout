package com.sky.service;

import com.github.pagehelper.Page;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

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
     * @param employeePageQueryDTO 分页查询条件
     * @return PageResult<Object>
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);
}
