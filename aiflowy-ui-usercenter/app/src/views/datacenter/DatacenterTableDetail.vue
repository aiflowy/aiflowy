<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

import { $t } from '@aiflowy/locales';

import { ArrowLeft, Delete, Edit, Refresh } from '@element-plus/icons-vue';
import {
  ElButton,
  ElCol,
  ElIcon,
  ElMenu,
  ElMenuItem,
  ElRow,
  ElTable,
  ElTableColumn,
} from 'element-plus';

import { api } from '#/api/request';
import tableIcon from '#/assets/datacenter/table2x.png';
import PageData from '#/components/page/PageData.vue';
import { router } from '#/router';

const pageDataRef = ref();
const route = useRoute();
const tableId = ref(route.query.tableId);
onMounted(() => {
  getDetailInfo(tableId.value);
  getHeaders(tableId.value);
});
const detailInfo = ref<any>({});
const fieldList = ref<any[]>([]);
const headers = ref<any[]>([]);
const activeMenu = ref('1');
function getDetailInfo(id: any) {
  api.get(`/api/v1/datacenterTable/detailInfo?tableId=${id}`).then((res) => {
    detailInfo.value = res.data;
    fieldList.value = res.data.fields;
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
    <ElRow>
      <ElCol :span="24" class="border-b">
        <div class="mb-2.5 flex items-center gap-2">
          <ElIcon class="cursor-pointer" @click="router.back()">
            <ArrowLeft />
          </ElIcon>
          <img :src="tableIcon" class="h-10 w-10" />
          <div class="flex flex-col justify-center">
            <div class="text-sm font-bold">{{ detailInfo.tableName }}</div>
            <div class="desc text-xs">{{ detailInfo.tableDesc }}</div>
          </div>
        </div>
      </ElCol>
      <ElCol :span="4" class="border-r">
        <ElMenu default-active="1" @select="(index) => (activeMenu = index)">
          <ElMenuItem index="1">
            {{ $t('datacenterTable.structure') }}
          </ElMenuItem>
          <ElMenuItem index="2">
            {{ $t('datacenterTable.data') }}
          </ElMenuItem>
        </ElMenu>
      </ElCol>
      <ElCol :span="20" class="p-2.5">
        <ElTable v-show="activeMenu === '1'" :data="fieldList">
          <ElTableColumn
            prop="fieldName"
            :label="$t('datacenterTableFields.fieldName')"
          />
          <ElTableColumn
            prop="fieldDesc"
            :label="$t('datacenterTableFields.fieldDesc')"
          />
          <ElTableColumn
            prop="fieldType"
            :label="$t('datacenterTableFields.fieldType')"
          >
            <template #default="{ row }">
              {{ row.fieldType }}
            </template>
          </ElTableColumn>
          <ElTableColumn
            prop="required"
            :label="$t('datacenterTableFields.required')"
          >
            <template #default="{ row }">
              {{ row.required }}
            </template>
          </ElTableColumn>
        </ElTable>
        <PageData
          v-show="activeMenu === '2'"
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
      </ElCol>
    </ElRow>
  </div>
</template>

<style scoped>
.desc {
  color: #969799;
}
</style>
