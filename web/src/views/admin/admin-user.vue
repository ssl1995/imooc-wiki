<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-input v-model:value="param.loginName" placeholder="登陆名">
            </a-input>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleQuery({page: 1, size: pagination.pageSize})">
              查询
            </a-button>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="add()">
              新增
            </a-button>
          </a-form-item>
        </a-form>
      </p>
      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="users"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-button type="primary" @click="resetPassword(record)">
              重置密码
            </a-button>
            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>
            <a-popconfirm
                title="删除后不可恢复，确认删除?"
                ok-text="是"
                cancel-text="否"
                @confirm="handleDelete(record.id)"
            >
              <a-button type="danger">
                删除
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>

  <a-modal
      title="用户表单"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
  >
    <a-form :model="currentUser" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="登陆名">
        <a-input v-model:value="currentUser.loginName" :disabled="!!currentUser.id"/>
      </a-form-item>
      <a-form-item label="昵称">
        <a-input v-model:value="currentUser.name"/>
      </a-form-item>
      <a-form-item label="密码" v-show="!currentUser.id">
        <a-input v-model:value="currentUser.password" type="password"/>
      </a-form-item>
      <a-form-item label="年龄">
        <a-input v-model:value="currentUser.age" type="number"/>
      </a-form-item>
      <a-form-item label="用户介绍">
        <a-input v-model:value="currentUser.desc"/>
      </a-form-item>
      <a-form-item label="角色">
        <a-select v-model:value="currentUser.perRoleId" style="width: 100%;">
          <a-select-option :value="1">管理员</a-select-option>
          <a-select-option :value="2">普通用户</a-select-option>
        </a-select>
      </a-form-item>
    </a-form>
  </a-modal>

  <a-modal
      title="重置密码"
      v-model:visible="resetModalVisible"
      :confirm-loading="resetModalLoading"
      @ok="handleResetModalOk"
  >
    <a-form :model="currentUser" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="新密码">
        <a-input v-model:value="currentUser.password" type="password"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts">
import {defineComponent, onMounted, ref, computed} from 'vue';
import axios from 'axios';
import {message} from 'ant-design-vue';
import {Tool} from "@/util/Tool";
import store from "@/store";

declare let hexMd5: any;
declare let KEY: any;

export default defineComponent({
  name: 'AdminUser',
  setup() {
    const param = ref();
    param.value = {};
    const users = ref();
    const pagination = ref({
      current: 1,
      pageSize: 10,
      total: 0
    });
    const loading = ref(false);

    const columns = [
      {
        title: '登陆名',
        dataIndex: 'loginName',
        width: 120
      },
      {
        title: '名称',
        dataIndex: 'name',
        width: 120
      },
      {
        title: '年龄',
        dataIndex: 'age',
        width: 80
      },
      {
        title: '角色',
        dataIndex: 'roleName',
        width: 100
      },
      {
        title: '用户介绍',
        dataIndex: 'desc',
        ellipsis: true,
        width: 200
      },
      {
        title: '上次登录',
        dataIndex: 'lastLoginTime',
        width: 170,
        customRender: ({ text }: { text: any }) => {
          if (!text) return '-';
          const ts = typeof text === 'string' ? Number(text) : text;
          const date = new Date(ts);
          return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`;
        }
      },
      {
        title: '操作',
        key: 'action',
        width: 320,
        slots: {customRender: 'action'}
      }
    ];

    /**
     * 数据查询
     **/
    const handleQuery = (params: any) => {
      loading.value = true;
      // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
      users.value = [];
      axios.get("/user/list", {
        params: {
          page: params.page,
          size: params.size,
          loginName: param.value.loginName
        }
      }).then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          // 生成更多模拟数据撑满页面（约15-16条）
          const now = new Date();
          const formatTime = (date: Date) => {
            return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`;
          };
          
          const list = data.content.list || [];
          users.value = list;

          // 重置分页按钮
          pagination.value.current = params.page;
          pagination.value.total = data.content.total;
        } else {
          message.error(data.message);
        }
      });
    };

    /**
     * 表格点击页码时触发
     */
    const handleTableChange = (pagination: any) => {
      console.log("看看自带的分页参数都有啥：" + pagination);
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      });
    };

    // -------- 表单 ---------
    const user = computed(() => store.state.user);
    const currentUser = ref();
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const handleModalOk = () => {
      modalLoading.value = true;

      // 前端密码加密：key是一个盐值防止常见明文被识别破解
      currentUser.value.password = hexMd5(currentUser.value.password + KEY);

      axios.post("/user/save", currentUser.value).then((response) => {
        modalLoading.value = false;
        const data = response.data; // data = commonResp
        if (data.success) {
          modalVisible.value = false;

          // 重新加载列表
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
          });
        } else {
          message.error(data.message);
        }
      });
    };

    /**
     * 编辑
     */
    const edit = (record: any) => {
      modalVisible.value = true;
      currentUser.value = Tool.copy(record);
    };

    /**
     * 新增
     */
    const add = () => {
      modalVisible.value = true;
      currentUser.value = {};
    };

    const handleDelete = (id: number) => {
      axios.delete("/user/delete/" + id).then((response) => {
        const data = response.data; // data = commonResp
        if (data.success) {
          // 重新加载列表
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
          });
        } else {
          message.error(data.message);
        }
      });
    };

    // -------- 重置密码 ---------
    const resetModalVisible = ref(false);
    const resetModalLoading = ref(false);
    const handleResetModalOk = () => {
      resetModalLoading.value = true;

      currentUser.value.password = hexMd5(currentUser.value.password + KEY);

      axios.post("/user/reset-password", currentUser.value).then((response) => {
        resetModalLoading.value = false;
        const data = response.data; // data = commonResp
        if (data.success) {
          resetModalVisible.value = false;

          // 重新加载列表
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
          });
        } else {
          message.error(data.message);
        }
      });
    };

    /**
     * 重置密码
     */
    const resetPassword = (record: any) => {
      resetModalVisible.value = true;
      currentUser.value = Tool.copy(record);
      currentUser.value.password = null;
    };

    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize,
      });
    });

    return {
      param,
      users,
      pagination,
      columns,
      loading,
      handleTableChange,
      handleQuery,

      edit,
      add,

      user,
      currentUser,
      modalVisible,
      modalLoading,
      handleModalOk,

      handleDelete,

      resetModalVisible,
      resetModalLoading,
      handleResetModalOk,
      resetPassword
    }
  }
});
</script>

<style scoped>
/* 覆盖Ant Design表格默认字体，确保论文截图清晰 */
:deep(.ant-table) {
  font-size: 18px !important;
}

/* 表头加粗放大 */
:deep(.ant-table-thead > tr > th) {
  font-size: 18px !important;
  font-weight: 600;
  padding: 16px 12px;
}

/* 表格内容单元格 */
:deep(.ant-table-tbody > tr > td) {
  font-size: 18px !important;
  padding: 16px 12px;
}

/* 操作按钮放大 */
:deep(.ant-btn) {
  font-size: 16px;
  height: 38px;
  padding: 0 16px;
}

/* 搜索区域输入框和按钮 */
:deep(.ant-input),
:deep(.ant-form-item .ant-btn) {
  font-size: 16px;
  height: 38px;
}

/* 分页组件字体 */
:deep(.ant-pagination) {
  font-size: 16px;
}

img {
  width: 50px;
  height: 50px;
}
</style>
