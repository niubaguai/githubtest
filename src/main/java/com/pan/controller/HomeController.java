package com.pan.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.pan.aop.annotation.MyLog;
import com.pan.contants.Constant;
import com.pan.service.HomeService;
import com.pan.service.RedisService;
import com.pan.utils.DataResult;
import com.pan.utils.JwtTokenUtil;
import com.pan.vo.resp.HomeRespVO;
import com.pan.vo.resp.LayuiMiniPermissionVo;
import com.pan.vo.resp.LoginRespVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisServer;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

//@CrossOrigin(origins = "*",maxAge = 3600)  //解决跨域问题
@RestController
@RequestMapping("/api")
@Api(tags = "首页模块",description = "首页相关模块")
public class HomeController {

    @Autowired
    private HomeService homeService;

    @Autowired
    private RedisService redisService;

    @GetMapping("/home")
    @ApiOperation(value = "获取首页菜单和登录信息接口")
    @MyLog(title = "首页模块",action = "获取首页菜单和登录信息接口")
    public DataResult<HomeRespVO> getHomeInfo(HttpServletRequest request, HttpServletResponse response){
//        response.setHeader("Access-Control-Allow-Origin","*");
        // 获取 由前端发送过来的token
        String accessToken = request.getHeader(Constant.ACCESS_TOKEN);
        // 通过 token 获取 userId
        String userId = JwtTokenUtil.getUserId(accessToken);
        // new 返回格式 DataResult
        DataResult result = DataResult.success();
        // 通过业务层 获取 所需的数据
        HomeRespVO homeRespVO = homeService.getHomeInfo(userId);
        result.setData(homeRespVO);

        List<LayuiMiniPermissionVo> list = homeRespVO.getLayuiMiniMenus();
        System.out.println(list);
        // 输出json数据
        File file = new File("log.txt");
//        if (!file.exists()) {
//            createJsonFile(list.get(0), "D:/layuimini/mylayuimini/resource/api/myinit.json");
//        }


        return result;
    }

//    @GetMapping("/home/permission")
//    @ApiOperation(value = "不同用户不同菜单权限接口")
//    public DataResult<LayuiMiniPermissionVo> getHomePermissionInfo(HttpServletRequest request){
//        // 获取 由前端发送过来的token
//        String accessToken = request.getHeader(Constant.ACCESS_TOKEN);
//        // 通过 token 获取 userId
//        String userId = JwtTokenUtil.getUserId(accessToken);
//
//        DataResult<LayuiMiniPermissionVo> result = DataResult.success();
//
//        result.setData(homeService.getHomePermissionInfo(userId));
//
//        return result;
//    }
    @GetMapping("/home/permission")
    @ApiOperation(value = "不同用户不同菜单权限接口")
    @MyLog(title = "首页模块",action = "不同用户不同菜单权限接口")
    public LayuiMiniPermissionVo initUserMenu(){

        // 通过redis获取用户id
        // 解决不能通过HttpServletRequest request 获取用户id的问题
        // 在登陆的时候将userId存入redis ，然后在过去菜单权限页面的时候再获取
        String userId = (String) redisService.get(Constant.USERID_KEY);

        LayuiMiniPermissionVo homePermissionInfo = homeService.getHomePermissionInfo(userId);

        return homePermissionInfo;
    }

    /**
     * 将JSON数据格式化并保存到文件中
     * @param jsonData 需要输出的json数
     * @param filePath 输出的文件地址
     * @return
     */
    public boolean createJsonFile(Object jsonData, String filePath) {
        String content = JSON.toJSONString(jsonData, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
        // 标记文件生成是否成功
        boolean flag = true;
        // 生成json格式文件
        try {
            // 保证创建一个新文件
            File file = new File(filePath);
            if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
                file.getParentFile().mkdirs();
            }
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            file.createNewFile();
            // 将格式化后的字符串写入文件
            Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            write.write(content);
            write.flush();
            write.close();
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }


}