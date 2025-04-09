package tech.aiflowy.core.dict;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DictManager implements BeanPostProcessor {

    private Map<String, DictLoader> loaders = new HashMap<>();

    public DictManager(ObjectProvider<List<DictLoader>> listObjectProvider) {
        List<DictLoader> dictLoaders = listObjectProvider.getIfAvailable();
        if (dictLoaders != null) {
            dictLoaders.forEach(dictLoader -> loaders.put(dictLoader.code(), dictLoader));
        }
    }

    public Map<String, DictLoader> getLoaders() {
        return loaders;
    }

    public void setLoaders(Map<String, DictLoader> loaders) {
        this.loaders = loaders;
    }

    public void putLoader(DictLoader loader) {
        if (loader == null){
            return;
        }
        loaders.put(loader.code(), loader);
    }

    public void removeLoader(String code) {
        loaders.remove(code);
    }

    public DictLoader getLoader(String code) {
        if (loaders == null || loaders.isEmpty()) {
            return null;
        }
        return loaders.get(code);
    }
}
