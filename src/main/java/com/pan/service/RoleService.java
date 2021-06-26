package com.pan.service;

import com.pan.entity.SysRole;
import com.pan.vo.req.RoleAddReqVo;
import com.pan.vo.req.RolePageReqVo;
import com.pan.vo.req.RoleUpdateReqVo;
import com.pan.vo.resp.PageVo;

import java.util.List;

public interface RoleService {
    PageVo<SysRole> getAllRoles(RolePageReqVo vo);

    SysRole addRole(RoleAddReqVo vo);

    List<SysRole> selectAllRoles();

    SysRole detailInfo(String id);

    void updateRoleInfo(RoleUpdateReqVo vo);

    void deleteRoleInfo(String id);

    List<String> getNameByUserId(String userId);

//    void deleteRolesInfo(List<String> roleIdList);
}
