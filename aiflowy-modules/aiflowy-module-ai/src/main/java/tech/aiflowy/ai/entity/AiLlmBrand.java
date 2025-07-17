package tech.aiflowy.ai.entity;

import tech.aiflowy.common.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class AiLlmBrand implements Serializable {

    private String title;
    private String icon;
    private String key;

    private Map<String,Object> options;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    public static List<AiLlmBrand> fromJsonConfig() {
        Resource resource = new ClassPathResource("ai-brands.json");
        try (InputStream is = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            String json = jsonString.toString().trim();
            if (StringUtil.hasText(json)) {
                JSONArray jsonArray = JSON.parseArray(json);
                return jsonArray.toJavaList(AiLlmBrand.class);
            }
            return null;
        } catch (Exception e) {
            LoggerFactory.getLogger(AiLlmBrand.class).error(e.toString(), e);
            return null;
        }
    }
}
