<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { onMounted, ref } from 'vue';

import { formatBytes } from '@aiflowy/utils';

import {
  Delete,
  Download,
  Edit,
  More,
  Plus,
  View,
} from '@element-plus/icons-vue';
import {
  ElAvatar,
  ElButton,
  ElDropdown,
  ElDropdownItem,
  ElDropdownMenu,
  ElForm,
  ElFormItem,
  ElIcon,
  ElInput,
  ElMessage,
  ElMessageBox,
  ElTable,
  ElTableColumn,
  ElText,
  ElTooltip,
} from 'element-plus';

import { api } from '#/api/request';
import DictSelect from '#/components/dict/DictSelect.vue';
import PageData from '#/components/page/PageData.vue';
import Tag from '#/components/tag/Tag.vue';
import { $t } from '#/locales';
import { useDictStore } from '#/store';
import {
  getResourceOriginColor,
  getResourceTypeColor,
  getSrc,
} from '#/utils/resource';
import PreviewModal from '#/views/ai/resource/PreviewModal.vue';

import AiResourceModal from './AiResourceModal.vue';

onMounted(() => {
  initDict();
});
const formRef = ref<FormInstance>();
const pageDataRef = ref();
const saveDialog = ref();
const previewDialog = ref();
const formInline = ref({
  resourceName: '',
  resourceType: '',
});
const dictStore = useDictStore();
function initDict() {
  dictStore.fetchDictionary('resourceType');
  dictStore.fetchDictionary('resourceOriginType');
}
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
  ElMessageBox.confirm($t('message.deleteAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('message.ok'),
    cancelButtonText: $t('message.cancel'),
    type: 'warning',
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        instance.confirmButtonLoading = true;
        api
          .post('/api/v1/aiResource/remove', { id: row.id })
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
function preview(row: any) {
  previewDialog.value.openDialog({ ...row });
}
function download(row: any) {
  window.open(row.resourceUrl, '_blank');
}
</script>

<template>
  <div class="flex h-full flex-col gap-1.5 p-6">
    <PreviewModal ref="previewDialog" />
    <AiResourceModal ref="saveDialog" @reload="reset" />
    <div class="flex items-center justify-between">
      <ElForm ref="formRef" :inline="true" :model="formInline">
        <ElFormItem prop="resourceType">
          <DictSelect
            v-model="formInline.resourceType"
            dict-code="resourceType"
            :placeholder="$t('aiResource.resourceType')"
          />
        </ElFormItem>
        <ElFormItem prop="resourceName">
          <ElInput
            v-model="formInline.resourceName"
            :placeholder="$t('aiResource.resourceName')"
          />
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
        <ElButton
          v-access:code="'/api/v1/aiResource/save'"
          @click="showDialog({})"
          type="primary"
        >
          <ElIcon class="mr-1">
            <Plus />
          </ElIcon>
          {{ $t('button.add') }}
        </ElButton>
      </div>
    </div>

    <div class="bg-background flex-1 rounded-lg p-5">
      <PageData
        ref="pageDataRef"
        page-url="/api/v1/aiResource/page"
        :page-size="10"
      >
        <template #default="{ pageList }">
          <ElTable :data="pageList" border>
            <ElTableColumn
              prop="resourceName"
              :label="$t('aiResource.resourceName')"
              width="300"
            >
              <template #default="{ row }">
                <div class="flex items-center gap-2.5">
                  <ElAvatar :src="getSrc(row)" shape="square" :size="50" />
                  <div class="w-[200px]">
                    <ElTooltip :content="`${row.resourceName}`" placement="top">
                      <ElText truncated>
                        {{ row.resourceName }}
                      </ElText>
                    </ElTooltip>
                  </div>
                </div>
              </template>
            </ElTableColumn>
            <ElTableColumn
              align="center"
              prop="suffix"
              :label="$t('aiResource.suffix')"
              width="60"
            >
              <template #default="{ row }">
                {{ row.suffix }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              align="center"
              prop="fileSize"
              :label="$t('aiResource.fileSize')"
            >
              <template #default="{ row }">
                {{ formatBytes(row.fileSize) }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              align="center"
              prop="origin"
              :label="$t('aiResource.origin')"
            >
              <template #default="{ row }">
                <Tag
                  size="small"
                  :background-color="`${getResourceOriginColor(row)}15`"
                  :text-color="getResourceOriginColor(row)"
                  :text="
                    dictStore.getDictLabel('resourceOriginType', row.origin)
                  "
                />
              </template>
            </ElTableColumn>
            <ElTableColumn
              align="center"
              prop="resourceType"
              :label="$t('aiResource.resourceType')"
            >
              <template #default="{ row }">
                <Tag
                  size="small"
                  :background-color="`${getResourceTypeColor(row)}15`"
                  :text-color="getResourceTypeColor(row)"
                  :text="
                    dictStore.getDictLabel('resourceType', row.resourceType)
                  "
                />
              </template>
            </ElTableColumn>
            <ElTableColumn prop="created" :label="$t('aiResource.created')">
              <template #default="{ row }">
                {{ row.created }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              align="center"
              :label="$t('common.handle')"
              width="80"
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
                      <ElDropdownItem @click="preview(row)">
                        <ElButton :icon="View" link>
                          {{ $t('button.view') }}
                        </ElButton>
                      </ElDropdownItem>
                      <ElDropdownItem @click="download(row)">
                        <ElButton :icon="Download" link>
                          {{ $t('button.download') }}
                        </ElButton>
                      </ElDropdownItem>
                      <div v-access:code="'/api/v1/aiResource/save'">
                        <ElDropdownItem @click="showDialog(row)">
                          <ElButton :icon="Edit" link>
                            {{ $t('button.edit') }}
                          </ElButton>
                        </ElDropdownItem>
                      </div>
                      <div v-access:code="'/api/v1/aiResource/remove'">
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
