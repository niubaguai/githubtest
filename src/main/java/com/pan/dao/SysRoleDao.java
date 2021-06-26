package com.pan.dao;

import com.pan.entity.SysRole;
import com.pan.vo.req.RolePageReqVo;

import java.util.List;

public interface SysRoleDao {
    int deleteByPrimaryKey(String id);

    int insert(SysRole record);

    int insertSelective(SysRole record);

    SysRole selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SysRole record);

    int updateByPrimaryKey(SysRole record);

    List<SysRole> getAllRoles(RolePageReqVo vo);

    List<String> getNameByRoleIds(List<String> roleIds);
}