<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

import { $t } from '@aiflowy/locales';

import { Delete, Edit, Refresh } from '@element-plus/icons-vue';
import { ElButton, ElIcon, ElTable, ElTableColumn } from 'element-plus';

import { api } from '#/api/request';
import PageData from '#/components/page/PageData.vue';

const pageDataRef = ref();
const route = useRoute();
const tableId = ref(route.query.tableId);
onMounted(() => {
  getDetailInfo(tableId.value);
  getHeaders(tableId.value);
});
const detailInfo = ref({});
const headers = ref<any[]>([]);
function getDetailInfo(id: any) {
  api.get(`/api/v1/datacenterTable/detailInfo?tableId=${id}`).then((res) => {
    detailInfo.value = res.data;
  });
}
function getHeaders(id: any) {
  api.get(`/api/v1/datacenterTable/getHeaders?tableId=${id}`).then((res) => {
    headers.value = res.data;
  });
}
function showDialog(row: any) {
  console.log(row);
}
function remove(row: any) {
  console.log(row);
}
function refresh() {
  pageDataRef.value.setQuery({});
}
</script>

<template>
  <div class="page-container">
    <PageData
      ref="pageDataRef"
      page-url="/api/v1/datacenterTable/getPageData"
      :extra-query-params="{ tableId }"
      :page-size="10"
    >
      <template #default="{ pageList }">
        <ElButton @click="refresh" class="float-right">
          <ElIcon>
            <Refresh />
          </ElIcon>
        </ElButton>
        <ElTable :data="pageList">
          <ElTableColumn
            v-for="item in headers"
            :key="item.key"
            :prop="item.key"
            :label="item.title"
          >
            <template #default="{ row }">
              {{ row[item.key] }}
            </template>
          </ElTableColumn>
          <ElTableColumn :label="$t('common.handle')" width="150">
            <template #default="{ row }">
              <ElButton @click="showDialog(row)" link type="primary">
                <ElIcon class="mr-1">
                  <Edit />
                </ElIcon>
                {{ $t('button.edit') }}
              </ElButton>
              <ElButton @click="remove(row)" link type="danger">
                <ElIcon class="mr-1">
                  <Delete />
                </ElIcon>
                {{ $t('button.delete') }}
              </ElButton>
            </template>
          </ElTableColumn>
        </ElTable>
      </template>
    </PageData>
  </div>
</template>

<style scoped></style>
