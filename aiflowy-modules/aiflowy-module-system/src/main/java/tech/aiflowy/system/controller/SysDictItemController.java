package tech.aiflowy.system.controller;

import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.system.entity.SysDictItem;
import tech.aiflowy.system.service.SysDictItemService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据字典内容 控制层。
 *
 * @author michael
 * @since 2024-03-06
 */
@RestController
@RequestMapping("/api/v1/sysDictItem")
public class SysDictItemController extends BaseCurdController<SysDictItemService, SysDictItem> {
    public SysDictItemController(SysDictItemService service) {
        super(service);
    }
}