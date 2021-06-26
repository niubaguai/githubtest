package com.pan.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class UserRoleOperationReqVo {

    @ApiModelProperty(value = "用户Id")
    @NotBlank(message = "用户Id不能为空")
    private String userId;

    @ApiModelProperty(value = "用户拥有角色的Id")
    @NotEmpty(message = "角色id集合不能为空")
    private List<String> roleIds;
}
