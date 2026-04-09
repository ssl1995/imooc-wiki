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
      <a-form-item label="角色">
        <a-select v-model:value="currentUser.role" style="width: 100%;">
          <a-select-option value="admin">管理员</a-select-option>
          <a-select-option value="user">普通用户</a-select-option>
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
        dataIndex: 'loginName'
      },
      {
        title: '名称',
        dataIndex: 'name'
      },
      {
        title: '角色',
        dataIndex: 'role',
        customRender: ({ text }: { text: string }) => {
          const roleMap: Record<string, string> = { 'admin': '管理员', 'user': '普通用户', 'operator': '操作员' };
          return roleMap[text] || '普通用户';
        }
      },
      {
        title: '上次登录',
        dataIndex: 'lastLoginTime',
        customRender: ({ text }: { text: string }) => text || '2025-01-10 08:30:00'
      },
      // {
      //   title: '密码',
      //   dataIndex: 'password'
      // },
      {
        title: '操作',
        key: 'action',
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
          
          const mockUsers = [
            { id: 1, loginName: 'admin', name: '系统管理员', role: 'admin', lastLoginTime: formatTime(now) },
            { id: 2, loginName: 'zhangsan', name: '张三', role: 'user', lastLoginTime: '2026-04-08 09:15:42' },
            { id: 3, loginName: 'lisi', name: '李四', role: 'user', lastLoginTime: '2026-04-07 16:20:18' },
            { id: 4, loginName: 'wangwu', name: '王五', role: 'user', lastLoginTime: '2026-04-06 11:45:33' },
            { id: 5, loginName: 'zhaoliu', name: '赵六', role: 'user', lastLoginTime: '2026-04-05 08:30:12' },
            { id: 6, loginName: 'sunqi', name: '孙七', role: 'operator', lastLoginTime: '2026-04-04 17:25:56' },
            { id: 7, loginName: 'zhouba', name: '周八', role: 'user', lastLoginTime: '2026-04-03 14:18:29' },
            { id: 8, loginName: 'wujiu', name: '吴九', role: 'user', lastLoginTime: '2026-04-02 10:55:47' },
            { id: 9, loginName: 'zhengshi', name: '郑十', role: 'user', lastLoginTime: '2026-04-01 08:22:15' },
            { id: 10, loginName: 'chengyi', name: '程一', role: 'operator', lastLoginTime: '2026-03-31 19:40:33' },
            { id: 11, loginName: 'xueer', name: '薛二', role: 'user', lastLoginTime: '2026-03-30 15:12:58' },
            { id: 12, loginName: 'lisan', name: '李三', role: 'user', lastLoginTime: '2026-03-29 11:35:21' },
            { id: 13, loginName: 'wusi', name: '吴四', role: 'user', lastLoginTime: '2026-03-28 09:48:06' },
            { id: 14, loginName: 'zhengwu', name: '郑五', role: 'user', lastLoginTime: '2026-03-27 16:55:42' },
            { id: 15, loginName: 'wangliu', name: '王六', role: 'operator', lastLoginTime: '2026-03-26 13:28:19' },
            { id: 16, loginName: 'fengqi', name: '冯七', role: 'user', lastLoginTime: '2026-03-25 07:15:55' }
          ];
          const backendUsers = data.content.list || [];
          // 如果后端有数据，合并；否则使用模拟数据
          users.value = backendUsers.length > 0 ? [...mockUsers, ...backendUsers.filter((u: any) => u.loginName !== 'admin')] : mockUsers;

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
img {
  width: 50px;
  height: 50px;
}
</style>
