package tech.aiflowy.ai.agentsflex.ocrModel;

import com.agentsflex.core.model.config.BaseModelConfig;

import java.util.HashMap;
import java.util.Map;

public class OcrModelConfig extends BaseModelConfig {

    protected Map<String, String> headers;

    public Map<String, String> getHeaders() {
        if (headers == null) {
            headers = new HashMap<>();
        }
        String apiKey = getApiKey();
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            headers.remove("Authorization");
            headers.put("Authorization", "Bearer " + apiKey.trim());
        }
        return Map.copyOf(headers);
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
