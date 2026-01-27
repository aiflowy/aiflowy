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
          <div class="title">{{ item[titleKey] }}</div>
          <div class="description">{{ item[descriptionKey] }}</div>
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
  align-items: center;
  justify-content: space-between;
  padding: 12px 24px 12px 12px;
  background-color: hsl(var(--background));
  border-radius: 8px;
}

.collapse-item-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 10px;
  background-color: var(--bot-collapse-itme-back);
}

.el-list-item-container {
  display: flex;
  flex-direction: row;
  gap: 10px;
  align-items: center;
  justify-content: flex-start;
}

.el-list-item-content {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

.el-list-item-delete-container {
  cursor: pointer;
}

.title {
  font-family: PingFangSC, 'PingFang SC', sans-serif;
  font-size: 12px;
  font-style: normal;
  font-weight: 500;
  line-height: 24px;
  text-align: left;
  text-transform: none;
}

.description {
  font-family: PingFangSC, 'PingFang SC', sans-serif;
  font-size: 12px;
  font-style: normal;
  font-weight: 400;
  line-height: 22px;
  text-align: left;
  text-transform: none;
  opacity: 0.65;
}
</style>
