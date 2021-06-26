package com.pan.service;

import com.pan.entity.SysLog;
import com.pan.vo.req.SysLogPageReqVo;
import com.pan.vo.resp.PageVo;

public interface LogService {
    PageVo<SysLog> pageInfo(SysLogPageReqVo vo);

}
