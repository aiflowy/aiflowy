<script setup lang="ts">
import { onMounted, ref } from 'vue';

import { createIconifyIcon } from '@aiflowy/icons';

import { Delete, Edit, Plus } from '@element-plus/icons-vue';
import {
  ElButton,
  ElIcon,
  ElMessage,
  ElMessageBox,
  ElTable,
  ElTableColumn,
} from 'element-plus';

import { api } from '#/api/request';
import { $t } from '#/locales';
import { useDictStore } from '#/store';

import SysMenuModal from './SysMenuModal.vue';

onMounted(() => {
  getTree();
  initDict();
});

const saveDialog = ref();
const treeData = ref([]);
const loading = ref(false);
const dictStore = useDictStore();
function initDict() {
  dictStore.fetchDictionary('menuType');
  dictStore.fetchDictionary('yesOrNo');
}
function reset() {
  getTree();
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
          .post('/api/v1/sysMenu/remove', { id: row.id })
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
function getTree() {
  loading.value = true;
  api
    .get('/api/v1/sysMenu/list', {
      params: {
        asTree: true,
      },
    })
    .then((res) => {
      loading.value = false;
      treeData.value = res.data;
    });
}
</script>

<template>
  <div class="page-container">
    <SysMenuModal ref="saveDialog" @reload="reset" />
    <div class="handle-div">
      <ElButton
        v-access:code="'/api/v1/sysMenu/save'"
        @click="showDialog({})"
        type="primary"
      >
        <ElIcon class="mr-1">
          <Plus />
        </ElIcon>
        {{ $t('button.add') }}
      </ElButton>
    </div>
    <ElTable :data="treeData" border row-key="id" v-loading="loading">
      <ElTableColumn width="50" />
      <ElTableColumn prop="menuType" :label="$t('sysMenu.menuType')">
        <template #default="{ row }">
          {{ dictStore.getDictLabel('menuType', row.menuType) }}
        </template>
      </ElTableColumn>
      <ElTableColumn prop="menuTitle" :label="$t('sysMenu.menuTitle')">
        <template #default="{ row }">
          {{ $t(row.menuTitle) }}
        </template>
      </ElTableColumn>
      <ElTableColumn prop="menuUrl" :label="$t('sysMenu.menuUrl')">
        <template #default="{ row }">
          {{ row.menuUrl }}
        </template>
      </ElTableColumn>
      <ElTableColumn prop="component" :label="$t('sysMenu.component')">
        <template #default="{ row }">
          {{ row.component }}
        </template>
      </ElTableColumn>
      <ElTableColumn prop="menuIcon" :label="$t('sysMenu.menuIcon')">
        <template #default="{ row }">
          <component class="size-5" :is="createIconifyIcon(row.menuIcon)" />
        </template>
      </ElTableColumn>
      <ElTableColumn prop="isShow" :label="$t('sysMenu.isShow')">
        <template #default="{ row }">
          {{ dictStore.getDictLabel('yesOrNo', row.isShow) }}
        </template>
      </ElTableColumn>
      <ElTableColumn prop="permissionTag" :label="$t('sysMenu.permissionTag')">
        <template #default="{ row }">
          {{ row.permissionTag }}
        </template>
      </ElTableColumn>
      <ElTableColumn prop="sortNo" :label="$t('sysMenu.sortNo')">
        <template #default="{ row }">
          {{ row.sortNo }}
        </template>
      </ElTableColumn>
      <ElTableColumn prop="created" :label="$t('sysMenu.created')">
        <template #default="{ row }">
          {{ row.created }}
        </template>
      </ElTableColumn>
      <ElTableColumn :label="$t('common.handle')" width="150">
        <template #default="{ row }">
          <div>
            <ElButton
              v-access:code="'/api/v1/sysMenu/save'"
              @click="showDialog(row)"
              link
              type="primary"
            >
              <ElIcon class="mr-1">
                <Edit />
              </ElIcon>
              {{ $t('button.edit') }}
            </ElButton>
            <ElButton
              v-access:code="'/api/v1/sysMenu/remove'"
              @click="remove(row)"
              link
              type="danger"
            >
              <ElIcon class="mr-1">
                <Delete />
              </ElIcon>
              {{ $t('button.delete') }}
            </ElButton>
          </div>
        </template>
      </ElTableColumn>
    </ElTable>
  </div>
</template>

<style scoped></style>
