package com.pan.controller;


import com.pan.aop.annotation.MyLog;
import com.pan.contants.Constant;
import com.pan.entity.SysUser;
import com.pan.service.UserRoleService;
import com.pan.service.UserService;
import com.pan.utils.DataResult;
import com.pan.utils.JwtTokenUtil;
import com.pan.vo.req.*;
import com.pan.vo.resp.LoginRespVo;
import com.pan.vo.resp.PageVo;
import com.pan.vo.resp.UserOwnRoleRespVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@Api(tags = "组织模块-用户管理")
@Slf4j
//@CrossOrigin(origins = "*",maxAge = 3600)  //解决跨域问题
public class UserController {

    @Autowired
    private UserService userService;


    @ApiOperation(value = "用户登录接口")
    @PostMapping("/user/login")

    public DataResult<LoginRespVo> login(@RequestBody @Valid LoginReqVo vo){
        DataResult result = DataResult.success();
        result.setData(userService.login(vo));

        return result;
    }

    @PostMapping("/users")
    @ApiOperation(value = "分页查询用户接口")
    @MyLog(title = "组织模块-用户管理",action = "分页查询用户接口")
    @RequiresPermissions("sys:user:list")
    public DataResult<PageVo<SysUser>> pageInfo(@RequestBody UserPageReqVO vo){
        DataResult result=DataResult.success();
        result.setData(userService.pageInfo(vo));
        return result;
    }

    @PostMapping("/user/add")
    @ApiOperation(value = "添加用户接口")
    @MyLog(title = "组织模块-用户管理",action = "添加用户接口")
    @RequiresPermissions("sys:user:add")
    public DataResult<SysUser> userAdd(@RequestBody @Valid UserAddVo vo){
        DataResult<SysUser> result = DataResult.success();
        result.setData(userService.addUser(vo));

        return result;
    }

    @GetMapping("/user/role/{userId}")
    @ApiOperation(value = "赋予角色-获取用户拥有角色接口")
    @MyLog(title = "组织模块-用户管理",action = "赋予角色-获取用户拥有角色接口")
    @RequiresPermissions("sys:user:role:update")
    public DataResult<UserOwnRoleRespVo> getUserOwnRole(@PathVariable("userId") String userId){
        DataResult<UserOwnRoleRespVo> result = DataResult.success();
        result.setData(userService.getUserOwnRole(userId));

        return result;
    }

    @PutMapping("/user/roles")
    @ApiOperation(value = "保存用户拥有的角色信息接口")
    @MyLog(title = "组织模块-用户管理",action = "保存用户拥有的角色信息接口")
    @RequiresPermissions("sys:user:role:update")
    public DataResult saveUserOwnRoleId(@RequestBody @Valid UserRoleOperationReqVo vo){
        DataResult result = DataResult.success();
        userService.saveUserOwnRoleId(vo);

        return result;
    }

    /**
     * Jwt自动刷新接口
     */
    @GetMapping("/user/token")
    @ApiOperation(value = "自动刷新Token接口")
    @MyLog(title = "组织模块-用户管理",action = "自动刷新Token接口")
    public DataResult<String> refreshToken(HttpServletRequest request){
        String refreshToken = request.getHeader(Constant.REFRESH_TOKEN);
        DataResult<String> result = DataResult.success();
        result.setData(userService.refreshToken(refreshToken));

        return result;
    }

    /**
     * 用户修改信息接口
     */
    @PutMapping("/user")
    @ApiOperation(value = "用户修改信息接口")
    @MyLog(title = "组织模块-用户管理",action = "用户修改信息接口")
    @RequiresPermissions("sys:user:update")
    public DataResult updateUserInfo(@RequestBody @Valid UserUpdateReqVo vo, HttpServletRequest request){
        // 获取操作人Id ，通过 accessToken 获取
        String accessToken = request.getHeader(Constant.ACCESS_TOKEN);
        String operationId = JwtTokenUtil.getUserId(accessToken);
        // -
        DataResult result = DataResult.success();
        userService.updateUserInfo(vo, operationId);

        return result;

    }

    /**
     * 删除用户接口，批量删除用户接口（逻辑删除
     * 删除用户可以支持单个删除和批量删除，
     * 所以后端接口就把两个功能设计成一个接口了，
     * 而且用户属于敏感数据，一般制作逻辑删除不做物理删除
     *
     * 接口名与 修改接口一样 (我也不懂为啥- -！
     */
    @DeleteMapping("/user")
    @ApiOperation(value = "删除用户接口")
    @MyLog(title = "组织模块-用户管理",action = "删除用户接口")
    @RequiresPermissions("sys:user:delete")
    public DataResult deleteUserInfo(@RequestBody @ApiParam(value = "用户Id集合") List<String> userIdList, HttpServletRequest request){
        // 获取操作人的id
        String accessToken = request.getHeader(Constant.ACCESS_TOKEN);
        String operationId = JwtTokenUtil.getUserId(accessToken);
        // -
        DataResult result = DataResult.success();
        // 执行删除操作
        userService.deleteUserInfo(userIdList, operationId);

        return result;
    }

    @GetMapping("/user/logout")
    @ApiOperation(value = "用户退出登录接口")
    public DataResult logout(HttpServletRequest request){
        try {
            // 获取 accessToken 和 refreshToken
            String accessToken = request.getHeader(Constant.ACCESS_TOKEN);
            String refreshToken = request.getHeader(Constant.REFRESH_TOKEN);
            userService.logout(accessToken, refreshToken);
        } catch (Exception e) {
            log.error("logout:{}",e);
        }

        return DataResult.success();
    }

    /**
     * 获取用户详情信息接口
     */
    @GetMapping("/user/info")
    @ApiOperation(value = "获取用户详情信息接口")
    @MyLog(title = "组织管理-用户管理",action = "用户信息详情接口")
    public DataResult<SysUser> getUserDetail(HttpServletRequest request){
        String accessToken=request.getHeader(Constant.ACCESS_TOKEN);
        String id=JwtTokenUtil.getUserId(accessToken);
        DataResult result=DataResult.success();
        result.setData(userService.detailInfo(id));
        return result;
    }

    /**
     * 修改用户详情信息接口
     */
    @PutMapping("/user/info")
    @ApiOperation(value = "修改用户详情信息接口")
    @MyLog(title = "组织管理-用户管理",action = "保存个人信息接口")
    public DataResult updateUserDetail(@RequestBody UserUpdateDetailInfoReqVo vo, HttpServletRequest request){
        //获取用户Id
        String accessToken=request.getHeader(Constant.ACCESS_TOKEN);
        String id=JwtTokenUtil.getUserId(accessToken);
        //业务层操作
        userService.updateUserDetail(vo, id);
        DataResult result = DataResult.success();

        return result;
    }

    /**
     * 修改用户密码接口
     */
    @PutMapping("/user/pwd")
    @ApiOperation(value = "修改用户密码接口")
    @MyLog(title = "组织管理-用户管理",action = "修改用户密码接口")
    public DataResult updateUserPwd(@RequestBody UpdatePasswordReqVo vo, HttpServletRequest request){
        // 获取 accessToken 和 refreshToken
        // 用于验证用户合法性和 刷新token
        String accessToken=request.getHeader(Constant.ACCESS_TOKEN);
        String refreshToken=request.getHeader(Constant.REFRESH_TOKEN);
        userService.updateUserPwd(vo, accessToken, refreshToken);
        DataResult result = DataResult.success();

        return result;
    }
}
