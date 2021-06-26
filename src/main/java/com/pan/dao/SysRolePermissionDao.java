package com.pan.dao;

import com.pan.entity.SysRolePermission;

import java.util.List;

public interface SysRolePermissionDao {
    int deleteByPrimaryKey(String id);

    int insert(SysRolePermission record);

    int insertSelective(SysRolePermission record);

    SysRolePermission selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SysRolePermission record);

    int updateByPrimaryKey(SysRolePermission record);

    void removeByRoleId(String roleId);

    int addRolePermission(List<SysRolePermission> list);

    List<String> getRoleIdByPermissionId(String id);

    int deleteByPermissionId(String permissionId);

    List<String> getPermissionIdByRoleId(String roleId);

    int deleteInfoByRoleId(String roleId);

    List<String> getPermissionIdByRoleIds(List<String> roleIdByUserId);
}