package io.renren.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;

import io.renren.common.utils.Query;
import io.renren.common.utils.R;
import io.renren.modules.sys.dao.EvaluationDelegationDao;
import io.renren.modules.sys.entity.AppraiserEntity;
import io.renren.modules.sys.entity.EvaluationDelegationEntity;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.form.EntrustForm;
import io.renren.modules.sys.service.AppraiserService;
import io.renren.modules.sys.service.EvaluationDelegationService;
import io.renren.modules.sys.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;






@Service("evaluationDelegationService")
public class EvaluationDelegationServiceImpl extends ServiceImpl<EvaluationDelegationDao, EvaluationDelegationEntity> implements EvaluationDelegationService {
    @Autowired
    private AppraiserService appraiserService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<EvaluationDelegationEntity> page = this.page(
                new Query<EvaluationDelegationEntity>().getPage(params),
                new QueryWrapper<EvaluationDelegationEntity>()
                .eq("status",1)
        );
        return new PageUtils(page);
    }

    @Override
    public void add(EntrustForm entrustForm, Long userId) {
        EvaluationDelegationEntity evaluationDelegationEntity = new EvaluationDelegationEntity();
        // 设置委托信息（除了id, userId, status, createDate外的字段）
        evaluationDelegationEntity.setHerbalMedicine(entrustForm.getHerbalMedicine());
        evaluationDelegationEntity.setAddress(entrustForm.getAddress());
        evaluationDelegationEntity.setPhone(entrustForm.getPhone());
        evaluationDelegationEntity.setScale(entrustForm.getScale());
        evaluationDelegationEntity.setDetails(entrustForm.getDetails());

        // 设置系统自动处理的字段
        evaluationDelegationEntity.setUserId(userId); // 设置当前用户ID
        evaluationDelegationEntity.setStatus(1);     // 默认状态：待处理
        evaluationDelegationEntity.setCreateDate(new Date()); // 当前时间
        // 保存到数据库
       save(evaluationDelegationEntity);

    }

    @Override
    public void accept(EvaluationDelegationEntity delegation,Long userId){
        delegation.setStatus(2); // 更新为“等待中”
        delegation.setAppraiserId(userId); // 需从认证信息获取
        this.updateById(delegation);
//        AppraiserEntity appraiserEntity = appraiserService.getOne(new QueryWrapper<AppraiserEntity>().eq("user_id",userId));
//        appraiserEntity.setReportList(appraiserEntity.getReportList()+delegation.getId()+",");
    }
    @Override
    public void approve(Long id,Integer select){
        EvaluationDelegationEntity delegationEntity = this.getById(id);
        if(select == 1){
            delegationEntity.setStatus(3);
            Long appraiserId = delegationEntity.getAppraiserId();
            AppraiserEntity appraiserEntity = appraiserService.getOne(new QueryWrapper<AppraiserEntity>().eq("user_id", appraiserId));  // 假设数据库字段名为 user_id
            if(appraiserEntity.getReportList() == null){
                appraiserEntity.setReportList(id + ",");
                this.saveOrUpdate(delegationEntity);
                appraiserService.saveOrUpdate(appraiserEntity);
            }else{
                appraiserEntity.setReportList(appraiserEntity.getReportList() + id + ",");
                this.saveOrUpdate(delegationEntity);
                appraiserService.saveOrUpdate(appraiserEntity);
            }

        }
        if (select == 0){
            delegationEntity.setStatus(1);
            delegationEntity.setAppraiserId(null);
            this.updateById(delegationEntity);
        }

    }
    @Override
    public List<EvaluationDelegationEntity> getTasksByUserId(Long userId){

        QueryWrapper<EvaluationDelegationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)  // 根据 userId 查询
                .orderByDesc("create_date"); // 按创建时间降序排序
        return this.list(queryWrapper);     // 返回任务列表
    }
    @Override
    public List<EvaluationDelegationEntity> getTasksByAPRId(Long userId){
        AppraiserEntity appraiserEntity = appraiserService.getOne(new QueryWrapper<AppraiserEntity>().eq("user_id",userId));
        String reportList = appraiserEntity.getReportList(); // 例如 "2,3,5,"
        String[] idArray = reportList.split(","); // 分割为 ["2", "3", "5", ""]
        List<Long> taskIds = new ArrayList<>();
        for (String idStr : idArray) {
            if (!idStr.trim().isEmpty()) { // 过滤空字符串
                try {
                    taskIds.add(Long.parseLong(idStr.trim())); // 转换为 Long 类型
                } catch (NumberFormatException e) {
                    // 可选：记录日志，忽略无效 ID
                }
            }
        }

        // 如果没有有效的 ID，返回空列表
        if (taskIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 根据 ID 查询 EvaluationDelegationEntity
        QueryWrapper<EvaluationDelegationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", taskIds) // 使用 in 查询匹配所有 ID
                .orderByDesc("create_date"); // 按创建时间降序排序
        return this.list(queryWrapper); // 返回任务列表

    }

}