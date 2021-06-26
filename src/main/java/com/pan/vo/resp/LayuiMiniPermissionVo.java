package com.pan.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class LayuiMiniPermissionVo {
    @ApiModelProperty(value = "homeInfo")
    private HomeInfoVo homeInfo;

    @ApiModelProperty(value = "logoInfo")
    private LogoInfoVo logoInfo;

    @ApiModelProperty(value = "menuInfo")
    private List<MenuInfoVo> menuInfo;
}
