<template>
  <a-layout>
    <a-layout-sider width="200" style="background: #fff">
      <a-menu mode="inline" :style="{ height: '100%', borderRight: 0 }" @click="handleClick" :openKeys="openKeys"
        v-model:selectedKeys="selectedKeys" @select="handleMenuClick">
        <!-- 左侧导航栏 - 数据由接口控制 -->
        <a-menu-item key="welcome">
          <MailOutlined />
          <span>首页</span>
        </a-menu-item>
        <a-sub-menu v-for="item in level1" :key="item.id">
          <template v-slot:title>
            <span><user-outlined />{{ item.name }}</span>
          </template>
          <a-menu-item v-for="child in item.children" :key="child.id">
            <MailOutlined />
            <span>{{ child.name }}</span>
          </a-menu-item>
        </a-sub-menu>

        <a-menu-item key="tip" :disabled="true">
          <span>以上菜单在分类管理配置</span>
        </a-menu-item>
      </a-menu>
    </a-layout-sider>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <div class="welcome" v-show="isShowWelcome">
        <the-welcome></the-welcome>
      </div>
      <a-list v-show="!isShowWelcome && !isShowImage" item-layout="vertical" size="large"
        :grid="{ gutter: 20, column: 3 }" :data-source="ebooks">
        <template #renderItem="{ item }">
          <a-list-item key="item.name">
            <template #actions>
              <span>
                <component v-bind:is="'FileOutlined'" style="margin-right: 8px" />
                {{ item.docCount }}
              </span>
              <span>
                <component v-bind:is="'UserOutlined'" style="margin-right: 8px" />
                {{ item.viewCount }}
              </span>
              <span>
                <component v-bind:is="'LikeOutlined'" style="margin-right: 8px" />
                {{ item.voteCount }}
              </span>
            </template>
            <a-list-item-meta :description="item.description">
              <template #title>
                <router-link :to="'/doc?ebookId=' + item.id">
                  {{ item.name }}
                </router-link>
              </template>
              <template #avatar>
                <a-avatar :src="item.cover" />
              </template>
            </a-list-item-meta>
          </a-list-item>
        </template>
      </a-list>
      <!-- 图片上传 -->
      <ImagePage v-show="isShowImage" :imageType="imageType" @changeType="changeType" />
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref } from 'vue';
import axios from 'axios';
import { message } from 'ant-design-vue';
import { Tool } from "@/util/Tool";
import TheWelcome from '@/components/the-welcome.vue';
import ImagePage from '@/components/image-page.vue'


export default defineComponent({
  name: 'Home',
  components: {
    TheWelcome,
    ImagePage
  },

  setup() {
    const ebooks = ref();
    // const ebooks1 = reactive({books: []});
    const openKeys = ref();

    const level1 = ref();
    const isShowImage = ref(false);
    let selectedKeys = ref(['welcome']); // 左侧导航栏默认选择的
    let imageType = ref('imageUpload'); // 控制展示图片页面 图片上传：imageUpload，图片管理：imageManage
    // 修改图片页面状态
    const changeType = (type: string) => {
      imageType.value = type;
      const key = type === 'imageUpload' ? '501' : '502';
      selectedKeys.value = [key]; // 对应修改左侧导航栏状态
    };

    let categorys: any;
    /**
     * 查询所有分类
     **/
    const handleQueryCategory = () => {
      axios.get("/category/all").then((response) => {
        const data = response.data;
        if (data.success) {
          categorys = data.content;
          console.log("原始数组：", categorys);

          // 加载完分类后，将侧边栏全部展开
          openKeys.value = [];
          for (let i = 0; i < categorys.length; i++) {
            openKeys.value.push(categorys[i].id)
          }

          level1.value = [];
          level1.value = Tool.array2Tree(categorys, 0);
          console.log("树形结构：", level1.value);
        } else {
          message.error(data.message);
        }
      });
    };
    // 
    const handleMenuClick = (data: any) => {
      const { key = '' } = data || {};
      selectedKeys.value = [key];
    };

    const isShowWelcome = ref(true);
    let categoryId2 = 0;

    const handleQueryEbook = () => {
      axios.get("/ebook/list", {
        params: {
          page: 1,
          size: 1000,
          categoryId2: categoryId2
        }
      }).then((response) => {
        const data = response.data;
        ebooks.value = data.content.list;
        // ebooks1.books = data.content;
      });
    };

    const handleClick = (value: any) => {
      // console.log("menu click", value.key)
      const { key = '' } = value || {}
      if (key === 'welcome') {
        isShowWelcome.value = true;
        isShowImage.value = false;
      } else {
        categoryId2 = key;
        if (key === '501' || key === '502') {
          // 打开图片上传列表时，显示内容 - 前端写死
          isShowImage.value = true;
          key === '501' ? imageType.value = 'imageUpload' : imageType.value = 'imageManage';
        } else {
          isShowImage.value = false;
        }
        isShowWelcome.value = false;
        handleQueryEbook();
      }
      // isShowWelcome.value = value.key === 'welcome';
    };

    onMounted(() => {
      handleQueryCategory();
      // handleQueryEbook();
    });

    return {
      ebooks,
      // ebooks2: toRef(ebooks1, "books"),
      // listData,
      pagination: {
        onChange: (page: any) => {
          console.log(page);
        },
        pageSize: 3,
      },
      // actions: [
      //   {type: 'StarOutlined', text: '156'},
      //   {type: 'LikeOutlined', text: '156'},
      //   {type: 'MessageOutlined', text: '2'},
      // ],

      handleClick,
      selectedKeys,
      level1,
      isShowWelcome,
      isShowImage,
      imageType,
      changeType,
      handleMenuClick,

      openKeys
    }
  }
});
</script>

<style scoped>
.ant-avatar {
  width: 50px;
  height: 50px;
  line-height: 50px;
  border-radius: 8%;
  margin: 5px 0;
}
</style>
