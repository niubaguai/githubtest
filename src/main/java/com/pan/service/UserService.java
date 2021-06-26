package com.pan.service;

import com.pan.entity.SysUser;
import com.pan.vo.req.*;
import com.pan.vo.resp.LoginRespVo;
import com.pan.vo.resp.PageVo;
import com.pan.vo.resp.UserOwnRoleRespVo;

import java.util.List;

public interface UserService {

    LoginRespVo login(LoginReqVo vo);

    PageVo<SysUser> pageInfo(UserPageReqVO vo);

    SysUser addUser(UserAddVo vo);

    UserOwnRoleRespVo getUserOwnRole(String userId);

    void saveUserOwnRoleId(UserRoleOperationReqVo vo);

    String refreshToken(String refreshToken);

    void updateUserInfo(UserUpdateReqVo vo, String operationId);

    void deleteUserInfo(List<String> userIdList, String operationId);

    List<SysUser> selectUserInfoByDeptIds(List<String> list);

    void logout(String accessToken, String refreshToken);

    SysUser detailInfo(String id);

    void updateUserDetail(UserUpdateDetailInfoReqVo vo, String id);

    void updateUserPwd(UpdatePasswordReqVo vo, String accessToken, String refreshToken);
}
