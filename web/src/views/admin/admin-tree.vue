<template>
    <div>
        <!-- 图片上传 -->
        <a-card :title="cardTitle" class="card-style" v-if="props.imageType === 'imageUpload'">
            <template #extra><a href="#" @click="changeType('imageManage')">{{ cardTitle }}</a></template>
            <div class="upload-content">
                <!-- 选择日期 -->
                <a-date-picker v-model="date" placeholder="请选择图片上传日期" class="antd-select" :format="dateFormat" />
                <!-- 选择图片类型 -->
                <a-select v-model="selectType" class="antd-select" placeholder="请选择图片类型">
                    <a-select-option v-for="(item, index) in selectList" :key="index" :value="item.value">{{ item.label
                        }}</a-select-option>
                </a-select>
                <!-- 上传图片区 -->
                <a-upload v-model:file-list="fileList" name="avatar" list-type="picture-card" class="avatar-uploader"
                    :show-upload-list="false" action="https://www.mocky.io/v2/5cc8019d300000980a055e76"
                    :before-upload="beforeUpload" @change="handleChange">
                    <img v-if="imageUrl" :src="imageUrl" alt="avatar" />
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
        <!-- 图片管理 -->
        <a-card :title="cardTitle" class="card-style" v-if="props.imageType === 'imageManage'">
            <template #extra><a href="#" @click="changeType('imageUpload')">{{ cardTitle }}</a></template>
            <!-- todo: 添加组件 -->
            <p>图片管理2</p>
            <p>图片管理1</p>
        </a-card>
    </div>
</template>

<script>
import { defineComponent, ref } from 'vue';
import { message } from 'ant-design-vue';

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
                this.cardTitle = '古树名木数据管理';
            } else {
                this.cardTitle = '古树名木数据上传';
            }
        }
    },
    setup(props, { emit }) {
        const cardTitle = ref('古树名木图片上传'); // 卡片标题
        const changeType = (type) => {
            emit('changeType', type);
            cardTitle.value = type === 'imageUpload' ? '古树名木图片上传' : '古树名木图片管理'; // 修改卡片标题
        };
        // 日期处理
        const date = ref(null);
        const dateFormat = 'YYYY-MM-DD'; // 日期格式
        // 图片类型处理
        const selectType = ref(null);
        // 类型下拉列表
        const selectList = ref([
            { value: '1', label: '古树名木1' },
            { value: '2', label: '古树名木2' },
            { value: '3', label: '古树名木3' },
            { value: '4', label: '古树名木4' }
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
                message.error('图片上传错误');
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
            submit
        };
    }
});
</script>

<style scoped>
.card-style {
    width: 600px;
    height: 400px;
}

.upload-content {
    display: flex;
    flex-direction: column;
}

.antd-select {
    width: 300px;
    margin-bottom: 20px;
}

.avatar-uploader>.ant-upload {
    width: 158px;
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
</style>