package com.pan.service;

import com.pan.vo.resp.HomeRespVO;
import com.pan.vo.resp.LayuiMiniPermissionVo;

public interface HomeService {
    HomeRespVO getHomeInfo(String userId);

    LayuiMiniPermissionVo getHomePermissionInfo(String userId);
}
