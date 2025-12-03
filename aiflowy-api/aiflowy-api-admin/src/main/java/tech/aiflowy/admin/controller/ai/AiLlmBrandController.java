//package tech.aiflowy.admin.controller.ai;
//
//import cn.dev33.satoken.annotation.SaCheckPermission;
//import com.alicp.jetcache.Cache;
//import tech.aiflowy.ai.entity.AiLlmBrand;
//import tech.aiflowy.ai.utils.AiBotChatUtil;
//import tech.aiflowy.common.annotation.UsePermission;
//import tech.aiflowy.common.domain.Result;
//import tech.aiflowy.common.web.controller.BaseController;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import tech.aiflowy.common.web.exceptions.BusinessException;
//
//import javax.annotation.Resource;
//import java.util.List;
//
//import static tech.aiflowy.ai.utils.AiBotChatUtil.LLM_BRAND_KEY;
//
///**
// * 控制层。
// *
// * @author michael
// * @since 2024-08-23
// */
//@RestController
//@RequestMapping("/api/v1/aiLlmBrand")
//@UsePermission(moduleName = "/api/v1/aiLlm")
//public class AiLlmBrandController extends BaseController {
//
//    @Resource
//    private Cache<String, Object> cache;
//
//
//    @RequestMapping("list")
//    @SaCheckPermission("/api/v1/aiLlm/query")
//    public Result<List<AiLlmBrand>> list(){
//
//        Object o = cache.get(AiBotChatUtil.LLM_BRAND_KEY);
//
//        List<AiLlmBrand> brandList = null;
//
//        if (o != null) {
//            brandList = (List<AiLlmBrand>) o;
//        } else {
//            brandList = AiLlmBrand.fromJsonConfig();
//            if (brandList == null || brandList.isEmpty()) {
//                throw new BusinessException("获取解析供应商列表结果为空，请检查配置文件！");
//            }
//            cache.put(AiBotChatUtil.LLM_BRAND_KEY, brandList);
//        }
//
//
//        return Result.ok(brandList);
//    }
//}