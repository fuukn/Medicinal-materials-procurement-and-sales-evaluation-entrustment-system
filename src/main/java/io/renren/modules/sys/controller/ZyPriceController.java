package io.renren.modules.sys.controller;


import io.renren.common.utils.R;
import io.renren.modules.sys.entity.PriceData;
import io.renren.modules.sys.service.ZyPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("zy/")
 // 替换为前端服务器的地址
//@CrossOrigin(origins = "http://localhost:8001")
public class ZyPriceController {

    @Autowired
    private ZyPriceService zyPriceService;

    @PostMapping("/selectAll")
    public R selectAll(){
        List<PriceData> list = zyPriceService.list();
        return R.ok().put("data",list);
    }


}
