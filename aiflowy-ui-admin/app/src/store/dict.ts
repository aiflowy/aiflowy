import { defineStore } from 'pinia';

import { api } from '#/api/request';

export const useDictStore = defineStore('dictionary', {
  state: () => ({
    dictCache: new Map(), // 缓存字典数据
  }),

  getters: {
    // 获取特定字典的 Map 对象
    getDictByType: (state) => (dictType: string) => {
      return state.dictCache.get(dictType) || new Map();
    },
  },

  actions: {
    // 获取字典数据
    async fetchDictionary(dictType: string) {
      // 如果已经有缓存数据，直接返回
      if (this.dictCache.has(dictType)) {
        return this.dictCache.get(dictType);
      }

      try {
        const requestPromise = api.get(`/api/v1/dict/items/${dictType}`);
        const dictData = await requestPromise;
        // 转换为 { value: label } 格式便于查找
        const dictMap = new Map(
          dictData.data.map((item: any) => [item.value, item.label]),
        );

        // 缓存数据并清理加载状态
        this.dictCache.set(dictType, dictMap);

        return dictMap;
      } catch (error) {
        console.error(`get dict ${dictType} error:`, error);
        return new Map();
      }
    },

    // 根据字典类型和值获取标签
    getDictLabel(dictType: string, value: any) {
      const dictMap = this.dictCache.get(dictType);
      if (!dictMap) {
        return value; // 返回原值作为降级处理
      }

      const label = dictMap.get(value);
      return label === undefined ? value : label;
    },
  },
});
