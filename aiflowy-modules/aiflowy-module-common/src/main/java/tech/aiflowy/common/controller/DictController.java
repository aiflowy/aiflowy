package tech.aiflowy.common.controller;

import tech.aiflowy.common.domain.Result;
import tech.aiflowy.core.dict.Dict;
import tech.aiflowy.core.dict.DictLoader;
import tech.aiflowy.core.dict.DictManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dict/")
public class DictController {

    @Resource
    DictManager dictManager;

    @GetMapping("/items/{code}")
    public Result items(@PathVariable("code") String code, String keyword, HttpServletRequest request) {
        DictLoader loader = dictManager.getLoader(code);
        if (loader == null) {
            return Result.success(Collections.emptyList());
        }
        Map<String, String[]> parameterMap = request.getParameterMap();
        Dict dict = loader.load(keyword, parameterMap);
        if (dict == null) {
            return Result.success(Collections.emptyList());
        }
        return Result.success(dict.getItems());
    }

}
