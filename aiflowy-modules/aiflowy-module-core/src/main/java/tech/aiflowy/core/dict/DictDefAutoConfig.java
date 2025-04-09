package tech.aiflowy.core.dict;

import tech.aiflowy.common.util.SpringContextUtil;
import tech.aiflowy.common.annotation.DictDef;
import tech.aiflowy.core.dict.loader.EnumDictLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.event.EventListener;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

@Component
public class DictDefAutoConfig {

    private static final Logger LOG = LoggerFactory.getLogger(DictDefAutoConfig.class);

    @EventListener(ApplicationReadyEvent.class)
    public <E extends Enum<E>> void onApplicationStartup() {

        DictManager dictManager = SpringContextUtil.getBean(DictManager.class);

        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);

        scanner.addIncludeFilter(new AnnotationTypeFilter(DictDef.class));

        for (BeanDefinition bd : scanner.findCandidateComponents("tech.aiflowy")) {
            try {
                @SuppressWarnings("unchecked")
                Class<E> enumClass = (Class<E>) Class.forName(bd.getBeanClassName());
                DictDef dictDef = enumClass.getAnnotation(DictDef.class);
                dictManager.putLoader(new EnumDictLoader<>(dictDef.code(), enumClass, dictDef.keyField(), dictDef.labelField()));
            } catch (ClassNotFoundException e) {
                LOG.warn("Could not resolve class object for bean definition", e);
            }
        }
    }

}
