package com.pan.shiro;

import com.pan.contants.Constant;
import com.pan.utils.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.Collection;

public class CustomRealm extends AuthorizingRealm {
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof CustomUsernamePasswordToken;
    }
    /**
     * 授权
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String accessToken = (String) principalCollection.getPrimaryPrincipal();
        Claims claims = JwtTokenUtil.getClaimsFromToken(accessToken);
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // 如果 负载信息 中的权限信息不为空，添加权限信息
        if(claims.get(Constant.PERMISSIONS_INFOS_KEY)!=null){
            info.addStringPermissions((Collection<String>) claims.get(Constant.PERMISSIONS_INFOS_KEY));
        }
        // 如果 负载信息 中的角色信息不为空，添加角色信息
        if(claims.get(Constant.ROLES_INFOS_KEY)!=null){
            info.addRoles((Collection<String>) claims.get(Constant.ROLES_INFOS_KEY));
        }
        return info;
    }

    /**
     * 认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        CustomUsernamePasswordToken customUsernamePasswordToken = (CustomUsernamePasswordToken) authenticationToken;
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(
                customUsernamePasswordToken.getPrincipal(),
                customUsernamePasswordToken.getCredentials(),
                this.getName()
        );
        return info;
    }
}
