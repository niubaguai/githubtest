package com.pan.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class MenuInfoVo extends MenuChildEmptyVo{
    @ApiModelProperty(value = "菜单权限名称")
    private String title;
    @ApiModelProperty(value = "菜单权限图标")
    private String icon;
    @ApiModelProperty(value = "菜单权限链接")
    private String href;
    @ApiModelProperty(value = "菜单权限target")
    private String target;
    @ApiModelProperty(value = "子集集合")
    private List<?> child;
}
