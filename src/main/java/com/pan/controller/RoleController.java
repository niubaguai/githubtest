package com.pan.controller;

import com.pan.aop.annotation.MyLog;
import com.pan.entity.SysRole;
import com.pan.service.RoleService;
import com.pan.utils.DataResult;
import com.pan.vo.req.RoleAddReqVo;
import com.pan.vo.req.RolePageReqVo;
import com.pan.vo.req.RoleUpdateReqVo;
import com.pan.vo.resp.PageVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@Api(tags = "组织模块-角色管理")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/roles")
    @ApiOperation(value = "查询所有角色信息接口")
    @MyLog(title = "组织模块-角色管理",action = "查询所有角色信息接口")
    @RequiresPermissions("sys:role:list")
    public DataResult<PageVo<SysRole>> rolesInfo(@RequestBody @Valid RolePageReqVo vo){
        DataResult<PageVo<SysRole>> result = DataResult.success();
        result.setData(roleService.getAllRoles(vo));
        return result;
    }

    @PostMapping("/roles/add")
    @ApiOperation(value = "新增角色信息接口")
    @MyLog(title = "组织模块-角色管理",action = "新增角色信息接口")
    @RequiresPermissions("sys:role:add")
    public DataResult<SysRole> addRoles(@RequestBody @Valid RoleAddReqVo vo){
        DataResult<SysRole> result = DataResult.success();
        result.setData(roleService.addRole(vo));

        return result;
    }

    @GetMapping("/roles/{id}")
    @ApiOperation(value = "查询角色详情接口")
    @MyLog(title = "组织模块-角色管理",action = "查询角色详情接口")
    @RequiresPermissions("sys:role:detail")
    public DataResult<SysRole> detailInfo(@PathVariable("id") String id){
        DataResult<SysRole> result = DataResult.success();
        result.setData(roleService.detailInfo(id));
        return result;
    }

    @PutMapping("/role")
    @ApiOperation(value = "更新角色信息接口")
    @MyLog(title = "组织模块-角色管理",action = "更新角色信息接口")
    @RequiresPermissions("sys:role:update")
    public DataResult updateRoleInfo(@RequestBody @Valid RoleUpdateReqVo vo){
        DataResult result = DataResult.success();
        roleService.updateRoleInfo(vo);

        return result;
    }

    @DeleteMapping("/role/{id}")
    @ApiOperation(value = "删除角色信息接口")
    @MyLog(title = "组织模块-角色管理",action = "删除角色信息接口")
    @RequiresPermissions("sys:role:delete")
    public DataResult deleteRoleInfo(@PathVariable("id") String id){
        DataResult result = DataResult.success();
        roleService.deleteRoleInfo(id);
        return result;
    }

//    @DeleteMapping("/role")
//    @ApiOperation(value = "批量删除角色信息接口")
//    public DataResult deleteRolesInfo(@RequestBody @ApiParam(value = "角色id集合") List<String> roleIdList){
//        DataResult result = DataResult.success();
//        roleService.deleteRolesInfo(roleIdList);
//        return result;
//    }

}
