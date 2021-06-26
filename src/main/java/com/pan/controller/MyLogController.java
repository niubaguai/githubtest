package com.pan.controller;

import com.pan.entity.SysLog;
import com.pan.service.LogService;
import com.pan.utils.DataResult;
import com.pan.vo.req.SysLogPageReqVo;
import com.pan.vo.resp.PageVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "系统模块-系统操作日志管理")
@RequestMapping("/api")
public class MyLogController {

    @Autowired
    private LogService logService;

    @PostMapping("/logs")
    @ApiOperation(value = "获取所有日志信息并分页接口")
    public DataResult<PageVo<SysLog>> pageInfo(@RequestBody SysLogPageReqVo vo){
        DataResult result = DataResult.success();
        result.setData(logService.pageInfo(vo));

        return result;
    }
}
