package com.pan.dao;

import com.pan.entity.SysLog;
import com.pan.vo.req.SysLogPageReqVo;

import java.util.List;

public interface SysLogDao {
    int deleteByPrimaryKey(String id);

    int insert(SysLog record);

    int insertSelective(SysLog record);

    SysLog selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SysLog record);

    int updateByPrimaryKey(SysLog record);

    List<SysLog> selectAll(SysLogPageReqVo vo);
}