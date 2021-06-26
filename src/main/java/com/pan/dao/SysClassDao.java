package com.pan.dao;

import com.pan.entity.SysClass;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface SysClassDao {
    int deleteByPrimaryKey(String id);

    int insert(SysClass record);

    int insertSelective(SysClass record);

    SysClass selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SysClass record);

    int updateByPrimaryKey(SysClass record);

    List<SysClass> getAllClassInfo();

    int updateRelationCode(@Param("oldStr") String oldStr, @Param("newStr") String newStr, @Param("relationCode") String relationCode);

    List<String> selectChildIds(String relationCode);

    int deletedClass(@Param("updateTime") Date updateTime, @Param("list") List<String> list);

}