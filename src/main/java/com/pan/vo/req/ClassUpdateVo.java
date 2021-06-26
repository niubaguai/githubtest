package com.pan.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ClassUpdateVo {

    @ApiModelProperty(value = "班级id")
    @NotBlank(message = "班级id不能为空")
    private String id;

    @ApiModelProperty(value = "班级名称")
    private String name;

    @ApiModelProperty(value = "父级id 一级为 0")
    private String pid;

    @ApiModelProperty(value = "负责人名称")
    private String teacherName;

    @ApiModelProperty(value = "负责人电话")
    private String phone;

    @ApiModelProperty(value = "机构状态(1:正常；0:弃用)")
    private Integer status;
}
