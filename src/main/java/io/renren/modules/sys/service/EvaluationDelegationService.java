package io.renren.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.sys.entity.EvaluationDelegationEntity;
import io.renren.modules.sys.form.EntrustForm;

import java.util.List;
import java.util.Map;

/**
 * 评估委托
 *
 * @author gjz
 * @email 1826473923@qq.com
 * @date 2025-03-08 23:05:08
 */
public interface EvaluationDelegationService extends IService<EvaluationDelegationEntity> {

    PageUtils queryPage(Map<String, Object> params);
    void add(EntrustForm entrustForm, Long userId);
    void accept(EvaluationDelegationEntity evaluationDelegationEntity,Long userId);
    void approve(Long id,Integer select);
    List<EvaluationDelegationEntity> getTasksByUserId(Long userId);
    List<EvaluationDelegationEntity> getTasksByAPRId(Long userId);
}

