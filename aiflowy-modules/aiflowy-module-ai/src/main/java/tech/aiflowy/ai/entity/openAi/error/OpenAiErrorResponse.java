package tech.aiflowy.ai.entity.openAi.error;

public class OpenAiErrorResponse {

    private String message;

    private String type;

    private String param;

    private String code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public OpenAiErrorResponse(String message, String type, String param, String code) {
        this.message = message;
        this.type = type;
        this.param = param;
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    @Override
    public String toString() {
        return "OpenAiErrorResponse{" +
                "message='" + message + '\'' +
                ", type='" + type + '\'' +
                ", param='" + param + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
