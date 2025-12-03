//
//package tech.aiflowy.admin.controller.ai;
//
//import me.chanjar.weixin.mp.api.WxMpService;
//import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import tech.aiflowy.ai.entity.AiBot;
//import tech.aiflowy.ai.message.thirdPart.MessageHandlerService;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import java.math.BigInteger;
//import java.util.HashMap;
//import java.util.Map;
//import cn.dev33.satoken.annotation.SaIgnore;
//import tech.aiflowy.ai.service.AiBotApiKeyService;
//import org.springframework.util.StringUtils;
//import tech.aiflowy.ai.service.AiBotService;
//import tech.aiflowy.common.domain.Result;
//import tech.aiflowy.common.web.exceptions.BusinessException;
//
//@RestController
//@RequestMapping("/api/v1/message")
//@SaIgnore
//public class ThirdPartMessageController {
//
//    private static final Logger log = LoggerFactory.getLogger(ThirdPartMessageController.class);
//
//    @Resource
//    private WxMpService wxMpService;
//
//    @Resource
//    private MessageHandlerService messageHandlerService;
//
//    @Resource
//    private AiBotApiKeyService aiBotApiKeyService;
//
//    @Resource
//    private AiBotService aiBotService;
//
//
//    /**
//     * 通用消息接收接口
//     * 根据平台类型路由到不同的处理器
//     */
//    @PostMapping(value = "/{platform}")
//    public Object receiveMessage(
//        @PathVariable String platform,
//        @RequestBody
//        String requestBody,
//        @RequestParam
//        Map<String, String> params,
//        HttpServletRequest request
//    ) {
//
//        log.info("接收{}平台消息", platform);
//        log.info("请求参数: {}", params);
//
//        try {
//            // 构建上下文数据
//            Map<String, Object> contextData = new HashMap<>();
//            contextData.put("request", request);
//            contextData.putAll(params);
//
//            // 使用通用消息处理服务
//            Object result = messageHandlerService.handleMessage(platform, requestBody, contextData);
//
//            if (result != null) {
//                return result;
//            }
//
//        } catch (Exception e) {
//            log.error("处理{}平台消息异常", platform, e);
//
//        }
//
//        return "系统繁忙请稍后重试！";
//    }
//
//    /**
//     * 微信签名验证接口（GET请求）
//     */
//    @GetMapping("/wechat")
//    public Result<?> verifyWeChatSignature(
//            @RequestParam("signature") String signature,
//            @RequestParam("timestamp") String timestamp,
//            @RequestParam("nonce") String nonce,
//            @RequestParam("echostr") String echostr,
//            @RequestParam ("apiKey") String apiKey
//    ) {
//
//        if (!StringUtils.hasLength(apiKey)) {
//            log.error("微信服务未配置");
//            return Result.fail("微信服务未配置");
//        }
//
//        // 解析 apiKey
//        BigInteger botId = aiBotApiKeyService.decryptApiKey(apiKey);
//        if (botId == null){
//            log.error("apiKey 解析失败");
//            return Result.fail("apiKey 解析失败");
//        }
//
//        // 查询 bot信息
//        AiBot aiBot = aiBotService.getById(botId);
//        if (aiBot == null || aiBot.getOptions() == null){
//            log.error("bot 不存在或未配置微信公众号信息");
//            return Result.fail("bot 不存在或未配置微信公众号信息");
//        }
//
//        Map<String, Object> options = aiBot.getOptions();
//
//
//        String appId = (String) options.get("weChatMpAppId");
//        String secret = (String) options.get("weChatMpSecret");
//        String token = (String) options.get("weChatMpToken");
//        String aesKey = (String) options.get("weChatMpAesKey");
//
//        // 获取 weChat 配置
//        if (
//            !StringUtils.hasText(appId) ||
//                !StringUtils.hasText(secret) ||
//                !StringUtils.hasText(token)
//            ) {
//                throw new BusinessException("未配置完整微信公众号信息！");
//        }
//
//        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
//        config.setAppId(appId); // 设置微信公众号的appid
//        config.setSecret(secret); // 设置微信公众号的app corpSecret
//        config.setToken(token); // 设置微信公众号的token
//        config.setAesKey(aesKey); // 设置微信公众号的EncodingAESKey
//
//        wxMpService.setWxMpConfigStorage(config);
//
//
//
//        log.info("微信签名验证: signature={}, timestamp={}, nonce={}, echostr={}",
//            signature, timestamp, nonce, echostr);
//
//        try {
//            if (wxMpService.checkSignature(timestamp, nonce, signature)) {
//                log.info("微信签名验证成功");
//                return Result.ok(echostr);
//            } else {
//                log.error("微信签名验证失败");
//                return Result.fail("微信签名验证失败");
//            }
//        } catch (Exception e) {
//            log.error("微信签名验证异常", e);
//            return Result.fail("微信签名验证异常");
//        }
//    }
//
//
//}
