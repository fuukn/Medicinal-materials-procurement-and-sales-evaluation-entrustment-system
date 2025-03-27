package io.renren.common.utils;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MoonshotAiUtils {

public static String chat(String content){
    try {
        // 设置 OkHttpClient 的超时参数
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)  // 连接超时
                .writeTimeout(30, TimeUnit.SECONDS)    // 写入超时
                .readTimeout(30, TimeUnit.SECONDS)     // 读取超时
                .build();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"model\": \"moonshot-v1-8k\",\r\n    \"messages\": [\r\n      {\r\n        \"role\": \"system\",\r\n        \"content\": \""+content+"\"\r\n      }\r\n    ],\r\n    \"stream\": false\r\n  }");
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
        chat("你好，你是谁？");
    }
}
