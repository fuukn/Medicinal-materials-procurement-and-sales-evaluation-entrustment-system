package io.renren.modules.sys.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ScriptController {
    private static final Logger logger = LoggerFactory.getLogger(ScriptController.class);
    @Value("${python.path}")
    private String pythonPath;

    @Value("${script.path}")
    private String scriptPath;

    @GetMapping("/run-scripts")
    @Scheduled(cron = "0 0 6 * * ?")
    public String runPythonScripts() {
        logger.info("开始执行Python脚本...");
        try {
            // 使用ProcessBuilder执行Python脚本
            ProcessBuilder pb = new ProcessBuilder(pythonPath, scriptPath);
            pb.redirectErrorStream(true); // 将错误流合并到输入流
            Process process = pb.start();
            // 读取脚本输出
            List<String> output = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.add(line);
                }
            }
            logger.info("脚本执行完成");
            // 等待脚本执行完成
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return "脚本执行成功！\n输出:\n" + String.join("\n", output);
            } else {
                return "脚本执行失败，退出码: " + exitCode + "\n输出:\n" + String.join("\n", output);
            }
        } catch (Exception e) {
            return "执行脚本时发生错误: " + e.getMessage();
        }
    }
}
