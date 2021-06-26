package com.pan.service.impl;

import com.alibaba.fastjson.JSON;
import com.pan.dao.SysUserDao;
import com.pan.entity.SysUser;
import com.pan.service.HomeService;
import com.pan.service.PermissionService;
import com.pan.service.UserService;
import com.pan.vo.resp.HomeRespVO;
import com.pan.vo.resp.LayuiMiniPermissionVo;
import com.pan.vo.resp.PermissionRespNodeVO;
import com.pan.vo.resp.UserInfoRespVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private PermissionService permissionService;

    @Override
    public HomeRespVO getHomeInfo(String userId) {
        HomeRespVO homeRespVO = new HomeRespVO();
//        List<LayuiMiniPermissionVo> list= JSON.parseArray(home, LayuiMiniPermissionVo.class);
        List<LayuiMiniPermissionVo> layuiList = permissionService.getLayuiMiniTree(userId);
        List<PermissionRespNodeVO> list = permissionService.permissionTreeList(userId);
//        System.out.println(list);
        homeRespVO.setLayuiMiniMenus(layuiList);
        homeRespVO.setMenus(list);

        // 获取用户对象
        SysUser user = sysUserDao.selectByPrimaryKey(userId);

        UserInfoRespVO userInfoRespVO = new UserInfoRespVO();
        if(user != null){
            userInfoRespVO.setUsername(user.getRealName());
            userInfoRespVO.setId(userId);
        }

        homeRespVO.setUserInfoVO(userInfoRespVO);


        return homeRespVO;
    }

    /**
     * 用于更新菜单权限栏
     * */
    @Override
    public LayuiMiniPermissionVo getHomePermissionInfo(String userId) {
        LayuiMiniPermissionVo layuitree = permissionService.getLayuiTree(userId);

        return layuitree;
    }
}
