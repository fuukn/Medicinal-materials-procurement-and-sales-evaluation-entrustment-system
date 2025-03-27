package io.renren.modules.sys.model;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

public class ChatCompletionRequest {

    private String model = "moonshot-v1-8k";
    private List<Message> messages;
    private double temperature = 0.3;

    public ChatCompletionRequest(String userInput) {
        this.messages = Arrays.asList(
                new Message("system", "你是深耕药材市场的相关专业人员负责川芎等药材市场价格预测评估，擅长对药材供需有自己的专业分析。"),
                new Message("user", userInput)
        );
    }

    // Getters and setters
    @Data
    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        // Getters and setters
    }
}
