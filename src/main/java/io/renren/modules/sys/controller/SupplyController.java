package io.renren.modules.sys.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.renren.modules.sys.entity.SupplyEntity;
import io.renren.modules.sys.service.SupplyService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;



/**
 * 
 *
 * @author gjz
 * @email 1826473923@qq.com
 * @date 2025-05-07 10:17:04
 */
@RestController
@RequestMapping("sys/supply")
public class SupplyController {
    @Autowired
    private SupplyService supplyService;

    /**
     * 列表
     */
    @RequestMapping("/list")
//    @RequiresPermissions("sys:supply:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = supplyService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
//    @RequiresPermissions("sys:supply:info")
    public R info(@PathVariable("id") Integer id){
		SupplyEntity supply = supplyService.getById(id);

        return R.ok().put("supply", supply);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
//    @RequiresPermissions("sys:supply:save")
    public R save(@RequestBody SupplyEntity supply){
		supplyService.save(supply);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
//    @RequiresPermissions("sys:supply:update")
    public R update(@RequestBody SupplyEntity supply){
		supplyService.updateById(supply);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
//    @RequiresPermissions("sys:supply:delete")
    public R delete(@RequestBody Integer[] ids){
		supplyService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
