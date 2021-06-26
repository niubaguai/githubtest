package com.pan.controller;

import com.pan.aop.annotation.MyLog;
import com.pan.contants.Constant;
import com.pan.entity.SysPermission;
import com.pan.service.PermissionService;
import com.pan.service.RedisService;
import com.pan.utils.DataResult;
import com.pan.vo.req.PermissionAddReqVO;
import com.pan.vo.req.PermissionUpdateReqVo;
import com.pan.vo.resp.LayuiMiniPermissionVo;
import com.pan.vo.resp.PermissionRespNodeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
@Api(tags = "组织模块-菜单权限管理")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RedisService redisService;

    @GetMapping("/permission")
    @ApiOperation(value = "查询所有权限菜单接口")
    @MyLog(title = "组织管理-菜单权限管理",action = "查询所有权限菜单接口")
    @RequiresPermissions("sys:permission:list")
    public DataResult<List<SysPermission>> getAllMenusPermission(){
        DataResult<List<SysPermission>> result = DataResult.success();
        result.setData(permissionService.getAllMenusPermissions());
        return result;
    }

    @GetMapping("/permission/tree")
    @ApiOperation(value = "只递归查询到菜单接口")
    @MyLog(title = "组织管理-菜单权限管理",action = "只递归查询到菜单接口")
    @RequiresPermissions(value = {"sys:permission:update","sys:permission:add"},logical = Logical.OR)
    public DataResult<List<PermissionRespNodeVO>> getAllPermissionTreeExBtn(){
        DataResult result=DataResult.success();
        result.setData(permissionService.selectAllMenuByTree());
        return result;
    }

    @PostMapping("/permission/add")
    @ApiOperation(value = "新增权限菜单接口")
    @MyLog(title = "组织管理-菜单权限管理",action = "新增权限菜单接口")
    @RequiresPermissions("sys:permission:add")
    public DataResult<SysPermission> addPermission(@RequestBody @Valid PermissionAddReqVO vo){
        DataResult result=DataResult.success();
        result.setData(permissionService.addPermission(vo));
        return result;
    }

    @GetMapping("/permission/tree/all")
    @ApiOperation(value = "递归查询到按钮接口")
    @MyLog(title = "组织管理-菜单权限管理",action = "递归查询到按钮接口")
    @RequiresPermissions(value = {"sys:role:update","sys:role:add"},logical = Logical.OR)
    public DataResult<List<PermissionRespNodeVO>> getAllPermissionTree(){
        DataResult result = DataResult.success();
        result.setData(permissionService.selectAllByTree());

        return result;
    }

    @PutMapping("/permission")
    @ApiOperation(value = "修改权限信息接口")
    @MyLog(title = "组织管理-菜单权限管理",action = "修改权限信息接口")
    @RequiresPermissions("sys:permission:update")
    public DataResult updatePemissionInfo(@RequestBody @Valid PermissionUpdateReqVo vo){
        DataResult result = DataResult.success();
        permissionService.updatePemissionInfo(vo);

        return result;

    }

    @DeleteMapping("/permission/{id}")
    @ApiOperation(value = "删除权限信息接口")
    @MyLog(title = "组织管理-菜单权限管理",action = "删除权限信息接口")
    @RequiresPermissions("sys:permission:delete")
    public DataResult deletePermissionInfo(@PathVariable("id") String permissionId){
        DataResult result = DataResult.success();
        permissionService.deletePermissionInfo(permissionId);

        return result;
    }

    /**
     * 校验权限
     */
    @GetMapping("/permission/owns")
    @ApiOperation(value = "校验权限接口")
    @MyLog(title = "组织管理-菜单权限管理",action = "校验权限接口")
    public List<String> ownsPermission(){
        // 获取该用户所拥有的权限
        // 通过redis获取用户id
        // 解决不能通过HttpServletRequest request 获取用户id的问题
        // 在登陆的时候将userId存入redis ，然后在过去菜单权限页面的时候再获取
        String userId = (String) redisService.get(Constant.USERID_KEY);

        // 通过用户id查询该用户所拥有的权限
        List<String> permissions = permissionService.getPermissionByUserId(userId);

        return permissions;
    }
}
