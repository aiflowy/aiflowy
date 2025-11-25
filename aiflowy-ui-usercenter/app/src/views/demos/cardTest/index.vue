<!-- ParentComponent.vue -->
<template>
  <div class="page-container">
    <PageData
      ref="pageDataRef"
      page-url="/api/v1/sysAccount/page"
      :page-size="10"
      :init-query-params="{ status: 1 }"
    >
      <template #default="{ pageList }">
        <CardPage
          title-key="id"
          description-key="loginName"
          :data="pageList"
          :actions="actions"
          @action-click="handleAction"
        />
      </template>
    </PageData>
  </div>
</template>

<script setup>
import {ref, markRaw, onMounted} from 'vue'
import {Upload, Edit, Plus, Delete, View, Share, Download} from '@element-plus/icons-vue'

import CardPage from "#/components/cardPage/CardPage.vue";
import PageData from "#/components/page/PageData.vue";

// 用户数据
const userList = ref([])

// 分页状态
const currentPage = ref(1)
const pageSize = ref(5)

// 操作按钮配置
const actions = ref([
  {
    name: 'edit',
    label: '编辑',
    type: 'primary',
    icon: markRaw(Edit)
  },
  {
    name: 'view',
    label: '查看',
    type: 'success',
    icon: markRaw(View)
  },
  {
    name: 'delete',
    label: '删除',
    type: 'danger',
    icon: markRaw(Delete)
  },
  {
    name: 'share',
    label: '分享',
    type: 'info',
    icon: markRaw(Share)
  },
  {
    name: 'download',
    label: '下载',
    type: 'warning',
    icon: markRaw(Download)
  },
  {
    name: 'download',
    label: '下载',
    type: 'warning',
    icon: markRaw(Download)
  }
])

// 模拟数据加载
onMounted(() => {
  // 模拟异步数据加载
  setTimeout(() => {
    userList.value = [
      {
        id: 1,
        avatar: 'https://copyright.bdstatic.com/vcg/creative/d90a05ca26b2ca79dc1cbaa4931b18ee.jpg@wm_1,k_cGljX2JqaHdhdGVyLmpwZw==',
        title: '张三',
        description: '前端开发工程师，专注于Vue和React技术栈,前端开发工程师，专注于Vue和React技术栈'
      },
      {
        id: 2,
        avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
        title: '李四',
        description: '后端开发工程师，擅长Java和Spring框架'
      },
      {
        id: 3,
        avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
        title: '王五',
        description: 'UI设计师，专注于用户体验设计'
      },
      {
        id: 4,
        avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
        title: '赵六',
        description: '全栈开发工程师，熟悉前后端技术'
      },
      {
        id: 5,
        avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
        title: '钱七',
        description: '产品经理，负责产品规划和设计'
      },
      {
        id: 6,
        avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
        title: '孙八',
        description: '测试工程师，确保产品质量'
      }
    ]
  }, 500)
})

// 处理操作按钮点击
const handleAction = ({action, item}) => {
  console.log('执行操作:', action.name, '数据:', item)
  // 根据不同的操作执行不同的逻辑
  switch (action.name) {
    case 'edit':
      // 编辑逻辑
      break
    case 'delete':
      // 删除逻辑
      break
    // 其他操作...
  }
}

// 处理分页变化
const handlePageChange = ({currentPage: page, pageSize: size}) => {
  console.log('分页变化:', {page, size})
}

// 处理当前页更新
const handleCurrentPageUpdate = (page) => {
  currentPage.value = page
  console.log('当前页更新:', page)
}

// 处理每页数量更新
const handlePageSizeUpdate = (size) => {
  pageSize.value = size
  console.log('每页数量更新:', size)
}
</script>

<style scoped>
.parent-container {
  padding: 20px;
  width: 100%;
  margin: 0 auto;
}

h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}
</style>
