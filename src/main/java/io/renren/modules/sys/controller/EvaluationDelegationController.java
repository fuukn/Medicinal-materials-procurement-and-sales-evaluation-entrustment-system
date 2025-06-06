package io.renren.modules.sys.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.sys.entity.AppraiserEntity;
import io.renren.modules.sys.entity.EvaluationDelegationEntity;
import io.renren.modules.sys.form.ApproveRequest;
import io.renren.modules.sys.form.EntrustForm;
import io.renren.modules.sys.service.AppraiserService;
import io.renren.modules.sys.service.EvaluationDelegationService;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;


/**
 * 评估委托
 *
 * @author gjz
 * @email 1826473923@qq.com
 * @date 2025-03-08 23:05:08
 */
@RestController
@RequestMapping("sys/evaluationdelegation")
public class EvaluationDelegationController extends AbstractController{
    @Autowired
    private EvaluationDelegationService evaluationDelegationService;
    @Autowired
    private AppraiserService appraiserService;
    /**
     * 列表
     */
    @RequestMapping("/list")
//    @RequiresPermissions("generator:evaluationdelegation:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = evaluationDelegationService.queryPage(params);

        return R.ok().put("page", page);
    }
    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
//    @RequiresPermissions("generator:evaluationdelegation:info")
    public R info(@PathVariable("id") Long id){
		EvaluationDelegationEntity evaluationDelegation = evaluationDelegationService.getById(id);

        return R.ok().put("evaluationDelegation", evaluationDelegation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
//    @RequiresPermissions("generator:evaluationdelegation:save")
    public R save(@RequestBody EvaluationDelegationEntity evaluationDelegation){
		evaluationDelegationService.save(evaluationDelegation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
//    @RequiresPermissions("generator:evaluationdelegation:update")
    public R update(@RequestBody EvaluationDelegationEntity evaluationDelegation){
		evaluationDelegationService.updateById(evaluationDelegation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
//    @RequiresPermissions("generator:evaluationdelegation:delete")
    public R delete(@RequestBody Long[] id){
		evaluationDelegationService.removeByIds(Arrays.asList(id));

        return R.ok();
    }

    @PostMapping("/add")
    public ResponseEntity<String> addEvaluationDelegation(@RequestBody EntrustForm entrustForm) {
        try {
            Long userId = getUserId();
            evaluationDelegationService.add(entrustForm, userId);
            return ResponseEntity.ok("评估委托新增成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("新增失败");
        }
    }

    /**
     * 申请委托
     */
    @PostMapping("/accept/{id}")
    public R accept(@PathVariable("id") Long id) {
        EvaluationDelegationEntity delegation = evaluationDelegationService.getById(id);
        Long userId=getUserId();
        try {
            evaluationDelegationService.accept(delegation, userId);
            return R.ok("申请中");
        } catch (Exception e) {
            return R.error("申请异常");
        }
    }

    /**
     * 采销用户批准
     */
    @PostMapping("/approve")
    public R approve(@RequestBody ApproveRequest request) {
        try {
            EvaluationDelegationEntity entity = evaluationDelegationService.getById(request.getId());
            Integer status = entity.getStatus();
            if(status != 3&&status != 4){
                evaluationDelegationService.approve(request.getId(), request.getSelect());
            }else {
                return R.error("请不要重复批准");
            }

            return R.ok("操作成功");
        } catch (Exception e) {
            return R.error("批准异常");
        }
    }

    /**
     * 采销用户发布的委托列表
     */
    @RequestMapping("/CXlist")
    public R CXlist() {
        try {
            Long userId = getUserId();
            if (userId == null) {
                return R.error("用户未登录");
            }
            List<EvaluationDelegationEntity> tasks = evaluationDelegationService.getTasksByUserId(userId);
            return R.ok().put("data", tasks); // 使用 put 添加任务列表
        } catch (Exception e) {
            return R.error("获取委托列表失败: " + e.getMessage());
        }
    }

    /**
     * 评估用户任务列表
     */
    @RequestMapping("/PGlist")
    public R PGlist(){
        try {
            Long userId = getUserId();
            List<EvaluationDelegationEntity> tasks = evaluationDelegationService.getTasksByAPRId(userId);
            return R.ok().put("data", tasks);
        } catch (Exception e) {
            return R.error("获取委托列表失败: " + e.getMessage());
        }
    }

    @PostMapping("/uploadReport")
    public R uploadReport(@RequestParam("file") MultipartFile file, @RequestParam("id") Long id) {
        Long userId = getUserId();
        if (file.isEmpty()) {
            return R.error("上传文件不能为空");
        }
        if (!file.getOriginalFilename().endsWith(".pdf")) {
            return R.error("只支持上传 PDF 文件");
        }

        EvaluationDelegationEntity entity = evaluationDelegationService.getById(id);
        if (entity == null || entity.getStatus() != 3) {
            return R.error("任务状态不支持上传报告");
        }

        try {
            // 文件存储路径：项目根目录下的 upload/report
            String uploadDir = System.getProperty("user.dir") + "/upload/report/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs(); // 创建目录
            }

            // 文件名：使用任务 ID 和时间戳避免覆盖
            String fileName = id + "_" + System.currentTimeMillis() + ".pdf";
            File destFile = new File(uploadDir + fileName);
            file.transferTo(destFile);

            // 更新任务的 report 字段，保存文件路径
            entity.setReport(fileName);
            entity.setStatus(4);
            AppraiserEntity appraiserEntity = appraiserService.getOne(new QueryWrapper<AppraiserEntity>().eq("user_id",userId));
            if (appraiserEntity != null && appraiserEntity.getReportList() != null) {
                String reportList = appraiserEntity.getReportList();
                String idToRemove = id + ",";
                if (reportList.contains(idToRemove)) {
                    String updatedReportList = reportList.replace(idToRemove, "");
                    appraiserEntity.setReportList(updatedReportList);
                    appraiserService.updateById(appraiserEntity);
                }
            }
            evaluationDelegationService.updateById(entity);

            return R.ok("报告上传成功");
        } catch (IOException e) {
            return R.error("文件上传失败: " + e.getMessage());
        }
    }
    @GetMapping("/download")
    public void downloadReport(@RequestParam("delegateId") String delegateId, HttpServletResponse response) {

        EvaluationDelegationEntity entity = evaluationDelegationService.getById(delegateId);
        if (entity == null) {
            throw new RuntimeException("未找到对应 ID 的委托实体: " + delegateId);
        }

        String report = entity.getReport();
        if (report == null || report.isEmpty()) {
            throw new RuntimeException("委托实体中未找到报告");
        }

        String filePath = System.getProperty("user.dir") + "/upload/report/" + report;
        File file = new File(filePath);

        if (!file.exists()) {
            throw new RuntimeException("未找到报告文件" + filePath);
        }

        try (InputStream is = new FileInputStream(file)) {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=" + report);
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException("文件下载失败", e);
        }
    }
}
