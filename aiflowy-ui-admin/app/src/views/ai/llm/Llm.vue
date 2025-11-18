<script setup>

import CategoryPanel from "#/components/categoryPanel/CategoryPanel.vue";
import {markRaw, onMounted, ref} from "vue";
import {ElTable, ElTableColumn, ElImage, ElButton, ElIcon, ElDialog} from 'element-plus'
import {getLlmBrandList} from "#/api/ai/llm.js";
import { Plus, Edit, Delete} from '@element-plus/icons-vue'
import PageData from "#/components/page/PageData.vue";
import HeaderSearch from "#/components/headerSearch/HeaderSearch.vue";
const brandListData = ref([])
const dialogTitle = ref('新增')
const LlmAddOrUpdateDialog = ref(false)
onMounted(() =>{
  getLlmBrandList().then(res => {
    console.log('res')
    console.log(res)
    brandListData.value = res.data
  })
})

const handleCategoryClick = (category) => {
  console.log('category')
  console.log(category.key)
}

// 按钮配置
const headerButtons = ref([
  {
    key: 'add',
    text: '新增大模型',
    icon: markRaw(Plus),
    type: 'primary',
    data: { action: 'addLlm' }
  },
  {
    key: 'edit',
    text: '一键添加',
    type: 'primary',
    icon: markRaw(Plus),
    data: { action: 'oneClickAdd' }
  }
])

const addLlm = () => {
  console.log('新增大模型')
  LlmAddOrUpdateDialog.value = true
}

const oneClickAdd = () => {
  console.log('一键添加')
}
// 处理搜索事件
const handleSearch = (searchValue) => {
  console.log('搜索内容:', searchValue)
  // 执行搜索逻辑
}
// 处理按钮点击事件
const handleButtonClick = (event) => {
  console.log('按钮点击事件:', event)

  // 根据按钮 key 执行不同操作
  switch (event.key) {
    case 'add':
      addLlm()
      break
    case 'edit':
      oneClickAdd()
      break
  }
}

const handleClose = () => {
  console.log('关闭对话框')
}
</script>

<template>
  <div class="llm-container">
    <div class="llm-header">
      <HeaderSearch
        :buttons="headerButtons"
        search-placeholder="搜索用户"
        @search="handleSearch"
        @button-click="handleButtonClick"
      />
    </div>

    <div class="llm-content">
      <div>
        <CategoryPanel :categories="brandListData" title-key="title" :use-img-for-svg="true" :expandWidth="150" @click="handleCategoryClick"/>
      </div>
      <div class="llm-table">
        <PageData
          ref="pageDataRef"
          page-url="/api/v1/aiLlm/page"
          :page-size="10"
          :init-query-params="{ status: 1 }"
        >
          <template #default="{ pageList }">
            <el-table :data="pageList" style="width: 100%">
              <el-table-column prop="icon" label="Icon" width="80">
                <template #default="scope">
                <el-image v-if="scope.row.icon" :src="scope.row.icon" style="width: 30px; height: 30px;"></el-image>
                </template>
              </el-table-column>
              <el-table-column prop="id" label="id" width="180" />
              <el-table-column prop="title" label="名称" width="180" />
              <el-table-column prop="description" label="描述" width="300" show-overflow-tooltip />
              <el-table-column fixed="right" label="操作" min-width="120">
                <template #default>
                  <el-button link type="primary" size="small">
                    <el-icon class="mr-1">
                      <Edit />
                    </el-icon>编辑
                  </el-button>
                  <el-button link type="primary" size="small">
                    <el-icon class="mr-1">
                    <Delete />
                  </el-icon>删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </template>
        </PageData>
      </div>
    </div>

<!--    新增大模型对话框-->
    <el-dialog
      v-model="LlmAddOrUpdateDialog"
      :title="dialogTitle"
      width="500"
      :before-close="handleClose"
    >
      <span>This is a message</span>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="LlmAddOrUpdateDialog = false">Cancel</el-button>
          <el-button type="primary" @click="LlmAddOrUpdateDialog = false">
            Confirm
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.llm-container {
  display: flex;
  flex-direction: column;
  padding: 20px;
}
.llm-header{
  margin-bottom: 20px;
}
.llm-content{
 display: flex;
}

.llm-table{
  width: 100%;
}
</style>
