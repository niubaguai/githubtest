package com.pan.service.impl;

import com.pan.contants.Constant;
import com.pan.dao.SysPermissionDao;
import com.pan.dao.SysRolePermissionDao;
import com.pan.dao.SysUserRoleDao;
import com.pan.entity.SysPermission;
import com.pan.exception.BusinessException;
import com.pan.exception.code.BaseResponseCode;
import com.pan.service.PermissionService;
import com.pan.service.RedisService;
import com.pan.service.RolePermissionService;
import com.pan.service.UserRoleService;
import com.pan.utils.TokenSetting;
import com.pan.vo.req.PermissionAddReqVO;
import com.pan.vo.req.PermissionUpdateReqVo;
import com.pan.vo.resp.*;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private SysPermissionDao sysPermissionDao;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private TokenSetting tokenSettings;

    @Override
    public List<PermissionRespNodeVO> permissionTreeList(String userId) {
//        List<SysPermission> list=getPermissions(userId);
        return getTree(getAllMenusPermissions(),true);
    }

    @Override
    public List<PermissionRespNodeVO> selectAllByTree() {
        return getTree(getAllMenusPermissions(),false);
    }


    /**
     * 获取 初始化 左侧导航栏 数据
     * @return
     */
    @Override
    public List<LayuiMiniPermissionVo> getLayuiMiniTree(String userId) {
        List<LayuiMiniPermissionVo> list = new ArrayList<>();

        LayuiMiniPermissionVo vo = new LayuiMiniPermissionVo();
        // 设置 homeInfo
        HomeInfoVo homeInfoVo = new HomeInfoVo();
        homeInfoVo.setTitle("首页");
        homeInfoVo.setHref("");
        vo.setHomeInfo(homeInfoVo);

        // 设置 logoInfo
        LogoInfoVo logoInfoVo = new LogoInfoVo();
        logoInfoVo.setTitle("懋牌后台管理系统");
        logoInfoVo.setImage("");
        logoInfoVo.setHref("");
        vo.setLogoInfo(logoInfoVo);


        List<MenuInfoVo> menuInfoVoList = getLayuiTree(getAllMenusPermissions(), true);
        MenuInfoVo menuInfoVo = new MenuInfoVo();
        List<MenuInfoVo> menuList = new ArrayList<>();
        menuInfoVo.setTitle("常规管理");
        menuInfoVo.setHref("");
        menuInfoVo.setTarget("_self");
        menuInfoVo.setIcon("fa fa-address-book");
        menuInfoVo.setChild(menuInfoVoList);
        menuList.add(menuInfoVo);
        vo.setMenuInfo(menuList);

        list.add(vo);
        return list;
    }

    @Override
    public LayuiMiniPermissionVo getLayuiTree(String userId) {

        LayuiMiniPermissionVo vo = new LayuiMiniPermissionVo();
        // 设置 homeInfo
        HomeInfoVo homeInfoVo = new HomeInfoVo();
        homeInfoVo.setTitle("首页");
        homeInfoVo.setHref("");
        vo.setHomeInfo(homeInfoVo);

        // 设置 logoInfo
        LogoInfoVo logoInfoVo = new LogoInfoVo();
        logoInfoVo.setTitle("MAO UI");
        logoInfoVo.setImage("../resource/images/logo.png");
        logoInfoVo.setHref("");
        vo.setLogoInfo(logoInfoVo);


        List<MenuInfoVo> menuInfoVoList = getLayuiTree(getPermissions(userId), true);
        MenuInfoVo menuInfoVo = new MenuInfoVo();
        List<MenuInfoVo> menuList = new ArrayList<>();
        menuInfoVo.setTitle("常规管理");
        menuInfoVo.setHref("");
        menuInfoVo.setTarget("_self");
        menuInfoVo.setIcon("fa fa-address-book");
        menuInfoVo.setChild(menuInfoVoList);
        menuList.add(menuInfoVo);
        vo.setMenuInfo(menuList);
        return vo;
    }

    /**
     * 通过用户Id获取该用户所拥有的权限信息
     */
    @Override
    public List<SysPermission> getPermissions(String userId) {
        List<String> roleIdsByUserId = userRoleService.getRoleIdByUserId(userId);
        if(roleIdsByUserId.isEmpty()){
            return null;
        }
        List<String> permissionIdsByRoleIds = rolePermissionService.getPermissionIdByRoleIds(roleIdsByUserId);
        if (permissionIdsByRoleIds.isEmpty()){
            return null;
        }
        List<SysPermission> result=sysPermissionDao.getPermsByPermissionIds(permissionIdsByRoleIds);
        return result;
    }


    /**
     * 添加菜单权限
     * @param vo
     * @return
     */
    @Override
    public SysPermission addPermission(PermissionAddReqVO vo) {
        SysPermission permission = new SysPermission();
        BeanUtils.copyProperties(vo, permission);
        // 检验菜单权限是否符合标准
        verifyForm(permission);
        // 设置ID
        permission.setId(UUID.randomUUID().toString());
        // 设置创建时间
        permission.setCreateTime(new Date());
        // 插入进数据库
        int count = sysPermissionDao.insertSelective(permission);
        if (count != 1){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }

        return permission;
    }

    /**
     * 操作后的菜单类型是目录的时候 父级必须为目录
     * 操作后的菜单类型是菜单的时候，父类必须为目录类型
     * 操作后的菜单类型是按钮的时候 父类必须为菜单类型
     * @param sysPermission
     */
    private void verifyForm(SysPermission sysPermission){
        // 获取 父类id
        SysPermission parent=sysPermissionDao.selectByPrimaryKey(sysPermission.getPid());
        switch (sysPermission.getType()){
            case 1:
                if(parent!=null){
                    if(parent.getType()!=1){
                        throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_CATALOG_ERROR);
                    }
                }else if (!sysPermission.getPid().equals("0")){
                    throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_CATALOG_ERROR);
                }
                break;
            case 2:
                if(parent==null||parent.getType()!=1){
                    throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_MENU_ERROR);
                }
                if(StringUtils.isEmpty(sysPermission.getUrl())){
                    throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_URL_NOT_NULL);
                }
                break;
            case 3:
                if(parent==null||parent.getType()!=2){
                    throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_BTN_ERROR);
                }
                if(StringUtils.isEmpty(sysPermission.getPerms())){
                    throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_URL_PERMS_NULL);
                }
                if(StringUtils.isEmpty(sysPermission.getUrl())){
                    throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_URL_NOT_NULL);
                }
                if(StringUtils.isEmpty(sysPermission.getMethod())){
                    throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_URL_METHOD_NULL);
                }
                if(StringUtils.isEmpty(sysPermission.getCode())){
                    throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_URL_CODE_NULL);
                }
                break;
        }
    }



    @Override
    public List<SysPermission> getAllMenusPermissions() {

        List<SysPermission> result = sysPermissionDao.getAllMenusPermissions();

        if(!result.isEmpty()){
            // 遍历result ，每一个都是一个permission对象
            for (SysPermission permission : result){
                // 查询 父类
                SysPermission parent = sysPermissionDao.selectByPrimaryKey(permission.getPid());
                if(parent != null){
                    permission.setPidName(parent.getName());
                }
            }
        }

        return result;
    }

    @Override
    public List<PermissionRespNodeVO> selectAllMenuByTree() {
        List<SysPermission> list=sysPermissionDao.getAllMenusPermissions();
        List<PermissionRespNodeVO> result=new ArrayList<>();
        PermissionRespNodeVO respNodeVO=new PermissionRespNodeVO();
        respNodeVO.setId("0");
        respNodeVO.setTitle("默认顶级菜单");
        respNodeVO.setChildren(getTree(list,true));
        result.add(respNodeVO);
        return result;
    }



    /**
     * type=true 递归遍历到菜单
     * type=false 递归遍历到按钮
     * @Author:      小霍
     * @UpdateUser:
     * @Version:     0.0.1
     * @param all
     * @param type
     * @return       java.util.List<com.yingxue.lesson.vo.resp.PermissionRespNodeVO>
     * @throws
     */
    private List<MenuInfoVo> getLayuiTree(List<SysPermission> all,boolean type){
        List<MenuInfoVo> menuInfoVoList = new ArrayList<>();
        if(all==null||all.isEmpty()){
            return menuInfoVoList;
        }

        for(SysPermission sysPermission:all){
            if(sysPermission.getPid().equals("0")){
                // 设置 menuInfo
                MenuInfoVo menuInfoVo = new MenuInfoVo();
                menuInfoVo.setTitle(sysPermission.getName());
                menuInfoVo.setIcon("");
                menuInfoVo.setHref(sysPermission.getUrl());
//                System.out.println(sysPermission.getVisit());
                menuInfoVo.setTarget("_self");
                if(type){
                    menuInfoVo.setChild(getLayuiChildExBtn(sysPermission.getId(),all));
                }else {
                    menuInfoVo.setChild(getLayuiChild(sysPermission.getId(),all));
                }
                menuInfoVoList.add(menuInfoVo);
            }
        }
        return menuInfoVoList;
    }

    private List<PermissionRespNodeVO> getTree(List<SysPermission> all,boolean type){

        List<PermissionRespNodeVO> list=new ArrayList<>();
        if(all==null||all.isEmpty()){
            return list;
        }
        for(SysPermission sysPermission:all){
            if(sysPermission.getPid().equals("0")){
                PermissionRespNodeVO respNodeVO=new PermissionRespNodeVO();
                BeanUtils.copyProperties(sysPermission,respNodeVO);
                respNodeVO.setTitle(sysPermission.getName());
                if(type){
                    respNodeVO.setChildren(getChildExBtn(sysPermission.getId(),all));
                }else {
                    respNodeVO.setChildren(getChild(sysPermission.getId(),all));
                }

                list.add(respNodeVO);
            }
        }
        return list;
    }
    /**
     * 递归遍历所有数据
     * @Author:      小霍
     * @UpdateUser:
     * @Version:     0.0.1
     * @param id
     * @param all
     * @return       java.util.List<com.yingxue.lesson.vo.resp.PermissionRespNodeVO>
     * @throws
     */
    private List<MenuInfoVo> getLayuiChild(String id,List<SysPermission> all){
        List<MenuInfoVo> menuInfoVoList = new ArrayList<>();
        for (SysPermission s: all) {
            if(s.getPid().equals(id)){
                MenuInfoVo menuInfoVo = new MenuInfoVo();
                menuInfoVo.setTitle(s.getName());
                menuInfoVo.setIcon("");
                menuInfoVo.setHref(s.getUrl());
                menuInfoVo.setTarget("_self");
                menuInfoVo.setChild(getChild(s.getId(), all));
                menuInfoVoList.add(menuInfoVo);
            }
        }
        return menuInfoVoList;
    }

    private List<PermissionRespNodeVO> getChild(String id,List<SysPermission> all){

        List<PermissionRespNodeVO> list=new ArrayList<>();
        for (SysPermission s:
                all) {
            if(s.getPid().equals(id)){
                PermissionRespNodeVO respNodeVO=new PermissionRespNodeVO();
                BeanUtils.copyProperties(s,respNodeVO);
                respNodeVO.setTitle(s.getName());
                respNodeVO.setChildren(getChild(s.getId(),all));
                list.add(respNodeVO);
            }
        }
        return list;
    }
    /**
     * 只递归到菜单
     * @Author:      小霍
     * @UpdateUser:
     * @Version:     0.0.1
     * @param id
     * @param all
     * @return       java.util.List<com.yingxue.lesson.vo.resp.PermissionRespNodeVO>
     * @throws
     */
    private List<MenuInfoVo> getLayuiChildExBtn(String id,List<SysPermission> all){
        List<MenuInfoVo> menuInfoVoList = new ArrayList<>();
        for (SysPermission s: all) {
            if(s.getPid().equals(id)&&s.getType()!=3){
                MenuInfoVo menuInfoVo = new MenuInfoVo();
                menuInfoVo.setTitle(s.getName());
                menuInfoVo.setIcon("");
                menuInfoVo.setHref(s.getUrl());
                menuInfoVo.setTarget("_self");
                menuInfoVo.setChild(getChildExBtn(s.getId(), all));
                menuInfoVoList.add(menuInfoVo);
            }
        }
        return menuInfoVoList;
    }

    private List<PermissionRespNodeVO> getChildExBtn(String id,List<SysPermission> all){
        List<PermissionRespNodeVO> list=new ArrayList<>();
        for (SysPermission s:
                all) {
            if(s.getPid().equals(id)&&s.getType()!=3){
                PermissionRespNodeVO respNodeVO=new PermissionRespNodeVO();
                BeanUtils.copyProperties(s,respNodeVO);
                respNodeVO.setTitle(s.getName());
                respNodeVO.setChildren(getChildExBtn(s.getId(),all));
                list.add(respNodeVO);
            }
        }
        return list;
    }

    /**
     * 更新权限
     */
    @Override
    public void updatePemissionInfo(PermissionUpdateReqVo vo) {
        SysPermission updatePermission = new SysPermission();
        BeanUtils.copyProperties(vo, updatePermission);
        // 校验传过来的数据
        verifyForm(updatePermission);
        // 通过id 获取当前permission对象
        // 如果为空，则传过来的id有误，
        SysPermission sysPermission = sysPermissionDao.selectByPrimaryKey(vo.getId());
        if (sysPermission ==null){
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }

        //所属菜单发生了变化或者权限状态发生了变化要校验该权限是否存在子集
        // 如果存在子集，则抛出异常
        if (!sysPermission.getPid().equals(vo.getPid()) || !sysPermission.getStatus().equals(vo.getStatus())){
            List<SysPermission> list = sysPermissionDao.selectChild(vo.getId());
            // 如果有子集
            if (!list.isEmpty()){
                throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_UPDATE);
            }
        }

        updatePermission.setUpdateTime(new Date());

        int i = sysPermissionDao.updateByPrimaryKeySelective(updatePermission);
        if (i != 1) {
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }


        //判断授权标识符是否发生了变化(权限标识符发生了变化，或者权限状态发生了变化
        // 1. 通过权限id获取角色id
        // 2. 通过角色id获取用户id
        // 3. 获取的用户id打上标记存入redis，用于自动刷新token
        if (!sysPermission.getPerms().equals(vo.getPerms()) || !sysPermission.getStatus().equals(vo.getStatus())) {
            // 1. 通过权限id获取角色id
            List<String> roleIdList = rolePermissionService.getRoleIdByPermissionId(vo.getId());
            if (!roleIdList.isEmpty()){
                // 2. 通过角色id获取用户id
                List<String> userIdList = userRoleService.getUserIdByRoleId(roleIdList);
                if (!userIdList.isEmpty()){
                    // 3. 获取的用户id打上标记存入redis，用于自动刷新token
                    for (String userId : userIdList){
                        // 为每一个用户id打标记存入redis
                        redisService.set(Constant.JWT_REFRESH_KEY+userId, userId, tokenSettings.getAccessTokenExpireTime().toMillis(), TimeUnit.MILLISECONDS);
                        redisService.delete(Constant.IDENTIFY_CACHE_KEY+userId);
                    }
                }
            }
        }

    }

    /**
     * 删除权限  逻辑删除
     * 同时还要删除 权限与角色表的信息 ，这个是真删除
     */
    @Override
    public void deletePermissionInfo(String permissionId) {
        SysPermission sysPermission = sysPermissionDao.selectByPrimaryKey(permissionId);
        // 如果权限对象为空 抛出异常
        if (null == sysPermission) {
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }
        // 如果这个权限 有子集，不能删除
        List<SysPermission> child = sysPermissionDao.selectChild(permissionId);
        if (!child.isEmpty()){
            throw new BusinessException(BaseResponseCode.ROLE_PERMISSION_RELATION);
        }

        sysPermission.setDeleted(0);
        sysPermission.setUpdateTime(new Date());
        int i = sysPermissionDao.updateByPrimaryKeySelective(sysPermission);
        if (i!=1){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }

        // 删除 权限与角色表中的信息
        List<String> roleList = rolePermissionService.getRoleIdByPermissionId(permissionId);

        rolePermissionService.deleteByPermissionId(permissionId);
        if (!roleList.isEmpty()) {
            // 通过角色id获取用户id
            List<String> userIds = userRoleService.getUserIdByRoleId(roleList);
            if (!userIds.isEmpty()) {
                for (String userId : userIds) {
                    redisService.set(Constant.JWT_REFRESH_KEY+userId, userId, tokenSettings.getAccessTokenExpireTime().toMillis(), TimeUnit.MILLISECONDS);

                    /**
                     * 清楚用户授权数据缓存
                     */
                    redisService.delete(Constant.IDENTIFY_CACHE_KEY+userId);
                }
            }
        }
    }

    /**
     * 通过用户id获取他所拥有的权限信息  如 sys:permission:add
     * 用于授权用
     * */

    @Override
    public List<String> getPermissionByUserId(String userId) {
        // 第一步：通过用户id获取角色id， 因为只有角色id跟用户id相关联
        List<String> roleIdByUserId = userRoleService.getRoleIdByUserId(userId);
        // 判断如果为空，返回null
        if (roleIdByUserId.isEmpty()) {
            return null;
        }
        // 第二步：在通过角色id获取权限id
        List<String> permissionIdByRoleId = rolePermissionService.getPermissionIdByRoleIds(roleIdByUserId);
        // 判断如果为空，返回null
        if (permissionIdByRoleId.isEmpty()) {
            return null;
        }
        // 第三步：通过权限id查询permission对象，获取它的 perms属性 即权限信息
        List<SysPermission> permissions = sysPermissionDao.getPermsByPermissionIds(permissionIdByRoleId);
        // 判断如果为空，返回null
        if (permissions==null || permissions.isEmpty()) {
            return null;
        }
        // 第四步：将 permission对象中的perms属性添加到集合中，返回
        List<String> permissionList = new ArrayList<>();
        for (SysPermission p : permissions){
            if (!StringUtils.isEmpty(p.getPerms())) {
                permissionList.add(p.getPerms());
            }
        }

        return permissionList;
    }
}
