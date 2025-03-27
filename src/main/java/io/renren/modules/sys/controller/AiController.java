package io.renren.modules.sys.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.renren.common.utils.MoonshotAiUtils;
import io.renren.modules.sys.service.OpenAIService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static io.renren.common.utils.MoonshotAiUtils.chat;

@RestController
public class AiController {

    private final OpenAIService openAIService;

    public AiController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @PostMapping("/generate-report")
    public Map<String, Object> generateReport(@RequestBody String inputData) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            // 假设 inputData 是 JSON 字符串格式
            JSONObject jsonObject = JSONUtil.parseObj(inputData);
            String content = jsonObject.getStr("content");

            // 调用 AI 接口
            String aiResponse = MoonshotAiUtils.chat(content);

            // 假设调用 AI 接口成功，返回 code 为 0
            responseMap.put("code", 0);
            responseMap.put("data", aiResponse);
        } catch (Exception e) {
            // 如果调用 AI 接口失败，返回 code 为非 0 值，并附带错误信息
            responseMap.put("code", 1);
            responseMap.put("message", "调用 AI 失败: " + e.getMessage());
        }
        return responseMap;
    }


}
