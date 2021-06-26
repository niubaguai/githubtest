package com.pan.vo.req;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 *  用于接收前端登录数据 vo
 */
@Data
public class LoginReqVo {

    @ApiModelProperty(value = "账号名")
    @NotBlank(message = "账号名不能为空")
    private String username;

    @ApiModelProperty(value = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;

    @ApiModelProperty(value = "登录状态(1.web登录 2.app登录)")
    @NotBlank(message = "登录类型不能为空")
    private String type;

}
