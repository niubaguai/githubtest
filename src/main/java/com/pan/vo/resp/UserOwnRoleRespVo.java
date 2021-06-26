package com.pan.vo.resp;

import com.pan.entity.SysRole;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.util.List;

@Data
public class UserOwnRoleRespVo {

    @ApiModelProperty(value = "所有角色信息,用于穿梭框左侧")
    private List<SysRole> allRoles;

    @ApiModelProperty(value = "某个用户拥有的角色信息id，用于穿梭框右侧")
    private List<String> ownRoles;
}
