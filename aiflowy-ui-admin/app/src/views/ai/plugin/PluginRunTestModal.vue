<script setup lang="ts">
import { ref, watch } from 'vue';

import { $t } from '@aiflowy/locales';
import { preferences } from '@aiflowy/preferences';

import { VideoPlay } from '@element-plus/icons-vue';
import { ElButton, ElDialog, ElMenu, ElMenuItem } from 'element-plus';
import { JsonViewer } from 'vue3-json-viewer';

import { api } from '#/api/request';
import PluginRunParams from '#/views/ai/plugin/PluginRunParams.vue';

import 'vue3-json-viewer/dist/vue3-json-viewer.css';

const props = defineProps<{
  pluginToolId: string;
}>();

const themeMode = ref(preferences.theme.mode);
watch(
  () => preferences.theme.mode,
  (newVal) => {
    themeMode.value = newVal;
  },
);
const dialogVisible = ref(false);
const openDialog = () => {
  getPluginToolInfo();
  runResultResponse.value = null;
  dialogVisible.value = true;
};
const runTitle = ref('');
const runResult = ref('');
const inputDataParams = ref<any>(null);
const runResultResponse = ref<any>(null);
function getPluginToolInfo() {
  api
    .post('/api/v1/aiPluginTool/tool/search', {
      aiPluginToolId: props.pluginToolId,
    })
    .then((res) => {
      if (res.errorCode === 0) {
        runTitle.value = `${res.data.aiPlugin.title} - ${res.data.data.name} ${$t(
          'aiPluginTool.inputData',
        )}`;
        runResult.value = `${$t('aiPluginTool.pluginToolEdit.runResult')}`;
        inputDataParams.value = JSON.parse(res.data.data.inputData || '[]');
      }
    });
}
const activeIndex = ref('1');
defineExpose({
  openDialog,
});
function handleSelect(index: string) {
  activeIndex.value = index;
}
const runParamsRef = ref();
const runLoading = ref(false);
function handleSubmitRun() {
  runLoading.value = true;
  const runParams = runParamsRef.value.handleSubmitParams();
  api
    .post('/api/v1/aiPluginTool/test', {
      pluginToolId: props.pluginToolId,
      inputData: JSON.stringify(runParams),
    })
    .then((res) => {
      if (res.errorCode === 0) {
        runResultResponse.value = res.data;
        activeIndex.value = '2';
      }
      runLoading.value = false;
    });
}
</script>

<template>
  <ElDialog
    v-model="dialogVisible"
    draggable
    :close-on-click-modal="false"
    width="80%"
    align-center
    class="run-test-dialog"
    :title="$t('aiPluginTool.pluginToolEdit.trialRun')"
  >
    <div class="run-test-container">
      <div class="run-test-params">
        <div class="run-title-style">
          {{ runTitle }}
        </div>
        <div>
          <PluginRunParams
            v-model="inputDataParams"
            :editable="true"
            :is-edit-output="true"
            ref="runParamsRef"
          />
        </div>
      </div>
      <div class="run-test-result">
        <div class="run-title-style">
          {{ runResult }}
        </div>
        <div>
          <ElMenu
            :default-active="activeIndex"
            class="el-menu-demo"
            mode="horizontal"
            :ellipsis="false"
            @select="handleSelect"
          >
            <ElMenuItem index="1">Request</ElMenuItem>
            <ElMenuItem index="2">Response</ElMenuItem>
          </ElMenu>
        </div>
        <div class="run-res-json">
          <JsonViewer
            v-if="activeIndex === '1'"
            :value="inputDataParams || {}"
            copyable
            :expand-depth="Infinity"
            :theme="themeMode"
          />
          <JsonViewer
            v-if="activeIndex === '2'"
            :value="runResultResponse || {}"
            copyable
            :expand-depth="Infinity"
            :theme="themeMode"
          />
        </div>
      </div>
    </div>
    <template #footer>
      <ElButton @click="dialogVisible = false">
        {{ $t('button.cancel') }}
      </ElButton>
      <ElButton
        type="primary"
        :icon="VideoPlay"
        @click="handleSubmitRun"
        :loading="runLoading"
      >
        {{ $t('aiPluginTool.pluginToolEdit.run') }}
      </ElButton>
    </template>
  </ElDialog>
</template>

<style scoped>
.run-test-container {
  display: flex;
  gap: 16px;
  width: 100%;
  height: calc(100vh - 161px);
}
.run-test-dialog {
}
.run-test-params {
  width: 100%;
  overflow: auto;
  flex: 1;
}
.run-res-json {
  width: 100%;
  flex: 1;
  overflow: auto;
}
.run-test-result {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.name-cell {
  position: relative;
  min-width: 100%;
}
.run-title-style {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
}
.editable-name {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.name-input-wrapper {
  display: flex;
  align-items: center;
  width: 100%;
}

.name-input-wrapper .el-input {
  box-sizing: border-box;
  width: 100%;
}

.error-message {
  color: #ff4d4f;
  font-size: 12px;
  margin-top: 2px;
  line-height: 1.2;
}

:deep(.el-table td.el-table__cell.first-column div) {
  display: flex;
  align-items: center;
  gap: 2px;
}
</style>
