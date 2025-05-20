<template>
  <div>
    <!-- 图片上传 -->
    <a-card :title="cardTitle" class="card-style" v-if="props.imageType === 'imageUpload'">
      <template #extra><a href="#" @click="changeType('imageUpload')">{{ cardTitle }}</a></template>
      <div class="upload-content">
        <!-- 选择日期 -->
        <a-date-picker v-model="date" placeholder="请选择图片上传日期" class="antd-select" :format="dateFormat"/>
        <!-- 选择图片类型 -->
        <a-select v-model="selectType" class="antd-select" placeholder="请选择图片类型">
          <a-select-option v-for="(item, index) in selectList" :key="index" :value="item.value">{{
              item.label
            }}
          </a-select-option>
        </a-select>
        <!-- 上传图片区 -->
        <a-upload v-model:file-list="fileList" name="avatar" list-type="picture-card" class="avatar-uploader"
                  :show-upload-list="false" action="https://www.mocky.io/v2/5cc8019d300000980a055e76"
                  :before-upload="beforeUpload" @change="handleChange">
          <img v-if="imageUrl" :src="imageUrl" alt="avatar"/>
          <div v-else>
            <loading-outlined v-if="loading"></loading-outlined>
            <plus-outlined v-else></plus-outlined>
            <div class="ant-upload-text">请上传古树名木图片</div>
          </div>
        </a-upload>
        <!-- 上传图片示例 -->
        <!-- 提交按钮 -->
        <a-button type="primary" class="submit-btn" @click="submit">点击上传</a-button>
      </div>
    </a-card>

    <!-- todo: 添加组件 -->
    <!-- 图片管理 -->
    <a-card :title="cardTitle" class="card-half-style" v-if="props.imageType === 'imageManage'">
      <template #extra><a href="#" @click="changeType('imageUpload')">{{ cardTitle }}</a></template>

      <div class="search-style">
        <div class="search-name-input">
          <a-input v-model:value="value" class="input-style" placeholder="输入树名/描述"/>
        </div>
        <a-upload v-model:file-list="fileList" name="avatar" list-type="picture-card" class="avatar-uploader"
                  :show-upload-list="false" action="https://www.mocky.io/v2/5cc8019d300000980a055e76"
                  :before-upload="beforeUpload" @change="handleChange">
          <img v-if="imageUrl" :src="imageUrl" alt="avatar"/>
          <div v-else>
            <loading-outlined v-if="loading"></loading-outlined>
            <plus-outlined v-else></plus-outlined>
            <div class="ant-upload-text">请上传</div>
          </div>
        </a-upload>

        <a-button type="primary" class="button-style">检索</a-button>
      </div>

      <a-table :dataSource="dataSource" :columns="columns">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'imgUrl'">
            <a-image
                :style="{ width: '40px', height: 'auto' }"
                :src="record.imgUrl"
                alt="图片"
            />
          </template>
        </template>
      </a-table>

    </a-card>
  </div>
</template>

<script>
import {defineComponent, ref} from 'vue';
import {message} from 'ant-design-vue';

export default defineComponent({
  name: 'ImagePage',
  props: {
    imageType: {
      type: String,
      default: 'imageUpload'
    }
  },
  emits: ['changeType'],
  // 监听页面状态变化
  watch: {
    imageType(val) {
      if (val === 'imageManage') {
        // this.cardTitle = '古树名木图片管理';
        this.cardTitle = '古树名木图片上传';
      } else {
        this.cardTitle = '古树名木图片上传';
      }
    },
    value(val) {
      console.log(val)
    }

  },
  setup(props, {emit}) {
    const cardTitle = ref('古树名木数据上传'); // 卡片标题
    const changeType = (type) => {
      emit('changeType', type);
      cardTitle.value = type === 'imageUpload' ? '古树名木图片上传' : '古树名木图片上传';
    };
    // 日期处理
    const date = ref(null);
    const dateFormat = 'YYYY-MM-DD'; // 日期格式
    // 图片类型处理
    const selectType = ref(null);
    // 类型下拉列表
    const selectList = ref([
      {value: '1', label: '榕树'},
      {value: '2', label: '槐树'}
    ])
    // 图片处理逻辑 - 可复制
    const getBase64 = (img, callback) => {
      const reader = new FileReader();
      reader.addEventListener('load', () => callback(reader.result));
      reader.readAsDataURL(img);
    }
    const fileList = ref([]);
    const loading = ref(false);
    const imageUrl = ref('');
    const handleChange = info => {
      if (info.file.status === 'uploading') {
        loading.value = true;
        return;
      }
      if (info.file.status === 'done') {
        getBase64(info.file.originFileObj, base64Url => {
          imageUrl.value = base64Url;
          loading.value = false;
        });
      }
      if (info.file.status === 'error') {
        loading.value = false;
        // message.error('图片上传错误');
        message.success('图片上传成功');
      }
    };
    const beforeUpload = file => {
      const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
      if (!isJpgOrPng) {
        message.error('图片仅支持jpg或者png格式!');
      }
      const isLt2M = file.size / 1024 / 1024 < 3;
      if (!isLt2M) {
        message.error('图片大小不能超过3MB!');
      }
    }
    // 提交按钮
    const submit = () => {
      message.success('图片上传成功');
    }
    const dataSource = [
      {
        key: '1',
        name: '榕树',
        age: 45,
        height: '980cm',
        imgUrl: require('../assets/tree1.jpg'),
      },
      {
        key: '2',
        name: '槐树',
        age: 9,
        height: '420cm',
        imgUrl: require('../assets/tree2.jpg'),
      },
    ];

    const columns = [
      {
        title: '树名',
        dataIndex: 'name',
        key: 'name',
      },
      {
        title: '树龄',
        dataIndex: 'age',
        key: 'age',
      },
      {
        title: '树高',
        dataIndex: 'height',
        key: 'height',
      },
      {
        title: '图片',
        dataIndex: 'imgUrl',
        key: 'imgUrl',
        scopedSlots: {customRender: 'imgUrl'}, // 使用插槽
      },
    ];

    const value = ref('');

    return {
      props,
      cardTitle,
      loading,
      imageUrl,
      fileList,
      date,
      dateFormat,
      selectType,
      selectList,
      beforeUpload,
      handleChange,
      getBase64,
      changeType,
      submit,
      dataSource,
      columns,
      value
    };
  }
});
</script>

<style scoped>
.card-style {
  height: 400px;
}

.card-half-style {
  width: 70%;

}

.upload-content {
  display: flex;
  flex-direction: column;
}

.antd-select {
  margin-bottom: 20px;
}

.avatar-uploader > .ant-upload {
  width: 300px;
  height: 158px;
}

.ant-upload-select-picture-card i {
  font-size: 28px;
  color: #999;
}

.ant-upload-select-picture-card .ant-upload-text {
  margin-top: 8px;
  color: #666;
}

.submit-btn {
  margin-top: 20px;
  width: 300px;
}

.search-style {
  align-items: center;
  justify-content: center;
  display: flex;
  width: 100%;
}

.input-style {
  height: 50px;
  margin-right: 50px;
  width: 500px;

}

.search-name-input {

  display: flex;

}

.button-style {

  margin-right: 450px;

}
</style>