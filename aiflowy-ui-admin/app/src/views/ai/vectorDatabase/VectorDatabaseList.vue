<script setup lang="ts">
import {ElDropdown, ElDropdownItem, ElDropdownMenu, type FormInstance} from 'element-plus';

import { markRaw, ref } from 'vue';

import {Delete, Edit, MoreFilled, Plus} from '@element-plus/icons-vue';
import {
  ElButton,
  ElMessage,
  ElMessageBox,
  ElTable,
  ElTableColumn,
} from 'element-plus';

import { api } from '#/api/request';
import HeaderSearch from '#/components/headerSearch/HeaderSearch.vue';
import PageData from '#/components/page/PageData.vue';
import { $t } from '#/locales';

import VectorDatabaseModal from './VectorDatabaseModal.vue';

const formRef = ref<FormInstance>();
const pageDataRef = ref();
const saveDialog = ref();

function reset(formEl: FormInstance | undefined) {
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
          .post('/api/v1/vectorDatabase/remove', { id: row.id })
          .then((res) => {
            instance.confirmButtonLoading = false;
            if (res.errorCode === 0) {
              ElMessage.success(res.message);
              reset(formRef.value);
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
const headerButtons = [
  {
    key: 'create',
    text: $t('button.add'),
    icon: markRaw(Plus),
    type: 'primary',
    data: { action: 'create' },
    permission: '/api/v1/vectorDatabase/save',
  },
];
const handleSearch = (params: string) => {
  pageDataRef.value.setQuery({ title: params, isQueryOr: true });
};
</script>

<template>
  <div class="flex h-full flex-col gap-6 p-6">
    <VectorDatabaseModal ref="saveDialog" @reload="reset" />
    <HeaderSearch
      :buttons="headerButtons"
      @search="handleSearch"
      @button-click="showDialog({})"
    />
    <div class="bg-background border-border flex-1 rounded-lg border p-5">
      <PageData
        ref="pageDataRef"
        page-url="/api/v1/vectorDatabase/page"
        :page-size="10"
      >
        <template #default="{ pageList }">
          <ElTable :data="pageList" border>
            <ElTableColumn prop="title" :label="$t('vectorDatabase.title')">
              <template #default="{ row }">
                {{ row.title }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="type" :label="$t('vectorDatabase.type')">
              <template #default="{ row }">
                {{ row.type }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="created" :label="$t('vectorDatabase.created')">
              <template #default="{ row }">
                {{ row.created }}
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
                        <div v-access:code="'/api/v1/vectorDatabase/remove'">
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
