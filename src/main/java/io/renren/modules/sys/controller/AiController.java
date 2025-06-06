package io.renren.modules.sys.controller;

import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.common.utils.MoonshotAiUtils;
import io.renren.modules.sys.entity.NewsEntity;
import io.renren.modules.sys.service.NewsService;
import io.renren.modules.sys.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static io.renren.common.utils.MoonshotAiUtils.chat;

@RestController
public class AiController {
    private static final String PYTHON_SCRIPT_PATH = "./scripts/analyze.py"; // <-- 修改为您的实际路径
    private static final String PYTHON_EXECUTABLE = "C:/Users/18264/AppData/Local/Programs/Python/Python311/python.exe"; // 或 "python3"，取决于您的环境
    @Autowired
    private NewsService newsService;

    private final OpenAIService openAIService;

    public AiController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @PostMapping("/generate-report")
    public Map<String, Object> generateReport(@RequestBody String inputData) {
        Map<String, Object> responseMap = new HashMap<>();
        String pythonAnalysisOutput = ""; // 用于存储 Python 脚本的输出

        try {
            // ... (获取最新新闻的代码不变) ...
            // 1. 获取最新新闻内容
            QueryWrapper<NewsEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("date");
            Page<NewsEntity> page = new Page<>(1, 1);
            page = newsService.page(page, queryWrapper);

            NewsEntity latestNews = null;
            if (page.getRecords() != null && !page.getRecords().isEmpty()) {
                latestNews = page.getRecords().get(0);
            }

            if (latestNews == null) {
                responseMap.put("code", 1);
                responseMap.put("message", "无法获取最新新闻内容，数据库中可能没有新闻记录。");
                return responseMap;
            }
            String NewsContent = latestNews.getContent();


            // 2. 调用第一个 AI (MoonshotAiUtils.keyword) 获取关键词
            String ai1KeywordResponseStr = MoonshotAiUtils.keyword(NewsContent);

            if (ai1KeywordResponseStr.startsWith("调用ai失败")) { // 根据 MoonshotAiUtils 返回的错误前缀判断
                responseMap.put("code", 1);
                responseMap.put("message", "首次AI调用失败，无法根据新闻获取关键词: " + ai1KeywordResponseStr); // 包含 AI 工具类返回的详细错误信息
                return responseMap;
            }

            // 3. 解析 AI 返回的关键词 JSON 字符串
            String keyword1 = null;
            String keyword2 = null;
            try {
                // 期望 AI 返回格式: {"keyword1":"xx", "keyword2":"xx"}
                JSONObject keywordJson = JSONUtil.parseObj(ai1KeywordResponseStr);
                keyword1 = keywordJson.getStr("keyword1");
                keyword2 = keywordJson.getStr("keyword2");

                if (keyword1 == null || keyword2 == null || keyword1.isEmpty() || keyword2.isEmpty()) {
                    responseMap.put("code", 1);
                    responseMap.put("message", "从AI获取的关键词格式不正确或关键词为空。原始AI响应: " + ai1KeywordResponseStr);
                    return responseMap;
                }

            } catch (JSONException e) {
                responseMap.put("code", 1);
                responseMap.put("message", "解析AI关键词响应失败: " + e.getMessage() + ". 原始AI响应: " + ai1KeywordResponseStr);
                e.printStackTrace(); // 记录解析错误
                return responseMap;
            }


            // 4. 调用 Python 脚本，并传递关键词参数
            ProcessBuilder pb = new ProcessBuilder(
                    PYTHON_EXECUTABLE,
                    PYTHON_SCRIPT_PATH,
                    keyword1,
                    keyword2
            );
            // 设置 PYTHONIOENCODING 环境变量，强制 Python 使用 UTF-8 输出
            pb.environment().put("PYTHONIOENCODING", "utf-8");

            Process process = pb.start();

            // 5. 读取 Python 脚本的标准输出 (分析结果)，指定 UTF-8 编码
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)); // <-- 指定 UTF-8 编码
            StringBuilder pythonOutput = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                pythonOutput.append(line).append("\n");
            }
            pythonAnalysisOutput = pythonOutput.toString(); // 存储输出

            // 6. 读取 Python 脚本的错误输出 (如果有错误信息或警告)，指定 UTF-8 编码
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8)); // <-- 指定 UTF-8 编码
            StringBuilder pythonError = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                pythonError.append(line).append("\n");
            }
            String pythonErrorString = pythonError.toString();

            // 7. 等待 Python 进程执行完成并检查退出码
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                responseMap.put("code", 1);
                responseMap.put("message", "执行Python脚本失败。退出码: " + exitCode);
                responseMap.put("python_stdout", pythonAnalysisOutput);
                responseMap.put("python_stderr", pythonErrorString);
                // e.printStackTrace(); // 记录错误
                return responseMap;
            }

            // 8. 调用第二个 AI (MoonshotAiUtils.chat) 生成最终回复
            // 将 Python 分析结果作为 analyse，用户原始输入 (inputData) 作为 content
            JSONObject userInputJson = JSONUtil.parseObj(inputData); // 再次解析 inputData 字符串，或者使用之前已解析的 jsonObject if available
            String userActualQuery = userInputJson.getStr("content"); // <-- 提取实际的用户问题文本

            if (userActualQuery == null || userActualQuery.isEmpty()) {
                responseMap.put("code", 1);
                responseMap.put("message", "用户输入JSON中缺少或content字段为空，无法继续。");
                return responseMap;
            }
            String ai2ChatResponseStr = MoonshotAiUtils.chat(pythonAnalysisOutput,userActualQuery);

            if (ai2ChatResponseStr.startsWith("调用ai失败")) { // 根据 MoonshotAiUtils 返回的错误前缀判断
                responseMap.put("code", 1);
                responseMap.put("message", "第二次AI调用失败，无法生成最终回复: " + ai2ChatResponseStr); // 包含 AI 工具类返回的详细错误信息
                responseMap.put("python_stdout_before_ai2", pythonAnalysisOutput); // 包含 Python 输出作为参考
                // e.printStackTrace(); // 记录错误
                return responseMap;
            }

            // 9. 构建最终成功响应
            responseMap.put("code", 0);
            responseMap.put("data", ai2ChatResponseStr); // 将第二个 AI 的回复作为最终数据

            // 可以选择在成功时也包含 Python 的 stderr 信息，用于调试警告
            if (pythonErrorString != null && !pythonErrorString.trim().isEmpty()) {
                responseMap.put("python_stderr_warnings", pythonErrorString);
            }


        } catch (IOException | InterruptedException e) {
            // 捕获执行外部命令或等待时的异常
            responseMap.put("code", 1);
            responseMap.put("message", "执行分析过程中的外部命令时发生错误: " + e.getMessage());
            responseMap.put("python_stdout_before_exception", pythonAnalysisOutput); // 包含任何已捕获的 Python 输出
            e.printStackTrace(); // 打印堆栈跟踪到服务器日志
        } catch (Exception e) {
            // 捕获流程中其他未预料的异常 (如 newsService.getOne() 异常等)
            responseMap.put("code", 1);
            responseMap.put("message", "处理请求时发生未知错误: " + e.getMessage());
            responseMap.put("python_stdout_before_exception", pythonAnalysisOutput); // 包含任何已捕获的 Python 输出
            e.printStackTrace(); // 打印堆栈跟踪到服务器日志
        }

        return responseMap;
    }


}
