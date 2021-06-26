package com.pan.service.impl;

import com.github.pagehelper.PageHelper;
import com.pan.contants.Constant;
import com.pan.dao.SysRoleDao;
import com.pan.dao.SysUserDao;
import com.pan.entity.SysRole;
import com.pan.entity.SysUser;
import com.pan.exception.BusinessException;
import com.pan.service.*;
import com.pan.utils.JwtTokenUtil;
import com.pan.utils.PageUtil;
import com.pan.utils.PasswordUtils;
import com.pan.utils.TokenSetting;
import com.pan.vo.req.*;
import com.pan.vo.resp.LoginRespVo;
import com.pan.vo.resp.PageVo;
import com.pan.vo.resp.UserOwnRoleRespVo;
import io.jsonwebtoken.Jwt;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisServer;
import org.springframework.stereotype.Service;

import com.pan.exception.code.BaseResponseCode;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private TokenSetting tokenSettings;

    @Autowired
    private PermissionService permissionService;

    @Override
    public LoginRespVo login(LoginReqVo vo) {

        SysUser user = sysUserDao.getUserByUsername(vo.getUsername());
        if(user==null){
            // 抛出 运行时异常
            // 账号不存在时
            throw new BusinessException(BaseResponseCode.ACCOUNT_ERROR);
        }
        if (user.getStatus()==2){
            // 账号状态 不是1 的时候
            throw new BusinessException(BaseResponseCode.ACCOUNT_LOCK_TIP);
        }
        if (!PasswordUtils.matches(user.getSalt(), vo.getPassword(), user.getPassword())){
            // 对比密码，  第一个：盐值；第二个：输入的密码；第三个：数据库中的加密后密码
            throw new BusinessException(BaseResponseCode.ACCOUNT_PASSWORD_ERROR);
        }

        LoginRespVo loginRespVO=new LoginRespVo();
//        loginRespVO.setPhone(userInfoByName.getPhone());
//        loginRespVO.setUsername(userInfoByName.getUsername());
        loginRespVO.setUserId(user.getId());

        redisService.set(Constant.USERID_KEY, user.getId());
        // 如果通过 验证，发放 token
        // 添加 负载信息
        Map<String,Object> claims=new HashMap<>();
        claims.put(Constant.JWT_USER_NAME,user.getUsername());
        claims.put(Constant.ROLES_INFOS_KEY,getRoleByUserId(user.getId()));
        claims.put(Constant.PERMISSIONS_INFOS_KEY,getPermissionByUserId(user.getId()));
        // 生成 accessToken
        String accessToken = JwtTokenUtil.getAccessToken(user.getId(), claims);
        // 输出日志信息
        log.info("accessToken = {}!!!",accessToken);

        // refreshToken 负载信息
        String refreshToken;
//        Map<String,Object> refreshTokenClaims=new HashMap<>();
//        refreshTokenClaims.put(Constant.JWT_USER_NAME,user.getUsername());
        // 生成 refreshToken ，
        // 判断 如果登录类型为1 即为web登录； 为2 为app登录
        if(vo.getType().equals("1")){
            refreshToken=JwtTokenUtil.getRefreshToken(user.getId(),claims);
        }else {
            refreshToken=JwtTokenUtil.getRefreshAppToken(user.getId(),claims);
        }
        loginRespVO.setAccessToken(accessToken);
        loginRespVO.setRefreshToken(refreshToken);
//        if(vo.getType().equals("1")){
//            refreshToken=JwtTokenUtil.getRefreshToken(user.getId(),refreshTokenClaims);
//        }else {
//            refreshToken=JwtTokenUtil.getRefreshAppToken(user.getId(),refreshTokenClaims);
//        }
        // 输出日志信息
        log.info("refreshToken={}",refreshToken);

        // 将 accessToken refreshToken 以LoginRespVo返回
//        LoginRespVo loginRespVo = new LoginRespVo();
//        loginRespVo.setAccessToken(accessToken);
//        loginRespVo.setRefreshToken(refreshToken);
//        loginRespVo.setUserId(user.getId());

        return loginRespVO;
    }

    /**
     * 用过用户id查询拥有的角色信息
     * @Author:      小霍
     * @UpdateUser:
     * @Version:     0.0.1
     * @param userId
     * @return       java.util.List<java.lang.String>
     * @throws
     */
    private List<String> getRoleByUserId(String userId){
//        List<String> list=new ArrayList<>();
//        if(userId.equals("9a26f5f1-cbd2-473d-82db-1d6dcf4598f8")){
//            list.add("admin");
//        }else {
//            list.add("dev");
//        }

        // 获取 角色名字

        return roleService.getNameByUserId(userId);
    }

    private List<String> getPermissionByUserId(String userId){
//        List<String> list=new ArrayList<>();
//        if(userId.equals("9a26f5f1-cbd2-473d-82db-1d6dcf4598f8")){
//            list.add("sys:user:add");
//            list.add("sys:user:update");
//            list.add("sys:user:delete");
//            list.add("sys:user:list");
//            list.add("sys:role:list");
//            list.add("sys:role:detail");
//
//        }else {
//            list.add("sys:user:add");
//        }
        List<String> permissionByUserId = permissionService.getPermissionByUserId(userId);
        System.out.println(permissionByUserId);
        return permissionByUserId;
    }

    @Override
    public PageVo<SysUser> pageInfo(UserPageReqVO vo) {
        PageHelper.startPage(vo.getPageNum(),vo.getPageSize());
        List<SysUser> sysUsers = sysUserDao.selectAll(vo);

//        PageInfo<SysUser> pageInfo=new PageInfo<>(sysUsers);
        return PageUtil.getPageVo(sysUsers);
    }

    /**
     * 添加用户操作
     * @param vo
     * @return
     */
    @Override
    public SysUser addUser(UserAddVo vo) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(vo, user);

        // 设置status
        user.setStatus(vo.getStatus());
        // 设置ID
        user.setId(UUID.randomUUID().toString());
        // 设置创建时间
        user.setCreateTime(new Date());
        // 设置盐值
        user.setSalt(PasswordUtils.getSalt());
        // 设置加密过后的密码
        String password = PasswordUtils.encode(vo.getPassword(), user.getSalt());
        user.setPassword(password);

        int count = sysUserDao.insertSelective(user);
        if (count != 1){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }

        return user;
    }

    /**
     * 获取 穿梭框 左侧角色信息 和 右侧角色信息
     * @param userId
     * @return
     */
    @Override
    public UserOwnRoleRespVo getUserOwnRole(String userId) {
        UserOwnRoleRespVo userOwnRoleRespVo = new UserOwnRoleRespVo();
        // 获取 所有角色信息
        List<SysRole> allRoles = roleService.selectAllRoles();
        // 获取 用户拥有的角色信息
        // 这里涉及 user_role 表
        List<String> ownRoleId = userRoleService.getRoleIdByUserId(userId);

        // 添加进vo
        userOwnRoleRespVo.setAllRoles(allRoles);
        userOwnRoleRespVo.setOwnRoles(ownRoleId);

        return userOwnRoleRespVo;
    }

    /**
     * 保存 用户拥有的角色ID到 user_role表
     * @param vo
     */
    @Override
    public void saveUserOwnRoleId(UserRoleOperationReqVo vo) {
        userRoleService.saveUserOwnRoleId(vo);

        /**
         * 标记用户 要主动去刷新
         */
        redisService.set(Constant.JWT_REFRESH_KEY+vo.getUserId(),vo.getUserId(),tokenSettings.getAccessTokenExpireTime().toMillis(), TimeUnit.MILLISECONDS);
        /**
         * 清楚用户授权数据缓存
         */
        redisService.delete(Constant.IDENTIFY_CACHE_KEY+vo.getUserId());
    }

    /**
     * 自动刷新TOken
     * @param refreshToken
     * @return
     */
    @Override
    public String refreshToken(String refreshToken) {
        //它是否过期
        //它是否被加如了黑名
        if(!JwtTokenUtil.validateToken(refreshToken)||redisService.hasKey(Constant.JWT_REFRESH_TOKEN_BLACKLIST+refreshToken)){
            throw new BusinessException(BaseResponseCode.TOKEN_ERROR);
        }
        String userId = JwtTokenUtil.getUserId(refreshToken);
        String userName = JwtTokenUtil.getUserName(refreshToken);
        log.info("userId={}",userId);

        Map<String, Object> claims = new HashMap<>();
        claims.put(Constant.ROLES_INFOS_KEY,getRoleByUserId(userId));
        claims.put(Constant.PERMISSIONS_INFOS_KEY,getPermissionByUserId(userId));
        claims.put(Constant.JWT_USER_NAME,userName);
        String newAccessToken= JwtTokenUtil.getAccessToken(userId,claims);
        return newAccessToken;
    }

    /**
     * 更新用户信息
     */
    @Override
    public void updateUserInfo(UserUpdateReqVo vo, String operationId) {
        // 根据传过来的id查询用户
        SysUser user = sysUserDao.selectByPrimaryKey(vo.getUserId());
        // 如果user为空,抛出异常
        if (null == user){
            log.error("传入的Id：{}不合法", vo.getUserId());
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }
        BeanUtils.copyProperties(vo, user);
        user.setUpdateTime(new Date());
        // 设置密码，如果传过来的密码为空设为null， 如果不是 重新加密
        if (!StringUtils.isEmpty(vo.getPassword())){
            String salt = user.getSalt();
            String password = PasswordUtils.encode(vo.getPassword(), salt);
            user.setPassword(password);
        } else {
            user.setPassword(null);
        }
        // 设置操作人
        user.setUpdateId(operationId);
        // 更新数据库
        int i = sysUserDao.updateByPrimaryKeySelective(user);
        if (i!= 1){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }

        /**
         * 说明用户状态有改变
         * 加入变成禁用，之前
         * 签发的token 都要失效
         */
        if (vo.getStatus()==2){
            redisService.set(Constant.ACCOUNT_LOCK_KEY+vo.getUserId(),vo.getUserId());
        }else {
            redisService.delete(Constant.ACCOUNT_LOCK_KEY+vo.getUserId());
        }
    }

    /**
     * 删除用户信息操作
     * 是逻辑删除（相当于更新操作）把deleted更改为0即可
     */
    @Override
    public void deleteUserInfo(List<String> userIdList, String operationId) {
        SysUser user = new SysUser();
        user.setUpdateId(operationId);
        user.setUpdateTime(new Date());
        user.setDeleted(0);
        // 执行sql语句
        int i = sysUserDao.deleteUserInfo(user, userIdList);
        if (i==0){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }

        /**
         * 标记用户id 已删除
         * 因为我们是以签发token 的形式保持用户登录状态的
         * 有可能签发了多次toekn 所以在用户删除的时候
         * 要把userId标记起来 过期时间为refreahToekn 的过期时间
         * 避免它可以通过刷新toekn来继续保持登录
         */
        for (String userId:userIdList){
            redisService.set(Constant.DELETED_USER_KEY+userId, userId, tokenSettings.getRefreshTokenExpireAppTime().toMillis(), TimeUnit.MILLISECONDS);
            /**
             * 清楚用户授权数据缓存
             */
            redisService.delete(Constant.IDENTIFY_CACHE_KEY+userId);
        }
    }

    /**
     * 通过班级id查询组织下所有用户
     */
    @Override
    public List<SysUser> selectUserInfoByDeptIds(List<String> classIds) {
        return sysUserDao.selectUserInfoByDeptIds(classIds);
    }

    /**
     * 退出登录操作
     */
    @Override
    public void logout(String accessToken, String refreshToken) {
        if(StringUtils.isEmpty(accessToken)||StringUtils.isEmpty(refreshToken)){
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }
        Subject subject = SecurityUtils.getSubject();
        if(subject!=null){
            subject.logout();
        }
        String userId=JwtTokenUtil.getUserId(accessToken);
        /**
         * 把accessToken 加入黑名单
         */
        redisService.set(Constant.JWT_ACCESS_TOKEN_BLACKLIST+accessToken,userId,JwtTokenUtil.getRemainingTime(accessToken),TimeUnit.MILLISECONDS);

        /**
         * 把refreshToken 加入黑名单
         */
        redisService.set(Constant.JWT_REFRESH_IDENTIFICATION+refreshToken,userId,JwtTokenUtil.getRemainingTime(refreshToken),TimeUnit.MILLISECONDS);

    }

    /**
     * 获取用户详情信息接口
     */
    @Override
    public SysUser detailInfo(String id) {
        SysUser user = sysUserDao.selectByPrimaryKey(id);
        return user;
    }

    /**
     * 修改用户详情信息接口
     */
    @Override
    public void updateUserDetail(UserUpdateDetailInfoReqVo vo, String id) {
        SysUser user = new SysUser();
        // 拷贝vo里的信息
        BeanUtils.copyProperties(vo,user);
        // 添加user其他信息
        user.setId(id);
        user.setUpdateTime(new Date());
        user.setUpdateId(id);

        int i = sysUserDao.updateByPrimaryKeySelective(user);
        if (i!=1){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }
    }

    /**
     * 修改用户密码
     */
    @Override
    public void updateUserPwd(UpdatePasswordReqVo vo, String accessToken, String refreshToken) {
        // 通过token获取userId
        String userId = JwtTokenUtil.getUserId(accessToken);
        // 通过userId获取sysUser对象
        SysUser user = sysUserDao.selectByPrimaryKey(userId);
        // 如果用户为空，说明传过来的token有误，
        if (user == null) {
            throw new BusinessException(BaseResponseCode.TOKEN_ERROR);
        }
        // 验证旧密码是否正确，
        if (!PasswordUtils.matches(user.getSalt(), vo.getOldPwd(), user.getPassword())){
            throw new BusinessException(BaseResponseCode.OLD_PASSWORD_ERROR);
        }

        // 更新密码
        user.setUpdateTime(new Date());
        user.setUpdateId(userId);
        user.setPassword(PasswordUtils.encode(vo.getNewPwd(), user.getSalt()));

        int i = sysUserDao.updateByPrimaryKeySelective(user);
        if (i!=1){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }

        // 将改之前的token加入黑名单，不能再使用这个token登录

        /**
         * 把token 加入黑名单 禁止再访问我们的系统资源
         */
        redisService.set(Constant.JWT_ACCESS_TOKEN_BLACKLIST+accessToken,userId,JwtTokenUtil.getRemainingTime(accessToken), TimeUnit.MILLISECONDS);
        /**
         * 把 refreshToken 加入黑名单 禁止再拿来刷新token
         */
        redisService.set(Constant.JWT_REFRESH_TOKEN_BLACKLIST+refreshToken,userId,JwtTokenUtil.getRemainingTime(refreshToken),TimeUnit.MILLISECONDS);
        /**
         * 清楚用户授权数据缓存
         */
        redisService.delete(Constant.IDENTIFY_CACHE_KEY+userId);


    }
}
