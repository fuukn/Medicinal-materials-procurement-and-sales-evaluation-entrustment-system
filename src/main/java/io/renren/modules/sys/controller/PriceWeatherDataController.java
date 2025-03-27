package io.renren.modules.sys.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.sys.entity.PriceData;
import io.renren.modules.sys.entity.PriceWeatherDataEntity;
import io.renren.modules.sys.service.PriceWeatherDataService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


/**
 * 
 *
 * @author gjz
 * @email 1826473923@qq.com
 * @date 2025-03-06 21:17:40
 */
@RestController
@RequestMapping("priceweatherdata/")
public class PriceWeatherDataController {
    @Autowired
    private PriceWeatherDataService priceWeatherDataService;

    /**
     * 列表
     */
    @RequestMapping("/list")
//    @RequiresPermissions("generator:priceweatherdata:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = priceWeatherDataService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
//    @RequiresPermissions("generator:priceweatherdata:info")
    public R info(@PathVariable("id") Integer id){
		PriceWeatherDataEntity priceWeatherData = priceWeatherDataService.getById(id);
        return R.ok().put("priceWeatherData", priceWeatherData);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
//    @RequiresPermissions("generator:priceweatherdata:save")
    public R save(@RequestBody PriceWeatherDataEntity priceWeatherData){
		priceWeatherDataService.save(priceWeatherData);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
//    @RequiresPermissions("generator:priceweatherdata:update")
    public R update(@RequestBody PriceWeatherDataEntity priceWeatherData){
		priceWeatherDataService.updateById(priceWeatherData);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
//    @RequiresPermissions("generator:priceweatherdata:delete")
    public R delete(@RequestBody Integer[] ids){
		priceWeatherDataService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping("/selectAll")
    public R selectAll(){
        List<PriceWeatherDataEntity> list = priceWeatherDataService.list();
        return R.ok().put("data",list);
    }

    @GetMapping("/export")
    public void exportExcel(HttpServletResponse response) {
        priceWeatherDataService.exportToExcel(response);
    }
}
