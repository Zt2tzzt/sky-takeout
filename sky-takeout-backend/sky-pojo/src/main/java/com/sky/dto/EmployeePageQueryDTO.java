package com.sky.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "员工分页查询对象")
@Data
public class EmployeePageQueryDTO implements Serializable {
    @Schema(description = "员工姓名")
    private String name;

    @Schema(description = "页码")
    private int page;

    @Schema(description = "每页显示记录数")
    private int pageSize;
}
