package com.pan.dao;

import com.pan.entity.SysPermission;

import java.util.List;

public interface SysPermissionDao {
    int deleteByPrimaryKey(String id);

    int insert(SysPermission record);

    int insertSelective(SysPermission record);

    SysPermission selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SysPermission record);

    int updateByPrimaryKey(SysPermission record);

    List<SysPermission> getAllMenusPermissions();

    List<SysPermission> selectChild(String id);

    List<SysPermission> getPermsByPermissionIds(List<String> permissionIdByRoleId);
}