<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { markRaw, onMounted, ref } from 'vue';

import { Delete, Edit, More, Plus } from '@element-plus/icons-vue';
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
import { useDictStore } from '#/store';

import SysRoleModal from './SysRoleModal.vue';

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
    permission: '/api/v1/sysRole/save',
  },
];

function initDict() {
  dictStore.fetchDictionary('dataStatus');
}
function isAdminRole(data: any) {
  return data?.roleKey === 'super_admin';
}
const handleSearch = (params: string) => {
  pageDataRef.value.setQuery({ roleName: params, isQueryOr: true });
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
          .post('/api/v1/sysRole/remove', { id: row.id })
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
    <SysRoleModal ref="saveDialog" @reload="reset" />
    <HeaderSearch
      :buttons="headerButtons"
      @search="handleSearch"
      @button-click="showDialog({})"
    />

    <div class="bg-background flex-1 rounded-lg p-5">
      <PageData
        ref="pageDataRef"
        page-url="/api/v1/sysRole/page"
        :page-size="10"
      >
        <template #default="{ pageList }">
          <ElTable :data="pageList" border>
            <ElTableColumn prop="roleName" :label="$t('sysRole.roleName')">
              <template #default="{ row }">
                {{ row.roleName }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="roleKey" :label="$t('sysRole.roleKey')">
              <template #default="{ row }">
                {{ row.roleKey }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="status" :label="$t('sysRole.status')">
              <template #default="{ row }">
                {{ dictStore.getDictLabel('dataStatus', row.status) }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="created" :label="$t('sysRole.created')">
              <template #default="{ row }">
                {{ row.created }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="remark" :label="$t('sysRole.remark')">
              <template #default="{ row }">
                {{ row.remark }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              :label="$t('common.handle')"
              width="80"
              align="center"
            >
              <template #default="{ row }">
                <ElDropdown v-if="!isAdminRole(row)">
                  <ElButton link>
                    <ElIcon>
                      <More />
                    </ElIcon>
                  </ElButton>

                  <template #dropdown>
                    <ElDropdownMenu>
                      <div v-access:code="'/api/v1/sysRole/save'">
                        <ElDropdownItem @click="showDialog(row)">
                          <ElButton :icon="Edit" link>
                            {{ $t('button.edit') }}
                          </ElButton>
                        </ElDropdownItem>
                      </div>
                      <div v-access:code="'/api/v1/sysRole/remove'">
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
