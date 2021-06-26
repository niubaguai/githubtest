package com.pan.shiro;

import com.alibaba.fastjson.JSON;
import com.pan.contants.Constant;
import com.pan.exception.BusinessException;
import com.pan.exception.code.BaseResponseCode;
import com.pan.utils.DataResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
public class CustomAccessControlFilter extends AccessControlFilter {

    /**
     * 如果返回的是true 就流转到下一个链式调用
     * 返回false 就会流转到 onAccessDenied方法
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        return false;
    }

    /**
     * 如果返true 就会流转到下一个链式调用
     * false 就是不会流转到下一个链式调用
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        log.info("request 接口地址：{}",request.getRequestURI());
        log.info("request 接口的请求方式{}",request.getMethod());
        // 获取前端发送过来的token
        String accessToken = request.getHeader(Constant.ACCESS_TOKEN); //ACCESS_TOKEN这个值是前后端约定好，否则找不到

        try {
            if(StringUtils.isEmpty(accessToken)){
                // 抛出token不能为空的异常
                throw new BusinessException(BaseResponseCode.TOKEN_NOT_NULL);
            }
            // 如果token不为空， 把前端携带过来的业务访问token(accessToken) 整合成一个 UsernamePasswordToken
            CustomUsernamePasswordToken customUsernamePasswordToken = new CustomUsernamePasswordToken(accessToken);
            // .主体提交认证
            getSubject(servletRequest, servletResponse).login(customUsernamePasswordToken);
            // 解决doGetAuthorizationInfo没有调用的方案
            getSubject(servletRequest, servletResponse).hasRole("尼玛");
        } catch (BusinessException e) {
            customResponse(servletResponse, e.getCode(), e.getMsg());
            return false;
        } catch (AuthenticationException e) {
            if(e.getCause() instanceof BusinessException){
                BusinessException exception= (BusinessException) e.getCause();
                customResponse(servletResponse, exception.getCode(), exception.getMsg());
            } else {
                customResponse(servletResponse,BaseResponseCode.TOKEN_ERROR.getCode(),BaseResponseCode.TOKEN_ERROR.getMsg());
            }
            return false;
        } catch (Exception e){
            customResponse(servletResponse,BaseResponseCode.SYSTEM_ERROR.getCode(),BaseResponseCode.SYSTEM_ERROR.getMsg());
            return false;
        }
        return true;
    }



    /**
     * 自定义响应前端
     */
    private void customResponse(ServletResponse response,int code ,String msg){
        try {
            DataResult result=DataResult.getResult(code,msg);
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.setCharacterEncoding("UTF-8");
            String str= JSON.toJSONString(result);
            OutputStream outputStream=response.getOutputStream();
            outputStream.write(str.getBytes("UTF-8"));
            outputStream.flush();
        } catch (IOException e) {
            log.error("customResponse...error:{}",e);
        }

    }
}
