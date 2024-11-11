package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Objects;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeMapper employeeMapper;

    @Autowired
    public EmployeeServiceImpl(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    /**
     * 员工登录
     *
     * @param employeeLoginDTO 登录时提交的用户名、密码
     * @return Employee
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //密码比对
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (Objects.equals(employee.getStatus(), StatusConstant.DISABLE)) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 此方法用于：新增员工的业务方法
     *
     * @param employeeDTO 新增的员工信息
     * @return 操作记录数
     */
    @Override
    public int save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();

        // 使用对象属性拷贝，前提是两个对象中的属性名一致
        BeanUtils.copyProperties(employeeDTO, employee);

        // 数据补充
        employee.setStatus(StatusConstant.ENABLE); // 设置状态
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes())); // 设置密码
        //employee.setCreateTime(LocalDateTime.now()); // 设置创建事件
        //employee.setUpdateTime(LocalDateTime.now()); // 设置修改事件
        //Long currentId = BaseContext.getCurrentId(); // 取出 ThreadLocal 中保存的员工 Id
        //employee.setCreateUser(currentId); // 设置创建用户
        //employee.setUpdateUser(currentId); // 设置修改用户

        return employeeMapper.insert(employee);
    }

    /**
     * 此方法用于：分页查询员工信息
     *
     * @param employeePageQueryDTO 分页查询条件
     * @return PageResult<Object>
     */
    @Override
    public Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        // 开始分页查询
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());

        return employeeMapper.selectEmpByPage(employeePageQueryDTO);
    }

    /**
     * 此方法用于：启用/禁用员工账号
     *
     * @param status 启用/禁用状态
     * @param id     员工 Id
     * @return 操作记录数
     */
    @Override
    public int enableAndDisable(Integer status, Long id) {
        Employee employee = Employee.builder()
                .status(status)
                .id(id).build();

        // 动态更新，在一个 UPDATE 语句中，处理多个字段的跟新。
        return employeeMapper.update(employee);
    }

    /**
     * 此方法用于：根据 Id 查询员工信息
     *
     * @return 员工信息
     */
    @Override
    public Employee empById(Long id) {
        Employee employee = employeeMapper.selectEmpById(id);
        employee.setPassword("****");
        return employee;
    }

    /**
     * 此方法用于：编辑员工信息
     *
     * @param employeeDTO 编辑的员工信息
     * @return 操作记录数
     */
    @Override
    public int modifyEmp(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();

        // 属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);
        // 补充数据
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(BaseContext.getCurrentId());

        return employeeMapper.update(employee);
    }
}
