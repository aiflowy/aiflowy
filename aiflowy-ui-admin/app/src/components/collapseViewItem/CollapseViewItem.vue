<script setup lang="ts">
import { $t } from '@aiflowy/locales';

import { Delete } from '@element-plus/icons-vue';
import { ElAvatar, ElIcon, ElMessageBox } from 'element-plus';

const props = defineProps({
  data: {
    type: Array as any,
    default: () => [],
  },
  titleKey: {
    type: String,
    default: 'title',
  },
  descriptionKey: {
    type: String,
    default: 'description',
  },
});
const emits = defineEmits(['delete']);
const handleDelete = (item: any) => {
  ElMessageBox.confirm($t('message.deleteAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('button.confirm'),
    cancelButtonText: $t('button.cancel'),
    type: 'warning',
  }).then(() => {
    emits('delete', item);
  });
};
</script>

<template>
  <div class="collapse-item-container">
    <div
      v-for="(item, index) in props.data"
      :key="index"
      class="el-list-item-max-container"
    >
      <div class="el-list-item-container">
        <div class="flex-center">
          <ElAvatar :src="item.icon" v-if="item.icon" />
          <ElAvatar v-else src="/favicon.png" shape="circle" />
        </div>
        <div class="el-list-item-content">
          <div>{{ item[titleKey] }}</div>
          <div>{{ item[descriptionKey] }}</div>
        </div>
      </div>
      <ElIcon
        color="var(--el-color-danger)"
        size="20px"
        @click="handleDelete(item)"
        class="el-list-item-delete-container"
      >
        <Delete />
      </ElIcon>
    </div>
  </div>
</template>

<style scoped>
.el-list-item-max-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px 12px 12px;
  background-color: var(--el-bg-color);
  border-radius: 8px;
}
.collapse-item-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
  background-color: #f5f5f5;
  padding: 10px;
}
.el-list-item-container {
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  align-items: center;
  gap: 10px;
}
.el-list-item-content {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  gap: 5px;
}
.el-list-item-delete-container {
  cursor: pointer;
}
</style>
