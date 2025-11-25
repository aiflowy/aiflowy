<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue';

import { ElEmpty, ElPagination } from 'element-plus';

import { api } from '#/api/request';

interface PageDataProps {
  pageUrl: string;
  pageSize?: number;
  pageSizes?: number[];
  extraQueryParams?: Record<string, any>;
}

const props = withDefaults(defineProps<PageDataProps>(), {
  pageSize: 10,
  pageSizes: () => [10, 20, 50, 100],
  extraQueryParams: () => ({}),
});

// 响应式数据
const pageList = ref([]);
const loading = ref(false);
const queryParams = ref({});

const pageInfo = reactive({
  pageNumber: 1,
  pageSize: props.pageSize,
  total: 0,
});

// 模拟 API 调用 - 这里需要根据你的实际 API 调用方式调整
const doGet = async (params: any) => {
  loading.value = true;
  try {
    // 这里替换为你的实际 API 调用
    // 例如：return await api.get(props.pageUrl, { params })
    const response = await api.get(`${props.pageUrl}`, {
      params,
    });
    const data = await response.data;
    return { data };
  } finally {
    loading.value = false;
  }
};

// 获取页面数据
const getPageList = async () => {
  try {
    const res = await doGet({
      pageNumber: pageInfo.pageNumber,
      pageSize: pageInfo.pageSize,
      ...queryParams.value,
      ...props.extraQueryParams,
    });
    pageList.value = res.data?.records || [];
    pageInfo.total = res.data?.totalRow || 0;
  } catch (error) {
    console.error('get data error:', error);
    pageList.value = [];
    pageInfo.total = 0;
  }
};

// 分页事件处理
const handleSizeChange = (newSize: number) => {
  pageInfo.pageSize = newSize;
  pageInfo.pageNumber = 1; // 重置到第一页
};

const handleCurrentChange = (newPage: number) => {
  pageInfo.pageNumber = newPage;
};

// 暴露给父组件的方法 (替代 useImperativeHandle)
const setQuery = (newQueryParams: string) => {
  pageInfo.pageNumber = 1;
  pageInfo.pageSize = props.pageSize;
  queryParams.value = newQueryParams;
  getPageList();
};

// 暴露方法给父组件
defineExpose({
  setQuery,
});

// 监听器
watch(
  [() => pageInfo.pageNumber, () => pageInfo.pageSize],
  () => {
    getPageList();
  },
  { deep: true },
);

// 生命周期
onMounted(() => {
  getPageList();
});
</script>

<template>
  <div class="page-data-container" v-loading="loading">
    <div v-if="pageList.length > 0">
      <slot :page-list="pageList"></slot>
    </div>
    <ElEmpty v-else />
    <div class="pagination-container">
      <ElPagination
        v-model:current-page="pageInfo.pageNumber"
        v-model:page-size="pageInfo.pageSize"
        :total="pageInfo.total"
        :page-sizes="pageSizes"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<style scoped>
.pagination-container {
  margin-top: 10px;
}
</style>
