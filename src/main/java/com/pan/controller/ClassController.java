package com.pan.controller;

import com.pan.aop.annotation.MyLog;
import com.pan.entity.SysClass;
import com.pan.service.ClassService;
import com.pan.utils.DataResult;
import com.pan.vo.req.ClassAddVo;
import com.pan.vo.req.ClassUpdateVo;
import com.pan.vo.resp.ClassRespNodeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Api(tags = "组织模块-班级(学院)管理")
@RequestMapping("/api")
public class ClassController {

    @Autowired
    private ClassService classService;

    @GetMapping("/class")
    @ApiOperation(value = "获取所有班级信息接口")
    @MyLog(title = "组织管理-班级管理",action = "获取所有班级信息接口")
    @RequiresPermissions("sys:class:list")
    public DataResult<List<SysClass>> getAllClassInfo(){
        DataResult<List<SysClass>> result = DataResult.success();
        result.setData(classService.getALlClassInfo());

        return result;
    }

    @GetMapping("/class/tree")
    @ApiOperation(value = "获取班级学院树接口")
    @MyLog(title = "组织管理-班级管理",action = "获取班级学院树接口")
    @RequiresPermissions(value = {"sys:user:update","sys:user:add","sys:class:add","sys:class:update"},logical = Logical.OR)
    public DataResult<List<ClassRespNodeVo>> getClassTree(@RequestParam(required = false) String classId){
        DataResult<List<ClassRespNodeVo>> result = DataResult.success();
        result.setData(classService.getClassTree(classId));

        return result;
    }

    @PostMapping("/class/add")
    @ApiOperation(value = "添加班级信息接口")
    @MyLog(title = "组织管理-班级管理",action = "添加班级信息接口")
    @RequiresPermissions("sys:class:add")
    public DataResult<SysClass> addClassInfo(@RequestBody @Valid ClassAddVo vo){
        DataResult<SysClass> result = DataResult.success();
        result.setData(classService.addClassInfo(vo));

        return result;
    }

    @PutMapping("/class/edit")
    @ApiOperation(value = "修改班级信息接口")
    @MyLog(title = "组织管理-班级管理",action = "修改班级信息接口")
    @RequiresPermissions("sys:class:update")
    public DataResult updateClassInfo(@RequestBody @Valid ClassUpdateVo vo){
        DataResult result = DataResult.success();
        classService.updateClassInfo(vo);
        return result;
    }

    @DeleteMapping("/class/{id}")
    @ApiOperation(value = "删除班级信息接口")
    @MyLog(title = "组织管理-班级管理",action = "删除班级信息接口")
    @RequiresPermissions("sys:class:delete")
    public DataResult deleteClassInfo(@PathVariable("id") String classId){
        DataResult result = DataResult.success();
        classService.deleteClassInfo(classId);
        return result;
    }
}
