package com.pan.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ClassRespNodeVo {

    @ApiModelProperty(value = "班级id")
    private String id;

    @ApiModelProperty(value = "班级名称")
    private String title;

    @ApiModelProperty("是否展开 默认true")
    private boolean spread=true;

    @ApiModelProperty(value = "子集叶子节点")
    private List<?> children;
}
