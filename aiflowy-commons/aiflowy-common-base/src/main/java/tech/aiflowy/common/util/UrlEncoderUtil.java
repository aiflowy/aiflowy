package tech.aiflowy.common.util;

import java.net.IDN;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlEncoderUtil {

    // 匹配URL的「协议+域名」部分（如 http://localhost:8080 或 https://www.baidu.商店）
    private static final Pattern URL_DOMAIN_PATTERN = Pattern.compile("^((http|https)://[^/]+)(/.*)?$");
    // 匹配连续的斜杠（用于清理多余/）
    private static final Pattern MULTIPLE_SLASH_PATTERN = Pattern.compile("/+");

    /**
     * 完整URL编码：兼容IDN域名 + 路径/文件名URL编码 + 自动清理多余斜杠
     * @param url 完整URL（如：http://localhost:8080//attachment/host 副本.txt）
     * @return 编码后URL（如：http://localhost:8080/attachment/host%20%E5%89%AF%E6%9C%AC.txt）
     */
    public static String getEncodedUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }

        // 第一步：先清理所有连续的斜杠（// → /），保留协议后的//（如 http://）
        String cleanUrl = cleanMultipleSlashes(url);

        Matcher matcher = URL_DOMAIN_PATTERN.matcher(cleanUrl);
        String domainPart = "";   // 协议+域名部分（如 http://localhost:8080）
        String pathPart = "";     // 路径+文件名部分（如 /attachment/host 副本.txt）

        // 1. 拆分URL为「域名部分」和「路径部分」
        if (matcher.matches()) {
            domainPart = matcher.group(1);
            pathPart = matcher.group(3) == null ? "" : matcher.group(3);
        } else {
            // 无路径的纯域名（如 http://www.baidu.商店）
            domainPart = cleanUrl;
        }

        // 2. 处理域名部分：IDN域名转Punycode编码（如 商店 → xn--3ds443g）
        String encodedDomain = encodeDomain(domainPart);

        // 3. 处理路径部分：URL编码（保留/，编码空格/中文）
        String encodedPath = encodePath(pathPart);

        // 4. 拼接完整URL（再次清理可能的多余斜杠）
        String finalUrl = encodedDomain + encodedPath;
        return cleanMultipleSlashes(finalUrl);
    }

    /**
     * 清理URL中多余的连续斜杠（保留协议后的//，如 http://）
     */
    private static String cleanMultipleSlashes(String url) {
        if (url.startsWith("http://")) {
            return "http://" + MULTIPLE_SLASH_PATTERN.matcher(url.substring(7)).replaceAll("/");
        } else if (url.startsWith("https://")) {
            return "https://" + MULTIPLE_SLASH_PATTERN.matcher(url.substring(8)).replaceAll("/");
        } else {
            // 非HTTP/HTTPS URL，直接替换所有连续斜杠
            return MULTIPLE_SLASH_PATTERN.matcher(url).replaceAll("/");
        }
    }

    /**
     * 编码域名：IDN域名转Punycode（处理中文/特殊字符域名）
     */
    private static String encodeDomain(String domain) {
        if (domain.isEmpty()) {
            return "";
        }
        // 拆分协议和域名（如 http:// + www.baidu.商店）
        String protocol = "";
        String pureDomain = domain;
        if (domain.startsWith("http://")) {
            protocol = "http://";
            pureDomain = domain.substring(7);
        } else if (domain.startsWith("https://")) {
            protocol = "https://";
            pureDomain = domain.substring(8);
        }

        // IDN域名转Punycode（核心：处理中文后缀如「商店」）
        String punycodeDomain = IDN.toASCII(pureDomain);
        return protocol + punycodeDomain;
    }

    /**
     * 编码路径：仅编码路径/文件名中的特殊字符，保留/
     */
    private static String encodePath(String path) {
        if (path.isEmpty()) {
            return "";
        }
        // 按/拆分路径段，逐个编码后拼接（避免/被编码）
        String[] pathSegments = path.split("/");
        StringBuilder encodedPath = new StringBuilder();
        for (String segment : pathSegments) {
            if (!segment.isEmpty()) {
                String encodedSegment = URLEncoder.encode(segment, StandardCharsets.UTF_8)
                        .replace("+", "%20")    // 空格转%20
                        .replace("%2F", "/");   // 保留段内的/（如有）
                encodedPath.append("/").append(encodedSegment);
            } else {
                encodedPath.append("/"); // 保留空段（如开头的/）
            }
        }
        // 处理末尾的/（避免多拼接）
        String result = encodedPath.length() > 0 ? encodedPath.toString() : path;
        // 清理路径中的多余斜杠
        return MULTIPLE_SLASH_PATTERN.matcher(result).replaceAll("/");
    }
}