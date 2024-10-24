package com.sky.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "员工对象")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "员工Id")
    private Long id;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "姓名")
    private String name;
    @Schema(description = "密码")
    private String password;
    @Schema(description = "手机号")
    private String phone;
    @Schema(description = "性别")
    private String sex;
    @Schema(description = "身份证号")
    private String idNumber;
    @Schema(description = "状态")
    private Integer status;
    @Schema(description = "创建时间")
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @Schema(description = "修改时间")
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    @Schema(description = "创建用户")
    private Long createUser;
    @Schema(description = "修改用户")
    private Long updateUser;
}
