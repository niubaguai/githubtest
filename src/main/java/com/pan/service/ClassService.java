package com.pan.service;

import com.pan.entity.SysClass;
import com.pan.vo.req.ClassAddVo;
import com.pan.vo.req.ClassUpdateVo;
import com.pan.vo.resp.ClassRespNodeVo;

import java.util.List;

public interface ClassService {
    List<SysClass> getALlClassInfo();

    List<ClassRespNodeVo> getClassTree(String classId);

    SysClass addClassInfo(ClassAddVo vo);

    void updateClassInfo(ClassUpdateVo vo);

    void deleteClassInfo(String classId);
}
