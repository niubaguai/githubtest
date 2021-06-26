package com.pan.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Api(tags = "测试接口", description = "用于测试")
//@CrossOrigin(origins = "*",maxAge = 3600)  //解决跨域问题

public class TestController {

    @GetMapping("/test")
    @ApiOperation(value = "测试接口")
    public String testSwagger(){
        return "测试成功";
    }
}
