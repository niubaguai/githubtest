package com.pan.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SysClass implements Serializable {
    private String id;

    private String classNo;

    private String name;

    private String pid;

    private Integer status;

    private String relationCode;

    private String classTeacherId;

    private String teacherName;

    private String phone;

    private Date createTime;

    private Date updateTime;

    private Integer deleted;

    private String pidName;

    private static final long serialVersionUID = 1L;


}