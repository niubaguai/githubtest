package com.pan.service.impl;

import com.github.pagehelper.PageHelper;
import com.pan.dao.SysLogDao;
import com.pan.entity.SysLog;
import com.pan.service.LogService;
import com.pan.utils.PageUtil;
import com.pan.vo.req.SysLogPageReqVo;
import com.pan.vo.resp.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private SysLogDao sysLogDao;

    /**
     * 分页接口操作
     */
    @Override
    public PageVo<SysLog> pageInfo(SysLogPageReqVo vo) {
        PageHelper.startPage(vo.getPageNum(),vo.getPageSize());
        List<SysLog> logList = sysLogDao.selectAll(vo);

        return PageUtil.getPageVo(logList);
    }
}
