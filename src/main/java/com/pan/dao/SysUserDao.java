package com.pan.dao;

import com.pan.entity.SysUser;
import com.pan.vo.req.UserPageReqVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserDao {
    int deleteByPrimaryKey(String id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    SysUser getUserByUsername(String username);

    List<SysUser> selectAll(UserPageReqVO vo);

    SysUser getUserById(String userId);

    int deleteUserInfo(@Param("sysUser") SysUser sysUser, @Param("list") List<String> list);

    List<SysUser> selectUserInfoByDeptIds(List<String> classIds);
}