package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Tag(name = "员工管理接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());

        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims
        );

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @Operation(summary = "员工退出")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 此方法用于：新增员工
     *
     * @param employeeDTO 新增员工数据
     * @return Result
     */
    @Operation(summary = "新增员工")
    @PostMapping
    public Result<String> save(@RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工，员工数据：{}", employeeDTO);

        int num = employeeService.save(employeeDTO);
        return Result.success("成功插入" + num + "条数据");
    }

    /**
     * 此方法用于：分页查询员工信息
     *
     * @param employeePageQueryDTO 分页查询条件
     * @return Result<PageResult < Object>>
     */
    @Operation(summary = "分页查询员工信息")
    @GetMapping("/page")
    public Result<PageResult<Employee>> pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("分页查询员工信息，查询条件为：{}", employeePageQueryDTO);

        Page<Employee> PageEmps = employeeService.pageQuery(employeePageQueryDTO);
        PageResult<Employee> result = new PageResult<>(PageEmps.getTotal(), PageEmps.getResult());
        return Result.success(result);
    }

    /**
     * 此方法用于：启用/禁用员工账号
     *
     * @param status 状态
     * @param id     员工 Id
     * @return Result
     */
    @Operation(summary = "启用禁用员工账号")
    @PostMapping("/status/{status}")
    public Result<String> enableAndDisable(@PathVariable Integer status, Long id) {
        log.info("启用/禁用员工账号，员工id为：{},状态为：{}", id, status);

        int num = employeeService.enableAndDisable(status, id);
        return num > 0 ? Result.success("成功修改" + num + "条数据") : Result.error("修改失败");
    }
}
