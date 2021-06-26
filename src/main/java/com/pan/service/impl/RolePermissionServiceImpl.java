package com.pan.service.impl;

import com.pan.dao.SysRolePermissionDao;
import com.pan.entity.SysPermission;
import com.pan.entity.SysRolePermission;
import com.pan.exception.BusinessException;
import com.pan.exception.code.BaseResponseCode;
import com.pan.service.RolePermissionService;
import com.pan.vo.req.RolePermissionOperationReqVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    @Autowired
    private SysRolePermissionDao sysRolePermissionDao;

    @Override
    public void addRolePermission(RolePermissionOperationReqVO reqVO) {
        sysRolePermissionDao.removeByRoleId(reqVO.getRoleId());
        if(null==reqVO.getPermissionIds() && reqVO.getPermissionIds().isEmpty()){
            return ;
        }
        Date createTime = new Date();
        List<SysRolePermission> list = new ArrayList<>();
        // 根据每一个权限id 添加 相应数据进数据库
        for(String permissionId : reqVO.getPermissionIds()){

            SysRolePermission sysRolePermission = new SysRolePermission();
            sysRolePermission.setId(UUID.randomUUID().toString());
            sysRolePermission.setCreateTime(createTime);
            sysRolePermission.setRoleId(reqVO.getRoleId());
            sysRolePermission.setPermissionId(permissionId);

            list.add(sysRolePermission);
        }

        int count = sysRolePermissionDao.addRolePermission(list);
        if (count == 0){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }
    }

    @Override
    public int deleteByPermissionId(String permissionId) {
       return sysRolePermissionDao.deleteByPermissionId(permissionId);
    }

    @Override
    public List<String> getRoleIdByPermissionId(String id) {
        return sysRolePermissionDao.getRoleIdByPermissionId(id);
    }

    /**
     * 角色回显操作
     * @param roleId
     * @return
     */
    @Override
    public List<String> getPermissionIdByRoleId(String roleId) {
        return sysRolePermissionDao.getPermissionIdByRoleId(roleId);
    }

    /**
     * 通过角色id 删除 role_permission表中的信息
     */
    @Override
    public int deleteInfoByRoleId(String roleId) {
        return sysRolePermissionDao.deleteInfoByRoleId(roleId);
    }

    /**
     *  通过角色Ids 获取权限id
     * */
    @Override
    public List<String> getPermissionIdByRoleIds(List<String> roleIdByUserId) {
        return sysRolePermissionDao.getPermissionIdByRoleIds(roleIdByUserId);
    }
}
