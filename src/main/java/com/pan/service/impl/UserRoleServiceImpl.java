package com.pan.service.impl;

import com.pan.dao.SysUserRoleDao;
import com.pan.entity.SysUser;
import com.pan.entity.SysUserRole;
import com.pan.exception.BusinessException;
import com.pan.exception.code.BaseResponseCode;
import com.pan.service.UserRoleService;
import com.pan.vo.req.UserRoleOperationReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private SysUserRoleDao sysUserRoleDao;

    @Override
    public List<String> getRoleIdByUserId(String userId) {

        List<String> list = sysUserRoleDao.getRoleIdByUserId(userId);

        return list;
    }

    @Override
    public void saveUserOwnRoleId(UserRoleOperationReqVo vo) {
        // 先删除 表 user_role userId用户相关的的信息
        sysUserRoleDao.removeInfoByUserId(vo.getUserId());

        // 如果角色id集合为空 返回，因为没有什么要插入的
        if (vo.getRoleIds() == null || vo.getRoleIds().isEmpty()){
            return ;
        }
        Date date = new Date();
        List<SysUserRole> list = new ArrayList<>();

        for (String roleId : vo.getRoleIds()){
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setCreateTime(date);
            sysUserRole.setId(UUID.randomUUID().toString());
            sysUserRole.setUserId(vo.getUserId());
            sysUserRole.setRoleId(roleId);

            list.add(sysUserRole);
        }

        int count = sysUserRoleDao.batchUserRole(list);
        if (count == 0){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }

    }

    /**
     * 通过roleId获取userId
     */
    @Override
    public List<String> getUserIdByRoleId(List<String> roleList) {
        return sysUserRoleDao.getUserIdByRoleId(roleList);
    }

    /**
     * 通过单个roleId 获取userId
     */
    @Override
    public List<String> getUserIdByOneRoleId(String roleId) {
        return sysUserRoleDao.getUserIdByOneRoleId(roleId);
    }

    /**
     * 通过 roleId 删除 user_role 表中的信息
     */
    @Override
    public int deleteInfoByRoleId(String roleId) {
        return sysUserRoleDao.deleteInfoByRoleId(roleId);
    }
}
