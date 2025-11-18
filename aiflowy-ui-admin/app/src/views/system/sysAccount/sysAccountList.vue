<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { ref } from 'vue';

import {
  ElButton,
  ElForm,
  ElFormItem,
  ElInput,
  ElTable,
  ElTableColumn,
} from 'element-plus';

import DictSelect from '#/components/dict/DictSelect.vue';
import PageData from '#/components/page/PageData.vue';

const formRef = ref();
const pageDataRef = ref();
const search = (formEl: FormInstance) => {
  formEl.validate((valid) => {
    if (valid) {
      pageDataRef.value.setQuery(formInline.value);
    }
  });
};
const reset = (formEl: FormInstance) => {
  formEl.resetFields();
  pageDataRef.value.setQuery({});
};
const formInline = ref({
  loginName: '',
  accountType: '',
});
</script>

<template>
  <div class="page-container">
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
        <ElButton @click="search(formRef)" type="primary">查询</ElButton>
        <ElButton @click="reset(formRef)">重置</ElButton>
      </ElFormItem>
    </ElForm>
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
        </ElTable>
      </template>
    </PageData>
  </div>
</template>

<style scoped></style>
