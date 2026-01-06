<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { onMounted, ref } from 'vue';

import {
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
  ElSwitch,
  ElTable,
  ElTableColumn,
  ElTabPane,
  ElTabs,
} from 'element-plus';

import { api } from '#/api/request';
import { $t } from '#/locales';

interface PropValue {
  type?: string;
  description?: string;
}

interface McpTool {
  name: string;
  description: string;
  status: boolean;
  inputSchema?: {
    properties: Record<string, PropValue>;
    required?: string[];
  };
}

interface McpEntity {
  id?: string;
  title: string;
  description: string;
  configJson: string;
  deptId: string;
  status: boolean;
  tools: McpTool[];
}

const emit = defineEmits(['reload']);

onMounted(() => {});
defineExpose({
  openDialog,
});

const saveForm = ref<FormInstance>();
const dialogVisible = ref(false);
const isAdd = ref(true);
const btnLoading = ref(false);

const defaultEntity: McpEntity = {
  title: '',
  description: '',
  configJson: '',
  deptId: '',
  status: false,
  tools: [],
};
const entity = ref<McpEntity>({ ...defaultEntity });

const validateJson = (
  _: any,
  value: string,
  callback: (error?: Error) => void,
) => {
  if (!value) {
    return callback();
  }
  try {
    JSON.parse(value);
    callback();
  } catch {
    callback(new Error($t('message.invalidJson') || '请输入合法的 JSON 格式'));
  }
};

const rules = ref({
  title: [
    {
      required: true,
      message: $t('message.required'),
      trigger: 'blur',
    },
  ],
  configJson: [
    {
      validator: validateJson,
      trigger: 'blur',
      message: '请输入合法的 JSON 格式',
    },
  ],
});

function openDialog(row: Partial<McpEntity> = {}) {
  isAdd.value = !row.id;
  entity.value = { ...defaultEntity, ...row };
  dialogVisible.value = true;
}

function save() {
  saveForm.value?.validate((valid) => {
    if (valid) {
      btnLoading.value = true;
      api
        .post(
          isAdd.value ? 'api/v1/mcp/save' : 'api/v1/mcp/update',
          entity.value,
        )
        .then((res) => {
          btnLoading.value = false;
          if (res.errorCode === 0) {
            ElMessage.success(res.message);
            emit('reload');
            closeDialog();
          }
        })
        .catch(() => {
          btnLoading.value = false;
        });
    }
  });
}

function closeDialog() {
  saveForm.value?.resetFields();
  isAdd.value = true;
  entity.value = { ...defaultEntity };
  dialogVisible.value = false;
}

const activeName = ref('config');
</script>

<template>
  <ElDialog
    v-model="dialogVisible"
    draggable
    :title="isAdd ? $t('button.add') : $t('button.edit')"
    :before-close="closeDialog"
    :close-on-click-modal="false"
  >
    <ElTabs v-model="activeName" class="demo-tabs">
      <ElTabPane :label="$t('mcp.modal.config')" name="config">
        <ElForm
          label-width="120px"
          ref="saveForm"
          :model="entity"
          status-icon
          :rules="rules"
        >
          <ElFormItem prop="title" :label="$t('mcp.title')">
            <ElInput v-model.trim="entity.title" />
          </ElFormItem>
          <ElFormItem prop="description" :label="$t('mcp.description')">
            <ElInput v-model.trim="entity.description" />
          </ElFormItem>
          <ElFormItem prop="configJson" :label="$t('mcp.configJson')">
            <ElInput
              type="textarea"
              :rows="15"
              v-model.trim="entity.configJson"
            />
          </ElFormItem>
          <ElFormItem prop="status" :label="$t('mcp.status')">
            <ElSwitch v-model="entity.status" />
          </ElFormItem>
        </ElForm>
      </ElTabPane>
      <ElTabPane :label="$t('mcp.modal.tool')" name="tool">
        <ElTable :data="entity.tools" border :preserve-expanded-content="true">
          <ElTableColumn type="expand">
            <template #default="scope">
              <!-- 解构获取properties和required，同时做空值保护 -->
              <div
                v-if="scope.row?.inputSchema?.properties"
                class="params-list"
              >
                <div
                  v-for="([propKey, propValue], index) in Object.entries(
                    scope.row.inputSchema.properties,
                  )"
                  :key="index"
                  class="params-content-container"
                >
                  <div class="params-left-title-container">
                    <div class="content-title">
                      {{ propKey }}
                      <span
                        v-if="
                          scope.row.inputSchema.required &&
                          scope.row.inputSchema.required.includes(propKey)
                        "
                        class="required-mark"
                      >
                        *
                      </span>
                    </div>
                  </div>
                  <div class="params-desc-container">
                    <div class="content-title">
                      {{ (propValue as PropValue).type || '未知类型' }}
                    </div>
                    <div class="content-desc">
                      {{ (propValue as PropValue).description || '无描述' }}
                    </div>
                  </div>
                </div>
              </div>
              <div v-else class="params-name">暂无属性配置</div>
            </template>
          </ElTableColumn>

          <ElTableColumn :label="$t('mcp.modal.table.availableTools')">
            <template #default="{ row }">
              <div class="content-left">
                <span class="content-title">{{ row.name }}</span>
                <span class="content-desc">{{ row.description }}</span>
              </div>
            </template>
          </ElTableColumn>
          <!--          <ElTableColumn :label="$t('mcp.status')">
            <template #default="{ row }">
              <ElSwitch v-model="row.status" />
            </template>
          </ElTableColumn>-->
        </ElTable>
      </ElTabPane>
    </ElTabs>
    <template #footer>
      <ElButton @click="closeDialog">
        {{ $t('button.cancel') }}
      </ElButton>
      <ElButton
        type="primary"
        @click="save"
        :loading="btnLoading"
        :disabled="btnLoading"
      >
        {{ $t('button.save') }}
      </ElButton>
    </template>
  </ElDialog>
</template>

<style scoped>
.content-left {
  display: flex;
  flex-direction: column;
}
.content-title {
  font-weight: 500;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.85);
  line-height: 24px;
  text-align: left;
  font-style: normal;
  text-transform: none;
}
.content-desc {
  font-weight: 400;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.45);
  line-height: 22px;
  text-align: left;
  font-style: normal;
  text-transform: none;
}
.params-name {
  flex: 1;
  background-color: #fafafa;
  display: flex;
  align-items: center;
  border: 1px solid #e6e9ee;
  border-radius: 8px;
  padding: 8px;
}
.params-content-container {
  display: flex;
  flex-direction: row;
  gap: 8px;
  flex: 1;
  border-radius: 8px;
  padding: 8px;
}
.params-desc-container {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
  border-radius: 8px;
  border: 1px solid #e6e9ee;
  padding: 8px;
}
.params-list {
  display: flex;
  flex-direction: column;
}

.params-left-title-container {
  display: flex;
  flex-direction: row;
  background-color: #fafafa;
  gap: 8px;
  flex: 1;
  border: 1px solid #e6e9ee;
  border-radius: 8px;
  padding: 8px;
  align-items: center;
}
.required-mark {
  color: #f56c6c;
  margin-left: 2px;
  font-size: 14px;
}
</style>
