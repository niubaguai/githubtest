package com.pan.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserUpdateReqVo {

    @ApiModelProperty(value = "用户Id")
    @NotBlank(message = "用户Id不能为空")
    private String userId;

    @ApiModelProperty(value = "账号")
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "班级（学院）")
    private String classId;

    @ApiModelProperty(value = "状态")
    private Integer status;
}
