package com.pan.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用于响应给前端的 vo
 */
@Data
public class LoginRespVo {

    @ApiModelProperty(value = "业务访问token")
    private String accessToken;
    @ApiModelProperty(value = "业务token刷新凭证")
    private String refreshToken;
    @ApiModelProperty(value = "用户id")
    private String userId;

}
