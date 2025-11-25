<script setup lang="ts">
import { Document } from '@element-plus/icons-vue';
import { ElIcon } from 'element-plus';
// 定义类型接口（与 React 版本一致）
interface PreviewItem {
  sorting: string;
  content: string;
  score: string;
}
const props = defineProps({
  data: {
    type: Array as () => PreviewItem[],
    default: () => [],
  },
  total: {
    type: Number,
    default: 0,
  },
  loading: {
    type: Boolean,
    default: false,
  },
  confirmImport: {
    type: Boolean,
    default: false,
  },
  disabledConfirm: {
    type: Boolean,
    default: false,
  },
  onCancel: {
    type: Function,
    default: () => {},
  },
  onConfirm: {
    type: Function,
    default: () => {},
  },
  isSearching: {
    type: Boolean,
    default: false,
  },
});

// 暴露事件（直接使用 props 中的回调，无需额外定义）
const { onCancel, onConfirm } = props;
</script>

<template>
  <div class="preview-container">
    <!-- 头部区域：标题 + 统计信息 -->
    <div class="preview-header">
      <h3>
        <ElIcon class="header-icon" size="20">
          <Document />
        </ElIcon>
        {{ isSearching ? '检索结果' : '文档预览' }}
      </h3>
      <span class="preview-stats" v-if="data.length > 0">
        共 {{ total > 0 ? total : data.length }} 个分段
      </span>
    </div>

    <!-- 内容区域：列表预览 -->
    <div class="preview-content">
      <div class="preview-list">
        <div
          v-for="(item, index) in data"
          :key="index"
          class="el-list-item-container"
        >
          <div class="el-list-item">
            <div class="segment-badge">
              {{ item.sorting ?? index + 1 }}
            </div>
            <div class="el-list-item-meta">
              <div>相似度: {{ item.score }}</div>
              <div class="content-desc">{{ item.content }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 操作按钮区域（仅导入确认模式显示） -->
    <div class="preview-actions" v-if="confirmImport">
      <div class="action-buttons">
        <ElButton
          :style="{ minWidth: '100px', height: '36px' }"
          click="onCancel"
        >
          取消导入
        </ElButton>
        <ElButton
          type="primary"
          :style="{ minWidth: '100px', height: '36px' }"
          :loading="disabledConfirm"
          click="onConfirm"
        >
          确认导入
        </ElButton>
      </div>
    </div>
  </div>
</template>

<style scoped>
.preview-container {
  width: 100%;
  border-radius: 8px;
  background-color: var(--el-bg-color);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  overflow: hidden;
  height: 100%;
  .preview-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 20px;
    border-bottom: 1px solid var(--el-border-color);

    h3 {
      margin: 0;
      font-size: 16px;
      font-weight: 500;
      color: var(--el-text-color-primary);
      display: flex;
      align-items: center;
      gap: 8px;

      .header-icon {
        color: var(--el-color-primary);
      }
    }

    .preview-stats {
      font-size: 14px;
      color: var(--el-text-color-secondary);
    }
  }

  .preview-content {
    //max-height: 500px; /* 限制最大高度，可根据需求调整 */
    overflow-y: auto;
    padding: 20px;

    .preview-list {
      .segment-badge {
        width: 24px;
        height: 24px;
        border-radius: 50%;
        background-color: var(--el-color-primary-light-9);
        color: var(--el-color-primary);
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 12px;
        font-weight: 500;
      }

      .similarity-score {
        font-size: 14px;
        color: var(--el-color-primary);
        text-decoration: none;

        &:hover {
          text-decoration: underline;
        }
      }

      .content-desc {
        color: #475569;
        font-size: 14px;
        line-height: 1.6;
        white-space: pre-wrap;
        background: #f8fafc;
        padding: 12px;
        border-radius: 6px;
        border-left: 3px solid #e2e8f0;
        transition: all 0.2s;
        width: 100%;
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(67, 97, 238, 0.08);
          border-color: #4361ee;
        }
      }

      .el-list-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 18px;
        border-radius: 8px;
      }

      .el-list-item-meta {
        align-items: flex-start;
        display: flex;
        flex-direction: column;
        gap: 12px;
        flex: 1;
      }
    }
  }

  .preview-actions {
    padding: 16px 20px;
    border-top: 1px solid var(--el-border-color);
    background-color: var(--el-bg-color-page);

    .action-buttons {
      display: flex;
      justify-content: flex-end;
      gap: 12px;
    }
  }
}

/* 适配 Element Plus 加载状态样式 */
.el-list--loading .el-list-loading {
  padding: 40px 0;
}

.el-list-item {
  border: 1px solid var(--el-border-color-lighter);
  margin-top: 12px;
  width: 100%;
  &:hover {
    border-color: var(--el-color-primary);
  }
}
</style>
