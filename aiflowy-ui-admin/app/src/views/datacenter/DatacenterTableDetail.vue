<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

import { $t } from '@aiflowy/locales';

import {
  ArrowLeft,
  Delete,
  Edit,
  Plus,
  Refresh,
  Upload,
} from '@element-plus/icons-vue';
import {
  ElButton,
  ElCol,
  ElIcon,
  ElMenu,
  ElMenuItem,
  ElMessage,
  ElMessageBox,
  ElRow,
  ElTable,
  ElTableColumn,
} from 'element-plus';

import { api } from '#/api/request';
import tableIcon from '#/assets/datacenter/table2x.png';
import PageData from '#/components/page/PageData.vue';
import { router } from '#/router';
import BatchImportModal from '#/views/datacenter/BatchImportModal.vue';
import RecordModal from '#/views/datacenter/RecordModal.vue';

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
const recordModal = ref();
const batchImportModal = ref();
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
  recordModal.value.openDialog({ ...row });
}
function remove(row: any) {
  ElMessageBox.confirm($t('message.deleteAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('message.ok'),
    cancelButtonText: $t('message.cancel'),
    type: 'warning',
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        instance.confirmButtonLoading = true;
        api
          .post(
            `/api/v1/datacenterTable/removeValue`,
            {},
            {
              params: {
                tableId: tableId.value,
                id: row.id,
              },
            },
          )
          .then((res) => {
            instance.confirmButtonLoading = false;
            if (res.errorCode === 0) {
              ElMessage.success(res.message);
              refresh();
              done();
            }
          })
          .catch(() => {
            instance.confirmButtonLoading = false;
          });
      } else {
        done();
      }
    },
  }).catch(() => {});
}
function refresh() {
  pageDataRef.value.setQuery({});
}
function openImportModal() {
  batchImportModal.value.openDialog();
}
</script>

<template>
  <div class="page-container">
    <BatchImportModal
      :table-id="tableId"
      :title="$t('button.batchImport')"
      ref="batchImportModal"
      @reload="refresh"
    />
    <RecordModal
      ref="recordModal"
      :form-items="headers"
      :table-id="tableId"
      @reload="refresh"
    />
    <ElRow>
      <ElCol :span="24" class="border-b">
        <div class="mb-2.5 flex items-center justify-between">
          <div class="flex items-center gap-2.5">
            <ElIcon class="cursor-pointer" @click="router.back()">
              <ArrowLeft />
            </ElIcon>
            <img :src="tableIcon" class="h-10 w-10" />
            <div class="flex flex-col justify-center">
              <div class="text-sm font-bold">{{ detailInfo.tableName }}</div>
              <div class="desc text-xs">{{ detailInfo.tableDesc }}</div>
            </div>
          </div>
          <div class="flex items-center" v-if="activeMenu === '2'">
            <ElButton type="primary" @click="showDialog({})">
              <ElIcon class="mr-1">
                <Plus />
              </ElIcon>
              {{ $t('button.addLine') }}
            </ElButton>
            <ElButton type="primary" @click="openImportModal">
              <ElIcon class="mr-1">
                <Upload />
              </ElIcon>
              {{ $t('button.batchImport') }}
            </ElButton>
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
            <ElButton link @click="refresh" class="float-right">
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
                  <div v-if="item.fieldType === 5">
                    {{ 1 === row[item.key] ? '是' : '否' }}
                  </div>
                  <div v-else-if="item.fieldType === 4">
                    {{ parseFloat(row[item.key]) }}
                  </div>
                  <div v-else>{{ row[item.key] }}</div>
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
