package com.pan.service;

import com.pan.vo.req.RolePermissionOperationReqVO;

import java.util.List;

public interface RolePermissionService {
    void addRolePermission(RolePermissionOperationReqVO reqVO);

    int deleteByPermissionId(String permissionId);

    List<String> getRoleIdByPermissionId(String id);

    List<String> getPermissionIdByRoleId(String roleId);

    int deleteInfoByRoleId(String roleId);

    List<String> getPermissionIdByRoleIds(List<String> roleIdByUserId);
}
