package com.pan.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LogoInfoVo {
    @ApiModelProperty(value = "首页logo名称")
    private String title;
    @ApiModelProperty(value = "首页logo图片")
    private String image;
    @ApiModelProperty(value = "首页logo链接")
    private String href;
}
