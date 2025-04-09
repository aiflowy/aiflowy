package tech.aiflowy.system.options;

import tech.aiflowy.common.options.SysOptionStore;
import tech.aiflowy.common.util.StringUtil;
import tech.aiflowy.system.entity.SysOption;
import tech.aiflowy.system.service.SysOptionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class DefaultOptionStore implements SysOptionStore {

    @Resource
    private SysOptionService optionService;


    @Override
    public void save(String key, Object value) {
        if (value == null || !StringUtil.hasText(value.toString())) {
            optionService.removeById(key);
            return;
        }

        String newValue = value.toString().trim();
        SysOption option = optionService.getById(key);
        if (option == null) {
            option = new SysOption(key, newValue);
            optionService.save(option);
        } else {
            option.setValue(newValue);
            optionService.updateById(option);
        }
    }

    @Override
    public String get(String key) {
        SysOption option = optionService.getById(key);
        return option != null ? option.getValue() : null;
    }
}
