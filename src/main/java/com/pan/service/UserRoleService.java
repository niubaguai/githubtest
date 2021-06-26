package com.pan.service;

import com.pan.entity.SysUser;
import com.pan.vo.req.UserRoleOperationReqVo;

import java.util.List;

public interface UserRoleService {
    List<String> getRoleIdByUserId(String userId);

    void saveUserOwnRoleId(UserRoleOperationReqVo vo);

    List<String> getUserIdByRoleId(List<String> roleList);

    List<String> getUserIdByOneRoleId(String roleId);

    int deleteInfoByRoleId(String roleId);
}
