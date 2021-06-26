package com.pan.service.impl;

import com.github.pagehelper.PageHelper;
import com.pan.contants.Constant;
import com.pan.dao.SysRoleDao;
import com.pan.entity.SysRole;
import com.pan.entity.SysUser;
import com.pan.exception.BusinessException;
import com.pan.exception.code.BaseResponseCode;
import com.pan.service.*;
import com.pan.utils.PageUtil;
import com.pan.utils.TokenSetting;
import com.pan.vo.req.RoleAddReqVo;
import com.pan.vo.req.RolePageReqVo;
import com.pan.vo.req.RolePermissionOperationReqVO;
import com.pan.vo.req.RoleUpdateReqVo;
import com.pan.vo.resp.PageVo;
import com.pan.vo.resp.PermissionRespNodeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private SysRoleDao sysRoleDao;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private TokenSetting tokenSettings;



    @Override
    public PageVo<SysRole> getAllRoles(RolePageReqVo vo) {

        PageHelper.startPage(vo.getPageNum(), vo.getPageSize());
        List<SysRole> roleList = sysRoleDao.getAllRoles(vo);

        return PageUtil.getPageVo(roleList);
    }

    @Override
    public SysRole addRole(RoleAddReqVo vo) {

        SysRole sysRole = new SysRole();
        // 将vo中与sysrole相同的属性 保存
        BeanUtils.copyProperties(vo, sysRole);
        // 设置id
        sysRole.setId(UUID.randomUUID().toString());
        // 设置创建时间
        sysRole.setCreateTime(new Date());
        // 插入数据库
        int count = sysRoleDao.insertSelective(sysRole);
        if(count != 1){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }
        if(null!=vo.getPermissions() && !vo.getPermissions().isEmpty()){
            RolePermissionOperationReqVO reqVO = new RolePermissionOperationReqVO();
            reqVO.setRoleId(sysRole.getId());
            reqVO.setPermissionIds(vo.getPermissions());
            rolePermissionService.addRolePermission(reqVO);
        }

        return sysRole;
    }

    /**
     * 获取所有角色信息
     * @return
     */
    @Override
    public List<SysRole> selectAllRoles() {
        return sysRoleDao.getAllRoles(new RolePageReqVo());
    }

    /**
     * 查询角色详情信息
     */
    @Override
    public SysRole detailInfo(String id) {
        SysRole sysRole = sysRoleDao.selectByPrimaryKey(id);
        // 如果查出来的数据为空，说明传过来的id错误
        if (null==sysRole){
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }

        // 获取所有权限信息，通过权限树显示
        List<PermissionRespNodeVO> allPermissions = permissionService.selectAllByTree();
        // 通过角色id获取 角色拥有的权限id
        // 并将list 转为set
        List<String> roleOwnPermissionId = rolePermissionService.getPermissionIdByRoleId(sysRole.getId());
        Set<String> checkList = new HashSet<>(roleOwnPermissionId);
        // 通过 自定义方法，遍历权限树，
        // 在allPermissions中，只需在没有子集的权限中 将其checked设置为true即可
        setChecked(checkList, allPermissions);

        sysRole.setPermissionRespNodes(allPermissions);

        return sysRole;
    }

    private void setChecked(Set<String> checkList, List<PermissionRespNodeVO> allPermissions) {
        for(PermissionRespNodeVO node : allPermissions){
            // 如果通过该角色拥有的权限集合中有 这个没有子集的权限，将其 checked 设为true
            if (checkList.contains(node.getId())&&(node.getChildren()==null||node.getChildren().isEmpty())) {
                node.setChecked(true);
            }
            // 如果不是  递归调用
            setChecked(checkList, (List<PermissionRespNodeVO>) node.getChildren());
        }
    }

    /**
     * 更新角色信息
     */
    @Override
    public void updateRoleInfo(RoleUpdateReqVo vo) {
        SysRole sysRole = sysRoleDao.selectByPrimaryKey(vo.getId());
        // 如果为空，说明传过来的id有误
        if (null == sysRole) {
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }
        BeanUtils.copyProperties(vo, sysRole);

        sysRole.setUpdateTime(new Date());
        // 更新数据库
        int i = sysRoleDao.updateByPrimaryKeySelective(sysRole);
        if (i != 1) {
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }

        // 修改 角色拥有的权限信息
        // 这里涉及 role_permission 表
        RolePermissionOperationReqVO reqVO = new RolePermissionOperationReqVO();
        reqVO.setRoleId(sysRole.getId());
        reqVO.setPermissionIds(vo.getPermissions());
        rolePermissionService.addRolePermission(reqVO);

        // 给修改过权限信息的用户 打标机，目的是为了 token刷新
        // 通过roleId 获取 用户id
        List<String> userIds = userRoleService.getUserIdByOneRoleId(vo.getId());
        if (!userIds.isEmpty()) {
            for (String userId : userIds){
                // 为每一个用户id打标记存入redis
                redisService.set(Constant.JWT_REFRESH_KEY+userId, userId, tokenSettings.getAccessTokenExpireTime().toMillis(), TimeUnit.MILLISECONDS);

                /**
                 * 清楚用户授权数据缓存
                 */
                redisService.delete(Constant.IDENTIFY_CACHE_KEY+userId);
            }
        }


    }

    /**
     * 删除角色信息  逻辑删除
     * 同时 要根据roleId 删除 user_role表和 role_permission表中的信息  真删除
     */
    @Override
    public void deleteRoleInfo(String id) {
        SysRole sysRole = sysRoleDao.selectByPrimaryKey(id);
        // 如果为空 说明传过来的数据异常
        if (null == sysRole) {
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }
        sysRole.setUpdateTime(new Date());
        sysRole.setDeleted(0);

        int i = sysRoleDao.updateByPrimaryKeySelective(sysRole);
        if (i != 1) {
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }

        // 通过roleId 获取userIds ，用于刷新token  用于后面打标记存入redis
        // 要在删除前操作，否则查不到数据
        List<String> userIds = userRoleService.getUserIdByOneRoleId(id);

        // 要根据roleId 删除 user_role表和 role_permission表中的信息
        userRoleService.deleteInfoByRoleId(id);
        rolePermissionService.deleteInfoByRoleId(id);

        // 对删除了角色信息的userId 打标记，用于刷新token
        if (null!=userIds){
            for (String userId : userIds) {
                redisService.set(Constant.JWT_REFRESH_KEY+userId, userId, tokenSettings.getAccessTokenExpireTime().toMillis(), TimeUnit.MILLISECONDS);

                /**
                 * 清楚用户授权数据缓存
                 */
                redisService.delete(Constant.IDENTIFY_CACHE_KEY+userId);
            }
        }
    }

    /**
     * 批量删除角色
     * 同时 要根据roleId 删除 user_role表和 role_permission表中的信息  真删除
     */
//    @Override
//    public void deleteRolesInfo(List<String> roleIdList) {
//        SysRole sysRole = new SysRole();
//        sysRole.setUpdateTime(new Date());
//        sysRole.setDeleted(0);
//
//        int i = sysRoleDao.deleteRoleByIds(roleIdList);
//        if (i != 1) {
//            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
//        }
//
//        // 通过roleId 获取userIds ，用于刷新token  用于后面打标记存入redis
//        // 要在删除前操作，否则查不到数据
//        List<String> userIds = userRoleService.getUserIdByOneRoleId(id);
//
//        // 要根据roleId 删除 user_role表和 role_permission表中的信息
//        userRoleService.deleteInfoByRoleId(id);
//        rolePermissionService.deleteInfoByRoleId(id);
//
//        // 对删除了角色信息的userId 打标记，用于刷新token
//        if (null!=userIds){
//            for (String userId : userIds) {
//                redisService.set(Constant.JWT_REFRESH_KEY+userId, userId, tokenSettings.getAccessTokenExpireTime().toMillis(), TimeUnit.MILLISECONDS);
//            }
//        }
//    }

    /**
    通过用户Id获取角色名称  如 admin
    **/

    @Override
    public List<String> getNameByUserId(String userId) {
        // 先通过用户Id获取角色Id
        List<String> roleIds = userRoleService.getRoleIdByUserId(userId);
        // 如果为空，返回空
        if (roleIds.isEmpty()) {
            return null;
        }
        // 通过角色Id获取角色名称
        List<String> roleNames = sysRoleDao.getNameByRoleIds(roleIds);
        return roleNames;
    }
}
