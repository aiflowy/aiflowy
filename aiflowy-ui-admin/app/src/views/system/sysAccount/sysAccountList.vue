<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { ref } from 'vue';

import {
  ElButton,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
  ElMessageBox,
  ElTable,
  ElTableColumn,
} from 'element-plus';

import { api } from '#/api/request';
import DictSelect from '#/components/dict/DictSelect.vue';
import PageData from '#/components/page/PageData.vue';
import { $t } from '#/locales';

import SysAccountModal from './sysAccountModal.vue';

const formRef = ref<FormInstance>();
const pageDataRef = ref();
const saveDialog = ref();
const formInline = ref({
  loginName: '',
  accountType: '',
});
function search(formEl: FormInstance | undefined) {
  formEl?.validate((valid) => {
    if (valid) {
      pageDataRef.value.setQuery(formInline.value);
    }
  });
}
function reset(formEl: FormInstance | undefined) {
  formEl?.resetFields();
  pageDataRef.value.setQuery({});
}
function showDialog(row: any) {
  saveDialog.value.openDialog({ ...row });
}
function remove(row: any) {
  ElMessageBox.confirm('确定删除吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        instance.confirmButtonLoading = true;
        api
          .post('/api/v1/sysAccount/remove', { id: row.id })
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
</script>

<template>
  <div class="page-container">
    <SysAccountModal ref="saveDialog" @reload="reset" />
    <ElForm ref="formRef" :inline="true" :model="formInline">
      <ElFormItem label="用户类型" prop="accountType">
        <DictSelect
          style="width: 200px"
          v-model="formInline.accountType"
          dict-code="accountType"
        />
      </ElFormItem>
      <ElFormItem label="账号" prop="loginName">
        <ElInput v-model="formInline.loginName" placeholder="请输入账号" />
      </ElFormItem>
      <ElFormItem>
        <ElButton @click="search(formRef)" type="primary">
          {{ $t('button.query') }}
        </ElButton>
        <ElButton @click="reset(formRef)">
          {{ $t('button.reset') }}
        </ElButton>
      </ElFormItem>
    </ElForm>
    <div class="handle-div">
      <ElButton @click="showDialog({})" type="primary">
        {{ $t('button.add') }}
      </ElButton>
    </div>
    <PageData
      ref="pageDataRef"
      page-url="/api/v1/sysAccount/page"
      :page-size="10"
      :init-query-params="{ status: 1 }"
    >
      <template #default="{ pageList }">
        <ElTable :data="pageList" border>
          <ElTableColumn prop="loginName" label="账号" width="180" />
          <ElTableColumn prop="nickname" label="昵称" width="180" />
          <ElTableColumn prop="avatar" label="头像" />
          <ElTableColumn>
            <template #default="{ row }">
              <ElButton @click="showDialog(row)" type="primary">
                {{ $t('button.edit') }}
              </ElButton>
              <ElButton @click="remove(row)" type="danger">
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
