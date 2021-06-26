package com.pan.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class HomeInfoVo {
    @ApiModelProperty(value = "首页标题")
    private String title;
    @ApiModelProperty(value = "链接")
    private String href;
}
