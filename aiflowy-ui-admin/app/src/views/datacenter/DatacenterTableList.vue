<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { markRaw, onMounted, ref } from 'vue';

import { Delete, Edit, More, Plus, View } from '@element-plus/icons-vue';
import {
  ElButton,
  ElDropdown,
  ElDropdownItem,
  ElDropdownMenu,
  ElIcon,
  ElMessage,
  ElMessageBox,
  ElTable,
  ElTableColumn,
} from 'element-plus';

import { api } from '#/api/request';
import HeaderSearch from '#/components/headerSearch/HeaderSearch.vue';
import PageData from '#/components/page/PageData.vue';
import { $t } from '#/locales';
import { router } from '#/router';
import { useDictStore } from '#/store';

import DatacenterTableModal from './DatacenterTableModal.vue';

onMounted(() => {
  initDict();
});

const pageDataRef = ref();
const saveDialog = ref();
const dictStore = useDictStore();
const headerButtons = [
  {
    key: 'create',
    text: $t('button.add'),
    icon: markRaw(Plus),
    type: 'primary',
    data: { action: 'create' },
    permission: '/api/v1/datacenterTable/save',
  },
];

function initDict() {
  dictStore.fetchDictionary('dataStatus');
}
const handleSearch = (params: string) => {
  pageDataRef.value.setQuery({ tableName: params, isQueryOr: true });
};
function reset(formEl?: FormInstance) {
  formEl?.resetFields();
  pageDataRef.value.setQuery({});
}
function showDialog(row: any) {
  saveDialog.value.openDialog({ ...row });
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
          .get(`/api/v1/datacenterTable/removeTable?tableId=${row.id}`)
          .then((res) => {
            instance.confirmButtonLoading = false;
            if (res.errorCode === 0) {
              ElMessage.success(res.message);
              reset();
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
function toDetailPage(row: any) {
  router.push({
    name: 'TableDetail',
    query: {
      tableId: row.id,
    },
  });
}
</script>

<template>
  <div class="flex h-full flex-col gap-6 p-6">
    <DatacenterTableModal ref="saveDialog" @reload="reset" />
    <HeaderSearch
      :buttons="headerButtons"
      @search="handleSearch"
      @button-click="showDialog({})"
    />

    <div class="bg-background flex-1 rounded-lg p-5">
      <PageData
        ref="pageDataRef"
        page-url="/api/v1/datacenterTable/page"
        :page-size="10"
      >
        <template #default="{ pageList }">
          <ElTable :data="pageList" border>
            <ElTableColumn
              prop="tableName"
              :label="$t('datacenterTable.tableName')"
            >
              <template #default="{ row }">
                {{ row.tableName }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              prop="tableDesc"
              :label="$t('datacenterTable.tableDesc')"
            >
              <template #default="{ row }">
                {{ row.tableDesc }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              prop="created"
              :label="$t('datacenterTable.created')"
            >
              <template #default="{ row }">
                {{ row.created }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              :label="$t('common.handle')"
              width="80"
              align="center"
            >
              <template #default="{ row }">
                <ElDropdown>
                  <ElButton link>
                    <ElIcon>
                      <More />
                    </ElIcon>
                  </ElButton>

                  <template #dropdown>
                    <ElDropdownMenu>
                      <div v-access:code="'/api/v1/datacenterTable/query'">
                        <ElDropdownItem @click="toDetailPage(row)">
                          <ElButton :icon="View" link>
                            {{ $t('button.view') }}
                          </ElButton>
                        </ElDropdownItem>
                      </div>
                      <div v-access:code="'/api/v1/datacenterTable/save'">
                        <ElDropdownItem @click="showDialog(row)">
                          <ElButton :icon="Edit" link>
                            {{ $t('button.edit') }}
                          </ElButton>
                        </ElDropdownItem>
                      </div>
                      <div v-access:code="'/api/v1/datacenterTable/remove'">
                        <ElDropdownItem @click="remove(row)">
                          <ElButton type="danger" :icon="Delete" link>
                            {{ $t('button.delete') }}
                          </ElButton>
                        </ElDropdownItem>
                      </div>
                    </ElDropdownMenu>
                  </template>
                </ElDropdown>
              </template>
            </ElTableColumn>
          </ElTable>
        </template>
      </PageData>
    </div>
  </div>
</template>

<style scoped></style>
