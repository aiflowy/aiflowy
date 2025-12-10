<script setup lang="ts">
import {ElAvatar, ElIcon, ElMessage, ElMessageBox} from 'element-plus';
import { Delete } from "@element-plus/icons-vue";
import {$t} from "@aiflowy/locales";

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
        <div>
          <ElAvatar :src="item.icon" v-if="item.icon" />
          <ElAvatar v-else src="/favicon.png" shape="circle" />
        </div>
        <div class="el-list-item-content">
          <div>{{ item[titleKey] }}</div>
          <div>{{ item[descriptionKey] }}</div>
        </div>
      </div>
      <div @click="handleDelete(item)" class="el-list-item-delete-container">
        <ElIcon>
          <Delete />
        </ElIcon>
      </div>
    </div>
  </div>
</template>

<style scoped>
.el-list-item-max-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px;
}
.collapse-item-container {
  display: flex;
  flex-direction: column;
}
.el-list-item-container {
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
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
