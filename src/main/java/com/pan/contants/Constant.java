package com.pan.contants;

/**
 * @ClassName: Constant
 * TODO:类文件简单描述
 * @Author: 小霍
 * @UpdateUser: 小霍
 * @Version: 0.0.1
 */
public class Constant {
    /**
     * Contants 加入 用户名 key 常量
     * 用户名称 key
     */
    public static final String JWT_USER_NAME="jwt-user-name-key";

    /**
     * 角色信息key
     */
    public static final String ROLES_INFOS_KEY="roles-infos-key";

    /**
     * 权限信息key
     */
    public static final String PERMISSIONS_INFOS_KEY="permissions-infos-key";

    /**
     *  业务访问token
     */
    public static final String ACCESS_TOKEN="authorization";


    /**
     * 主动去刷新 token key(适用场景 比如修改了用户的角色/权限去刷新token)
     */
    public static final String JWT_REFRESH_KEY="jwt-refresh-key_";

    /**
     * 标记用户是否已经被锁定
     */
    public static final String ACCOUNT_LOCK_KEY="account-lock-key_";
    /**
     * 标记用户是否已经删除
     */
    public static final String DELETED_USER_KEY="deleted-user-key_";

    /**
     * 用户权鉴缓存 key
     */
    public static final String IDENTIFY_CACHE_KEY="shiro-cache:com.pan.shiro.CustomRealm.authorizationCache:";

    /**
     * 部门编码key
     */
    public static final String DEPT_CODE_KEY="dept-code-key_";
    /**
     * 班级编码key
     */
    public static final String CLASS_CODE_KEY="class-code-key_";


    /**
     * 刷新token
     */
    public static final String REFRESH_TOKEN="refreshToken";

    /**
     * redis存userid 用于根据不同用户获取不同菜单页面
     */
    public static final String USERID_KEY="userid-key_";

    /**
     * refresh_token 主动退出后加入黑名单 key
     */
    public static final String JWT_REFRESH_TOKEN_BLACKLIST="jwt-refresh-token-blacklist_";

    /**
     * access_token 主动退出后加入黑名单 key
     */
    public static final String JWT_ACCESS_TOKEN_BLACKLIST="jwt-access-token-blacklist_";

    /**
     * 标记新的access_token
     */
    public static final String JWT_REFRESH_IDENTIFICATION="jwt-refresh-identification_";

}
