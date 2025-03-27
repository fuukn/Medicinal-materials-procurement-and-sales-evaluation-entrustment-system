package io.renren.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.sys.entity.AppraiserEntity;
import io.renren.modules.sys.form.AppraiserForm;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 
 *
 * @author gjz
 * @email 1826473923@qq.com
 * @date 2025-03-05 10:48:14
 */
public interface AppraiserService extends IService<AppraiserEntity> {

    PageUtils queryPage(Map<String, Object> params);
    AppraiserForm show(Long id,AppraiserEntity appraiserEntity);
    void edit(AppraiserForm appraiserForm,Long userId);
    String uploadFile(MultipartFile file);
}

