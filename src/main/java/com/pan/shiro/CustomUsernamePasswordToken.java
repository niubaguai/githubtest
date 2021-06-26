package com.pan.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

public class CustomUsernamePasswordToken extends UsernamePasswordToken {
    private String JwtToken;

    public CustomUsernamePasswordToken(String jwtToken) {
        this.JwtToken = jwtToken;
    }

    @Override
    public Object getPrincipal() {
        return JwtToken;
    }

    @Override
    public Object getCredentials() {
        return JwtToken;
    }
}
