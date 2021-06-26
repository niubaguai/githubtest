package com.pan.service;

import com.pan.entity.SysPermission;
import com.pan.vo.req.PermissionAddReqVO;
import com.pan.vo.req.PermissionUpdateReqVo;
import com.pan.vo.resp.LayuiMiniPermissionVo;
import com.pan.vo.resp.PermissionRespNodeVO;

import java.util.List;

public interface PermissionService {
    List<SysPermission> getAllMenusPermissions();

    List<PermissionRespNodeVO> selectAllMenuByTree();

    SysPermission addPermission(PermissionAddReqVO vo);

    List<PermissionRespNodeVO> permissionTreeList(String userId);

    List<LayuiMiniPermissionVo> getLayuiMiniTree(String userId);

    List<PermissionRespNodeVO> selectAllByTree();

    void updatePemissionInfo(PermissionUpdateReqVo vo);

    void deletePermissionInfo(String permissionId);

    List<String> getPermissionByUserId(String userId);

    LayuiMiniPermissionVo getLayuiTree(String userId);

    List<SysPermission> getPermissions(String userId);
}
