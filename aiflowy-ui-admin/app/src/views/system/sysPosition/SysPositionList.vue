<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { markRaw, onMounted, ref } from 'vue';

import { Delete, MoreFilled, Plus } from '@element-plus/icons-vue';
import {
  ElButton,
  ElDropdown,
  ElDropdownItem,
  ElDropdownMenu,
  ElMessage,
  ElMessageBox,
  ElTable,
  ElTableColumn,
} from 'element-plus';

import { api } from '#/api/request';
import HeaderSearch from '#/components/headerSearch/HeaderSearch.vue';
import PageData from '#/components/page/PageData.vue';
import { $t } from '#/locales';
import { useDictStore } from '#/store';

import SysPositionModal from './SysPositionModal.vue';

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
    permission: '/api/v1/sysPosition/save',
  },
];

function initDict() {
  dictStore.fetchDictionary('dataStatus');
}

const handleSearch = (params: string) => {
  // 后端支持 positionName 模糊查询
  pageDataRef.value.setQuery({ positionName: params });
};

function reset(formEl?: FormInstance) {
  formEl?.resetFields();
  pageDataRef.value.setQuery({});
}

function showDialog(row: any) {
  saveDialog.value.openDialog({ ...row });
}

function changeStatus(row: any) {
  const newStatus = row.status === 1 ? 0 : 1;
  const actionText = newStatus === 1 ? '启用' : '禁用';

  ElMessageBox.confirm(`确认要${actionText}"${row.positionName}"吗？`, $t('message.noticeTitle'), {
    confirmButtonText: $t('message.ok'),
    cancelButtonText: $t('message.cancel'),
    type: 'warning',
  }).then(() => {
    api.post('/api/v1/sysPosition/changeStatus', { id: row.id, status: newStatus })
      .then((res) => {
        if (res.errorCode === 0) {
          ElMessage.success('操作成功');
          pageDataRef.value.reload();
        }
      });
  }).catch(() => {});
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
          .post('/api/v1/sysPosition/remove', { id: row.id })
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
</script>

<template>
  <div class="flex h-full flex-col gap-6 p-6">
    <SysPositionModal ref="saveDialog" @reload="reset" />
    <HeaderSearch
      :buttons="headerButtons"
      @search="handleSearch"
      @button-click="showDialog({})"
    />

    <div class="bg-background border-border flex-1 rounded-lg border p-5">
      <PageData
        ref="pageDataRef"
        page-url="/api/v1/sysPosition/page"
        :page-size="10"
      >
        <template #default="{ pageList }">
          <ElTable :data="pageList" border>
            <ElTableColumn prop="positionName" :label="$t('sysPosition.positionName') || '岗位名称'">
              <template #default="{ row }">
                {{ row.positionName }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="positionCode" :label="$t('sysPosition.positionCode') || '岗位编码'">
              <template #default="{ row }">
                {{ row.positionCode }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="sortNo" :label="$t('sysPosition.sortNo') || '排序'" width="80" align="center">
              <template #default="{ row }">
                {{ row.sortNo }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="status" :label="$t('sysPosition.status') || '状态'" width="100" align="center">
              <template #default="{ row }">
                 <ElButton
                    :type="row.status === 1 ? 'success' : 'info'"
                    link
                    @click="changeStatus(row)"
                 >
                    {{ dictStore.getDictLabel('dataStatus', row.status) || (row.status === 1 ? '启用' : '禁用') }}
                 </ElButton>
              </template>
            </ElTableColumn>
            <ElTableColumn prop="created" :label="$t('sysPosition.created')" width="180">
              <template #default="{ row }">
                {{ row.created }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="remark" :label="$t('sysPosition.remark')">
              <template #default="{ row }">
                {{ row.remark }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              :label="$t('common.handle')"
              width="90"
              align="right"
            >
              <template #default="{ row }">
                <div class="flex items-center gap-3">
                  <ElButton link type="primary" @click="showDialog(row)">
                    {{ $t('button.edit') }}
                  </ElButton>

                  <ElDropdown>
                    <ElButton link :icon="MoreFilled" />

                    <template #dropdown>
                      <ElDropdownMenu>
                        <div v-access:code="'/api/v1/sysPosition/remove'">
                          <ElDropdownItem @click="remove(row)">
                            <ElButton type="danger" :icon="Delete" link>
                              {{ $t('button.delete') }}
                            </ElButton>
                          </ElDropdownItem>
                        </div>
                      </ElDropdownMenu>
                    </template>
                  </ElDropdown>
                </div>
              </template>
            </ElTableColumn>
          </ElTable>
        </template>
      </PageData>
    </div>
  </div>
</template>

<style scoped></style>
