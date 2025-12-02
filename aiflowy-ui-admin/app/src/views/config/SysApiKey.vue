<script setup lang="ts">
import { ref } from 'vue';

import { ElDialog } from 'element-plus';

import SysApiKeyList from '#/views/config/apikey/SysApiKeyList.vue';
import SysApiKeyResourcePermissionList from '#/views/config/apikey/SysApiKeyResourcePermissionList.vue';

const dialogVisible = ref(false);
</script>
<template>
  <div class="sys-apikey-container">
    <SysApiKeyList />
    <ElDialog
      v-model="dialogVisible"
      draggable
      :close-on-click-modal="false"
      align-center
    >
      <SysApiKeyResourcePermissionList />
    </ElDialog>
  </div>
</template>

<style scoped>
.sys-apikey-container {
  height: 100%;
  padding: 20px;
  width: 100%;
}
</style>
