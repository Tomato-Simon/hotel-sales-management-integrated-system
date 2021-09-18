package group.oneonetwo.hotelintelligencesystem.modules.dept.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import group.oneonetwo.hotelintelligencesystem.exception.CommonException;
import group.oneonetwo.hotelintelligencesystem.exception.SavaException;
import group.oneonetwo.hotelintelligencesystem.modules.dept.dao.DeptMapper;
import group.oneonetwo.hotelintelligencesystem.modules.dept.model.po.DeptPO;
import group.oneonetwo.hotelintelligencesystem.modules.dept.model.vo.DeptVO;
import group.oneonetwo.hotelintelligencesystem.modules.dept.service.IDeptService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.Query;

/**
 * @author 文
 * @description 部门服务层实现类
 */
@Service
public class DeptServiceImpl implements IDeptService {

    @Autowired
    DeptMapper deptMapper;

    @Override
    public DeptPO add(DeptVO deptVO) {
        if (deptVO == null) {
            throw new SavaException("插入用户失败：user实体为空");
        }
        DeptPO deptPO = new DeptPO();
        BeanUtils.copyProperties(deptVO,deptPO);
        int insert = deptMapper.insert(deptPO);
        if (insert > 0) {
            return deptPO;
        }
        throw new SavaException("插入用户失败");
    }

    @Override
    public DeptVO selectOneByIdReturnVO(String id) {
        if (id == null) {
            throw new CommonException(501,"参数为空");
        }
        DeptPO deptPO = deptMapper.selectById(id);
        DeptVO deptVO = new DeptVO();
        if (deptPO != null) {
            BeanUtils.copyProperties(deptPO,deptVO);
        }
        return deptVO;
    }

    @Override
    public DeptPO save(DeptVO deptVO) {
        if (deptVO == null) {
            throw new CommonException(501,"user实体为空");
        }
        DeptVO check = selectOneByIdReturnVO(deptVO.getId());
        if (check == null) {
            throw new CommonException(4004,"找不到id为'" + deptVO.getId() + "'的数据");
        }
        DeptPO userPO = new DeptPO();
        BeanUtils.copyProperties(deptVO,userPO);
        int save = deptMapper.updateById(userPO);
        if (save > 0) {
            return userPO;
        }
        throw new SavaException("更改用户失败");
    }

    @Override
    public Integer deleteById(String id) {
        DeptVO check = selectOneByIdReturnVO(id);
        if (check == null) {
            throw new CommonException(4004,"找不到id为'" + id + "'的数据");
        }
        int i = deptMapper.deleteById(id);
        return i;
    }


    @Override
    public DeptPO selectOneById(String id) {
        DeptPO deptPO = deptMapper.selectById(id);
        return deptPO;
    }

}