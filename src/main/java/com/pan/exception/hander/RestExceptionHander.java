package com.pan.exception.hander;


import com.pan.exception.BusinessException;
import com.pan.exception.code.BaseResponseCode;
import com.pan.utils.DataResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;


/**
 * 全局异常统一处理
 */
@RestControllerAdvice
@Slf4j
public class RestExceptionHander {

    @ExceptionHandler(Exception.class)
    public DataResult handleException(Exception e){
        log.error("handleException.....{}",e);
        return DataResult.getResult(BaseResponseCode.SYSTEM_ERROR);
    }

    /**
     * 运行时异常
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public DataResult handleBusinessException(BusinessException e){
        log.error("BusinessException ...{}",e);
        return DataResult.getResult(e.getCode(), e.getMsg());
    }

    /**
     * Hibernate Validator 提示判断
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public DataResult handlerMethodArgumentNotValidException(MethodArgumentNotValidException e){
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        log.error("handlerMethodArgumentNotValidException  AllErrors:{} MethodArgumentNotValidException:{}",e.getBindingResult().getAllErrors(),e);
        String msg=null;
        for(ObjectError error:allErrors){
            msg=error.getDefaultMessage();
            break;
        }
        return DataResult.getResult(BaseResponseCode.VALIDATOR_ERROR.getCode(),msg);
    }

    /**
     * 无权限异常提示
     * @param e
     * @return
     */
    @ExceptionHandler(UnauthorizedException.class)
    public DataResult unauthorizedException(UnauthorizedException e){
        log.error("UnauthorizedException:{}",e);
        return DataResult.getResult(BaseResponseCode.NOT_PERMISSION);
    }
}
