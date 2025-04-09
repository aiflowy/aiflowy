package tech.aiflowy.system.controller;

import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.util.SpringContextUtil;

import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.core.dict.DictManager;
import tech.aiflowy.system.entity.SysDict;
import tech.aiflowy.system.service.SysDictService;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 系统配置表 控制层。
 *
 * @author michael
 * @since 2024-03-05
 */
@RestController
@RequestMapping("/api/v1/sysDict")
public class SysDictController extends BaseCurdController<SysDictService, SysDict> {

    public SysDictController(SysDictService service) {
        super(service);
    }

    @Override
    protected void onSaveOrUpdateAfter(SysDict entity, boolean isSave) {
        DictManager dictManager = SpringContextUtil.getBean(DictManager.class);
        dictManager.putLoader(entity.buildLoader());
    }

    @Override
    protected Result onRemoveBefore(Collection<Serializable> ids) {
        List<SysDict> sysDicts = service.list(QueryWrapper.create().in("id", ids));
        if (sysDicts != null) {
            DictManager dictManager = SpringContextUtil.getBean(DictManager.class);
            sysDicts.forEach(sysDict -> dictManager.removeLoader(sysDict.getCode()));
        }
        return null;
    }
}