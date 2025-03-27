package io.renren.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.modules.sys.dao.AppraiserDao;
import io.renren.modules.sys.entity.AppraiserEntity;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.form.AppraiserForm;
import io.renren.modules.sys.service.AppraiserService;
import io.renren.modules.sys.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;


@Service("appraiserService")
public class AppraiserServiceImpl extends ServiceImpl<AppraiserDao, AppraiserEntity> implements AppraiserService {
    @Autowired
    @Lazy
    private SysUserService sysUserService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AppraiserEntity> page = this.page(
                new Query<AppraiserEntity>().getPage(params),
                new QueryWrapper<AppraiserEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public AppraiserForm show(Long id,AppraiserEntity appraiserEntity){
        SysUserEntity sysUserEntity = sysUserService.getById(id);
        AppraiserForm appraiserForm = new AppraiserForm();
        appraiserForm.setUserName(sysUserEntity.getUsername());
        appraiserForm.setEmail(sysUserEntity.getEmail());
        appraiserForm.setMobile(sysUserEntity.getMobile());
        appraiserForm.setSchool(appraiserEntity.getSchool());
        appraiserForm.setIntroduction(appraiserEntity.getIntroduction());
        appraiserForm.setWorkYears(appraiserEntity.getWorkYears());
        appraiserForm.setSpecialty(appraiserEntity.getSpecialty());
        appraiserForm.setRegisterDate(sysUserEntity.getCreateTime());
        appraiserForm.setRating("4.5");
        return appraiserForm;
    }

    @Override
    public void edit(AppraiserForm appraiserForm,Long userId){
        SysUserEntity sysUserEntity = sysUserService.getById(userId);
        QueryWrapper<AppraiserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId); // 字段名是数据库中的列名
        AppraiserEntity appraiserEntity = this.getBaseMapper().selectOne(wrapper);
        sysUserEntity.setMobile(appraiserForm.getMobile());
        sysUserEntity.setEmail(appraiserForm.getEmail());
        appraiserEntity.setSchool(appraiserForm.getSchool());
        appraiserEntity.setIntroduction(appraiserForm.getIntroduction());
        appraiserEntity.setWorkYears(appraiserForm.getWorkYears());
        appraiserEntity.setSpecialty(appraiserForm.getSpecialty());
        sysUserService.saveOrUpdate(sysUserEntity);
        saveOrUpdate(appraiserEntity);

    }
    // 文件保存到 src/main/resources/static/uploads/
    private static final String UPLOAD_DIR = "F:\\gitProject\\renren\\renren-fast\\src\\main\\resources\\static\\uploads\\";

    @Override
    public String uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String fileExt = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExt;

            // 保存路径使用 \
            String uploadPath = UPLOAD_DIR + uniqueFilename;
            File dest = new File(uploadPath);

            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }

            file.transferTo(dest);

            // 返回 Web 可用的 URL
            return "/uploads/" + uniqueFilename; // 前端访问用 /uploads/xxx.png
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
}