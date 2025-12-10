package tech.aiflowy.usercenter.controller.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.core.dict.Dict;
import tech.aiflowy.core.dict.DictItem;
import tech.aiflowy.core.dict.DictLoader;
import tech.aiflowy.core.dict.DictManager;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 字典
 */
@RestController
@RequestMapping("/api/v1/dict/")
public class DictController {

    @Resource
    DictManager dictManager;

    /**
     * 获取字典项
     */
    @GetMapping("/items/{code}")
    public Result<List<DictItem>> items(@PathVariable("code") String code, String keyword, HttpServletRequest request) {
        DictLoader loader = dictManager.getLoader(code);
        if (loader == null) {
            return Result.ok(Collections.emptyList());
        }
        Map<String, String[]> parameterMap = request.getParameterMap();
        Dict dict = loader.load(keyword, parameterMap);
        if (dict == null) {
            return Result.ok(Collections.emptyList());
        }
        return Result.ok(dict.getItems());
    }

}
