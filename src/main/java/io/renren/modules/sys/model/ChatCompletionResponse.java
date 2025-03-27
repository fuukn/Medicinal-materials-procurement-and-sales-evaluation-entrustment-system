package io.renren.modules.sys.model;

import java.util.List;

public class ChatCompletionResponse {

    private List<Choice> choices;

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public static class Choice {
        private ChatCompletionRequest.Message message;

        public ChatCompletionRequest.Message getMessage() {
            return message;
        }

        public void setMessage(ChatCompletionRequest.Message message) {
            this.message = message;
        }
    }
}
