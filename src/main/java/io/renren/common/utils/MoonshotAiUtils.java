package io.renren.common.utils;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MoonshotAiUtils {

    public static String chat(String analyse, String content) {
        try {

            MediaType mediaType = MediaType.parse("application/json");

            // 使用 JSONObject 构建请求体
            JSONObject requestBodyJson = new JSONObject();
            requestBodyJson.set("model", "moonshot-v1-8k");

            // 使用 JSONArray 构建 messages 数组
            JSONArray messages = new JSONArray();

            // 构建 System message 对象
            JSONObject systemMessage = new JSONObject();
            systemMessage.set("role", "system");
            // **重要：请检查这里的 system message 内容是否正确**
            systemMessage.set("content", "你是一个专业的市场评估员，你将根据提供的市场分析结果和用户问题，给出专业、有条理的回复。分析结果：${analysis}；用户问题：${question}");
            messages.add(systemMessage);

            // 构建 User message 对象
            JSONObject userMessage = new JSONObject();
            userMessage.set("role", "user");
            // 将 analyse 和 content 拼接成用户消息的文本内容，然后由 Hutool 自动转义
            String userContentText = "分析:" + analyse + "用户:" + content;
            userMessage.set("content", userContentText); // <-- Hutool 会自动转义 userContentText 中的特殊字符
            messages.add(userMessage);

            requestBodyJson.set("messages", messages);
            requestBodyJson.set("stream", false);

            // 将构建好的 JSONObject 转换为字符串作为请求体
            RequestBody body = RequestBody.create(mediaType, requestBodyJson.toString());

            // ... (构建 Request 和执行调用的代码，以及后续的响应检查和解析) ...
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)  // 连接超时
                    .writeTimeout(30, TimeUnit.SECONDS)    // 写入超时
                    .readTimeout(30, TimeUnit.SECONDS)     // 读取超时
                    .build();
            Request request = new Request.Builder()
                    .url("https://api.moonshot.cn/v1/chat/completions")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer sk-ggGcoAPLmc2XXtQYmgEb2gasQvypPnhRiq3BTOh0vk0qI0bd")
                    .addHeader("content-type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();

            // **重要：在这里增加对 response 和 response body 的详细检查和错误日志记录**
            // 这是诊断问题的关键步骤！
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "N/A";
                System.err.println("AI Chat API non-successful response: " + response.code() + ", body: " + errorBody); // 打印非成功响应的状态码和响应体
                return "调用ai失败: HTTP错误码 " + response.code() + ", 响应体: " + errorBody; // 返回详细错误信息
            }

            String responseBody = response.body() != null ? response.body().string() : null;

            if (responseBody == null || responseBody.trim().isEmpty()) {
                System.err.println("AI Chat API returned empty body."); // 记录空响应
                return "调用ai失败: AI返回空响应";
            }

            try {
                JSONObject jsonObject = JSONUtil.parseObj(responseBody);

                // **重要：在获取 "choices" 之前进行检查**
                JSONArray choices = jsonObject.getJSONArray("choices");
                if (choices == null || choices.isEmpty()) {
                    // <-- 如果 choices 为 null 或空，打印完整的 responseBody
                    System.err.println("AI Chat API response missing 'choices' array or array is empty. Full response: " + responseBody);
                    // 尝试从响应中提取 AI 返回的错误信息（如果存在）
                    JSONObject error = jsonObject.getJSONObject("error");
                    if (error != null) {
                        return "调用ai失败: AI响应无choices, 错误信息: " + error.getStr("message", "未知错误");
                    }
                    return "调用ai失败: AI响应格式异常";
                }

                // 如果 choices 存在且不为空，再安全地获取第一个元素
                JSONObject jsonObject1 = choices.getJSONObject(0); // <-- 使用 getJSONObject(0) 更安全
                if (jsonObject1 == null) {
                    System.err.println("AI Chat API response 'choices' array contains null element. Full response: " + responseBody);
                    return "调用ai失败: AI响应choices元素异常";
                }


                JSONObject message = jsonObject1.getJSONObject("message");
                if (message == null) {
                    System.err.println("AI Chat API response missing 'message' object in first choice. Full response: " + responseBody);
                    return "调用ai失败: AI响应message对象异常";
                }

                String str = message.getStr("content");
                if (str == null || str.trim().isEmpty()) {
                    System.err.println("AI Chat API response missing or empty 'content' in message. Full response: " + responseBody);
                    return "调用ai失败: AI响应content内容异常";
                }


                return str; // 返回 AI 的回复内容

            } catch (JSONException e) {
                System.err.println("Failed to parse AI Chat API response as JSON. Full response: " + responseBody); // 打印原始响应和解析错误
                e.printStackTrace();
                return "调用ai失败: 解析AI响应失败";
            }

        } catch (IOException e) {
            System.err.println("AI Chat API network or IO error: " + e.getMessage()); // 记录网络或 IO 错误
            e.printStackTrace();
            return "调用ai失败: 网络或IO错误";
        }
    }
    public static String keyword(String NewsContent){
        try {
            // 设置 OkHttpClient 的超时参数
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)  // 连接超时
                    .writeTimeout(30, TimeUnit.SECONDS)    // 写入超时
                    .readTimeout(30, TimeUnit.SECONDS)     // 读取超时
                    .build();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\n" +
                    "  \"model\": \"moonshot-v1-8k\",\n" +
                    "  \"messages\": [\n" +
                    "    {\"role\": \"system\", \"content\": \"你是一个专业的市场评估员，你将通过新闻内容总结出两个同义影响价格事件的不超过4个字关键词，输出的json格式示例：{keyword1:xx,keyword2:xx}\"},\n" +
                    "    {\"role\": \"user\", \"content\": \"" + NewsContent + "\"}\n" +
                    "  ],\n" +
                    "  \"stream\": false\n" +
                    "}");
            Request request = new Request.Builder()
                    .url("https://api.moonshot.cn/v1/chat/completions")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer sk-ggGcoAPLmc2XXtQYmgEb2gasQvypPnhRiq3BTOh0vk0qI0bd")
                    .addHeader("content-type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
            JSONObject jsonObject = JSONUtil.parseObj(response.body().string());
            JSONObject jsonObject1 = (JSONObject)jsonObject.getJSONArray("choices").get(0);
            String str = jsonObject1.getJSONObject("message").getStr("content");
            return str;
        } catch (IOException e) {
            e.printStackTrace();
            return "调用ai失败";
        }

    }

    public static void main(String[] args) {
        System.out.println(chat("今日价格18","我是一名沈阳皇姑区小中药堂，现在川芎库存尚充足，请问未来价格变动如何，请给出采购建议"));
    }
}
