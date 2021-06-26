package com.pan.service.impl;

import com.pan.contants.Constant;
import com.pan.dao.SysClassDao;
import com.pan.entity.SysClass;
import com.pan.entity.SysUser;
import com.pan.exception.BusinessException;
import com.pan.exception.code.BaseResponseCode;
import com.pan.service.ClassService;
import com.pan.service.RedisService;
import com.pan.service.UserService;
import com.pan.utils.CodeUtil;
import com.pan.vo.req.ClassAddVo;
import com.pan.vo.req.ClassUpdateVo;
import com.pan.vo.resp.ClassRespNodeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisServer;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ClassServiceImpl implements ClassService {

    @Autowired
    private SysClassDao sysClassDao;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserService userService;

    @Override
    public List<SysClass> getALlClassInfo() {
        List<SysClass> list = sysClassDao.getAllClassInfo();

        // 设置 上级部门名称
        for (SysClass sysClass : list){
            SysClass parent = sysClassDao.selectByPrimaryKey(sysClass.getPid());
            if (parent != null){
                sysClass.setPidName(parent.getName());
            }
        }

        return list;
    }

    /**
     * 在原先的接口上加入一个参数，部门主键，
     * 如果传有数据过来就表示部门编辑功能   需要排除自己和它的子集叶子节点；
     * 如果不传就代表是新增部门数据
     */
    @Override
    public List<ClassRespNodeVo> getClassTree(String classId) {
        List<SysClass> list = sysClassDao.getAllClassInfo();
        // 如果classId有值 说明 是编辑功能，
        // 将所选中的班级在班级树中移除，就是将自己从班级树移除
        if (!StringUtils.isEmpty(classId) && !list.isEmpty()) {
            for (SysClass sysClass : list) {
                if (sysClass.equals(classId)) {
                    list.remove(classId);
                    break;
                }
            }
        }

        // 设置最顶级的信息
        ClassRespNodeVo classRespNodeVo = new ClassRespNodeVo();
        classRespNodeVo.setId("0");
        classRespNodeVo.setTitle("广西民族大学");
        classRespNodeVo.setSpread(true);
        //  获取子集信息
        classRespNodeVo.setChildren(getTree(list));
        
        List<ClassRespNodeVo> classRespNodeVoList = new ArrayList<>();
        classRespNodeVoList.add(classRespNodeVo);
        return classRespNodeVoList;
    }

    private List<ClassRespNodeVo> getTree(List<SysClass> all) {
        List<ClassRespNodeVo> list = new ArrayList<>();

        for (SysClass sysClass : all){
            // 如果她的父类id是最顶级
            if(sysClass.getPid().equals("0")){
                ClassRespNodeVo respNodeVo = new ClassRespNodeVo();
                BeanUtils.copyProperties(sysClass, respNodeVo);
//                respNodeVo.setId(sysClass.getId());
                respNodeVo.setTitle(sysClass.getName());
                respNodeVo.setSpread(true);
                respNodeVo.setChildren(getChildren(all, sysClass.getId()));

                list.add(respNodeVo);
            }
        }

        return list;
    }

    private List<ClassRespNodeVo> getChildren(List<SysClass> all, String id) {
        List<ClassRespNodeVo> list = new ArrayList<>();

        for (SysClass sysClass :all){
            // 如果这个信息 是 刚刚传来的信息的子集
            if(sysClass.getPid().equals(id)){
                ClassRespNodeVo respNodeVo = new ClassRespNodeVo();
                BeanUtils.copyProperties(sysClass, respNodeVo);

                respNodeVo.setSpread(true);
                respNodeVo.setTitle(sysClass.getName());
//                respNodeVo.setId(sysClass.getId());
                respNodeVo.setChildren(getChildren(all, sysClass.getId()));

                list.add(respNodeVo);
            }
        }

        return list;
    }

    /**
     * 添加操作
     * @param vo
     * @return
     */
    @Override
    public SysClass addClassInfo(ClassAddVo vo) {
        // 设置 relationCode 和 classCode
        String relationCode;
        String classCode = null;
        // 这个还搞不懂
        long result = redisService.incrby(Constant.CLASS_CODE_KEY, 1);
        classCode = CodeUtil.classCode(String.valueOf(result),6,"0");
        // 查询 父类信息
        SysClass parent = sysClassDao.selectByPrimaryKey(vo.getPid());
        // 如果父类是顶级 节点
        // relationCode 就是 classCode
        if (vo.getPid().equals("0")){
            relationCode = classCode;
        } else if (null==parent){
            // 如果parent 为空  则 报错
            log.error("传入的 pid:{}不合法",vo.getPid());
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        } else {
            // 如果父类不是顶级节点
            // 则relationCode 等于 父亲relationCode + classCode
            relationCode = parent.getRelationCode()+classCode;
        }
        // 设置 对象 传入Dao层 =>数据库
        SysClass sysClass = new SysClass();
        BeanUtils.copyProperties(vo,sysClass);
        sysClass.setId(UUID.randomUUID().toString());
        sysClass.setCreateTime(new Date());
        // 设置 relationCode  和 classCOde
        sysClass.setRelationCode(relationCode);
        sysClass.setClassNo(classCode);
        int count = sysClassDao.insertSelective(sysClass);
        if (count != 1){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }

        return sysClass;
    }

    /**
     * 修改班级信息
     */
    @Override
    public void updateClassInfo(ClassUpdateVo vo) {
        // 根据id获取当前班级对象
        SysClass sysClass = sysClassDao.selectByPrimaryKey(vo.getId());
        // 如果为空 说明传入的数据有误
        if (null==sysClass) {
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }
        SysClass updateClass = new SysClass();
        BeanUtils.copyProperties(vo, updateClass);
        updateClass.setUpdateTime(new Date());
        // 修改数据库
        int i = sysClassDao.updateByPrimaryKeySelective(updateClass);
        if (i != 1) {
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }

        /**
         * 以下操作 我还没弄懂 - -！
         * 意思就是：当班级层级发生了变化，他的 relation_code 也要发生变化；
         *          大致分为：1、当 班级从 父节点是根目录 变成 其他目录的子集
         *                  2、其他目录 变成 根目录的子集
         *                  3、其他目录 变成 其他目录的子集
         */

        //就是维护层级关系
        if(!vo.getPid().equals(sysClass.getPid())){
            //子集的部门层级关系编码=父级部门层级关系编码+它本身部门编码
            SysClass newParent=sysClassDao.selectByPrimaryKey(vo.getPid());
            if(!vo.getPid().equals("0")&&null==newParent){
                log.info("修改后的部门在数据库查找不到{}",vo.getPid());
                throw new BusinessException(BaseResponseCode.DATA_ERROR);
            }
            SysClass oldParent=sysClassDao.selectByPrimaryKey(sysClass.getPid());
            String oleRelation;
            String newRelation;

            if(sysClass.getPid().equals("0")){
                //根目录挂靠到其它目录
                oleRelation=sysClass.getRelationCode();
                newRelation=newParent.getRelationCode()+sysClass.getClassNo();
            }else if(vo.getPid().equals("0")){
                //其他目录升级到跟目录
                oleRelation=sysClass.getRelationCode();
                newRelation=sysClass.getClassNo();
            }else {
                oleRelation=oldParent.getRelationCode();
                newRelation=newParent.getRelationCode();
            }
            sysClassDao.updateRelationCode(oleRelation,newRelation,sysClass.getRelationCode());
        }

    }

    /**
     * 删除班级信息  逻辑删除
     * 判断是否该班级有捆绑着用户，如果有，则不给删除
     */
    @Override
    public void deleteClassInfo(String classId) {
        SysClass sysClass = sysClassDao.selectByPrimaryKey(classId);
        // 如果为空，说明传入的id有误
        if (null==sysClass){
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }
        // 判断该班级是否捆绑用户
        // 先找出 班级的所有子集包括自己
        List<String> list = sysClassDao.selectChildIds(sysClass.getRelationCode());
        //判断它和它子集的叶子节点是否关联有用户
        List<SysUser> sysUsers = userService.selectUserInfoByDeptIds(list);
        // 如果关联用户，则不给删除
        if(!sysUsers.isEmpty()){
            throw new BusinessException(BaseResponseCode.NOT_PERMISSION_DELETED_DEPT);
        }

        //逻辑删除部门数据
        int count=sysClassDao.deletedClass(new Date(),list);
        if(count==0){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }

    }
}
