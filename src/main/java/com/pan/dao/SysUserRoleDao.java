package com.pan.dao;

import com.pan.entity.SysUser;
import com.pan.entity.SysUserRole;

import java.util.List;

public interface SysUserRoleDao {
    int deleteByPrimaryKey(String id);

    int insert(SysUserRole record);

    int insertSelective(SysUserRole record);

    SysUserRole selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SysUserRole record);

    int updateByPrimaryKey(SysUserRole record);

    List<String> getRoleIdByUserId(String userId);

    void removeInfoByUserId(String userId);

    int batchUserRole(List<SysUserRole> list);

    List<String> getUserIdByRoleId(List<String> roleIdList);

    List<String> getUserIdByOneRoleId(String roleId);

    int deleteInfoByRoleId(String roleId);
}