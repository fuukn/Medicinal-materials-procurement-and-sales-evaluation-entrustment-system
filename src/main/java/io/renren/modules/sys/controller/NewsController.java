package io.renren.modules.sys.controller;

import java.util.Arrays;
import java.util.Map;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.sys.entity.NewsEntity;
import io.renren.modules.sys.service.NewsService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;





/**
 * 
 *
 * @author gjz
 * @email 1826473923@qq.com
 * @date 2025-02-15 09:47:41
 */
@RestController
@RequestMapping("/news")
public class NewsController {
    @Autowired
    private NewsService newsService;

    /**
     * 列表
     */
    @RequestMapping("/list")
//    @RequiresPermissions("sys:news:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = newsService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
//    @RequiresPermissions("sys:news:info")
    public R info(@PathVariable("id") Integer id){
		NewsEntity news = newsService.getById(id);

        return R.ok().put("news", news);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
//    @RequiresPermissions("sys:news:save")
    public R save(@RequestBody NewsEntity news){
		newsService.save(news);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
//    @RequiresPermissions("sys:news:update")
    public R update(@RequestBody NewsEntity news){
		newsService.updateById(news);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
//    @RequiresPermissions("sys:news:delete")
    public R delete(@RequestBody Integer[] ids){
		newsService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
