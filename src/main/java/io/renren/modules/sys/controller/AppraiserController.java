package io.renren.modules.sys.controller;

import java.util.Arrays;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.sys.entity.AppraiserEntity;
import io.renren.modules.sys.entity.EvaluationDelegationEntity;
import io.renren.modules.sys.form.AppraiserForm;
import io.renren.modules.sys.service.AppraiserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * 
 *
 * @author gjz
 * @email 1826473923@qq.com
 * @date 2025-03-05 10:48:14
 */
@RestController
@RequestMapping("appraiser/")
public class AppraiserController extends AbstractController{
    @Autowired
    private AppraiserService appraiserService;

    /**
     * 列表
     */
    @RequestMapping("/list")
//    @RequiresPermissions("generator:appraiser:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = appraiserService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info")
//    @RequiresPermissions("generator:appraiser:info")
    public R info() {
        // 获取当前登录用户的 ID
        Long userId = getUserId();
        // 使用 QueryWrapper 构建查询条件
        QueryWrapper<AppraiserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId); // 字段名是数据库中的列名
        AppraiserEntity appraiserEntity = appraiserService.getBaseMapper().selectOne(wrapper);
        // 返回结果
        AppraiserForm appraiser = appraiserService.show(userId,appraiserEntity);
        return R.ok().put("appraiser", appraiser);
    }
    @RequestMapping("/info/{id}")
//    @RequiresPermissions("generator:evaluationdelegation:info")
    public R info(@PathVariable("id") Long id){
        QueryWrapper<AppraiserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", id); // 字段名是数据库中的列名
        AppraiserEntity appraiserEntity = appraiserService.getBaseMapper().selectOne(wrapper);
        // 返回结果
        AppraiserForm appraiser = appraiserService.show(id,appraiserEntity);
        return R.ok().put("appraiser", appraiser);
    }
    /**
     * 编辑信息
     */
    @RequestMapping("/edit")
    public R edit(@RequestBody AppraiserForm appraiserForm){
        Long userId = getUserId();
        appraiserService.edit(appraiserForm,userId);
        return R.ok();
    }
    @PostMapping("/upload")
    public R upload(@RequestParam("file") MultipartFile file) {
        String url = appraiserService.uploadFile(file); // 保存文件并返回 URL
        return R.ok().put("url", url);
    }


    /**
     * 保存
     */
    @RequestMapping("/save")
//    @RequiresPermissions("generator:appraiser:save")
    public R save(@RequestBody AppraiserEntity appraiser){
		appraiserService.save(appraiser);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
//    @RequiresPermissions("generator:appraiser:update")
    public R update(@RequestBody AppraiserEntity appraiser){
		appraiserService.updateById(appraiser);

        return R.ok();
    }
    /**
     * 删除
     */
    @RequestMapping("/delete")
//    @RequiresPermissions("generator:appraiser:delete")
    public R delete(@RequestBody Integer[] ids){
		appraiserService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
