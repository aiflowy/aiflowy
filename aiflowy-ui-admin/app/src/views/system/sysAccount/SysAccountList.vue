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
import PageData from '#/components/page/PageData.vue';
import { $t } from '#/locales';

import SysAccountModal from './SysAccountModal.vue';

const formRef = ref<FormInstance>();
const pageDataRef = ref();
const saveDialog = ref();
const formInline = ref({
  id: '',
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
      <ElFormItem label="查询字段" prop="id">
        <ElInput v-model="formInline.id" placeholder="请输入查询字段" />
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
    >
      <template #default="{ pageList }">
        <ElTable :data="pageList" border>
          <ElTableColumn prop="deptId" label="部门ID">
            <template #default="{ row }">
              {{ row.deptId }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="loginName" label="登录账号">
            <template #default="{ row }">
              {{ row.loginName }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="password" label="密码">
            <template #default="{ row }">
              {{ row.password }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="accountType" label="账户类型">
            <template #default="{ row }">
              {{ row.accountType }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="nickname" label="昵称">
            <template #default="{ row }">
              {{ row.nickname }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="mobile" label="手机电话">
            <template #default="{ row }">
              {{ row.mobile }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="email" label="邮件">
            <template #default="{ row }">
              {{ row.email }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="avatar" label="账户头像">
            <template #default="{ row }">
              {{ row.avatar }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="dataScope" label="数据权限类型">
            <template #default="{ row }">
              {{ row.dataScope }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="deptIdList" label="自定义部门权限">
            <template #default="{ row }">
              {{ row.deptIdList }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="status" label="数据状态">
            <template #default="{ row }">
              {{ row.status }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="created" label="创建时间">
            <template #default="{ row }">
              {{ row.created }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="remark" label="备注">
            <template #default="{ row }">
              {{ row.remark }}
            </template>
          </ElTableColumn>
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
