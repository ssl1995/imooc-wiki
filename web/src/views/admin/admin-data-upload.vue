<template>
  <div class="data-upload-container">
    <a-row :gutter="24" class="upload-content">
      <!-- 左侧：表单区域 -->
      <a-col :span="14">
        <a-card title="信息录入" class="info-card">
          <a-form :model="formState" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
            <!-- 古树名称 -->
            <a-form-item label="古树名称" required>
              <a-input 
                v-model:value="formState.name" 
                placeholder="请输入古树名称"
              >
                <template #prefix>
                  <FileImageOutlined />
                </template>
              </a-input>
            </a-form-item>
            
            <!-- 物种分类 -->
            <a-form-item label="物种分类" required>
              <a-input 
                v-model:value="formState.species" 
                placeholder="请输入树种名称，如：银杏、侧柏、国槐等"
              />
            </a-form-item>
            
            <!-- 地理位置 -->
            <a-form-item label="地理位置" required>
              <a-row :gutter="8">
                <a-col :span="12">
                  <a-input-number 
                    v-model:value="formState.latitude" 
                    placeholder="纬度"
                    :precision="6"
                    :min="-90"
                    :max="90"
                    style="width: 100%"
                  />
                </a-col>
                <a-col :span="12">
                  <a-input-number 
                    v-model:value="formState.longitude" 
                    placeholder="经度"
                    :precision="6"
                    :min="-180"
                    :max="180"
                    style="width: 100%"
                  />
                </a-col>
              </a-row>
              <div class="coordinate-hint">
                <EnvironmentOutlined /> 示例：纬度 39.9042°N，经度 116.4074°E
              </div>
            </a-form-item>
            
            <!-- 树龄 -->
            <a-form-item label="树龄">
              <a-input-number 
                v-model:value="formState.age" 
                placeholder="请输入树龄"
                :min="0"
                addon-after="年"
                style="width: 100%"
              />
            </a-form-item>
            
            <!-- 树高 -->
            <a-form-item label="树高">
              <a-input-number 
                v-model:value="formState.height" 
                placeholder="请输入树高"
                :min="0"
                :precision="2"
                addon-after="米"
                style="width: 100%"
              />
            </a-form-item>
            
            <!-- 备注 -->
            <a-form-item label="备注">
              <a-textarea 
                v-model:value="formState.desc" 
                :rows="3"
                placeholder="请输入备注信息"
              />
            </a-form-item>
          </a-form>
        </a-card>
      </a-col>
      
      <!-- 右侧：图片上传区域 -->
      <a-col :span="10">
        <a-card title="图像上传" class="upload-card">
          <a-upload-dragger
            v-model:fileList="fileList"
            name="file"
            :multiple="true"
            :customRequest="customRequest"
            @change="handleChange"
            @drop="handleDrop"
            class="upload-dragger"
          >
            <p class="ant-upload-drag-icon">
              <InboxOutlined />
            </p>
            <p class="ant-upload-text">点击或拖拽文件到此区域上传</p>
            <p class="ant-upload-hint">
              支持单张或多张图片上传，仅支持 JPG/PNG 格式，单张不超过 5MB
            </p>
          </a-upload-dragger>
          
          <!-- 预览区域 -->
          <div class="preview-section" v-if="fileList.length > 0">
            <a-divider orientation="left">图片预览</a-divider>
            <a-image-preview-group>
              <a-space wrap>
                <div v-for="(file, index) in fileList" :key="index" class="preview-item">
                  <a-image
                    :src="file.thumbUrl || file.url"
                    :width="100"
                    :height="100"
                    class="preview-image"
                  />
                  <a-button 
                    type="text" 
                    danger 
                    size="small"
                    @click="removeFile(index)"
                    class="remove-btn"
                  >
                    <DeleteOutlined />
                  </a-button>
                </div>
              </a-space>
            </a-image-preview-group>
          </div>
        </a-card>
        
        <!-- 提交按钮区域 -->
        <a-card class="submit-card">
          <a-space size="large">
            <a-button type="primary" size="large" @click="handleSubmit" :loading="submitting">
              <UploadOutlined /> 提交数据
            </a-button>
            <a-button size="large" @click="handleReset">
              <ReloadOutlined /> 重置表单
            </a-button>
          </a-space>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, ref } from 'vue';
import { message } from 'ant-design-vue';
import axios from 'axios';
import {
  EnvironmentOutlined,
  InboxOutlined,
  DeleteOutlined,
  UploadOutlined,
  ReloadOutlined,
  FileImageOutlined
} from '@ant-design/icons-vue';
import type { UploadChangeParam } from 'ant-design-vue';

const API_BASE = process.env.VUE_APP_SERVER || 'http://localhost:8099';

interface FormState {
  name: string;
  species: string | undefined;
  latitude: number | null;
  longitude: number | null;
  age: number | null;
  height: number | null;
  desc: string;
}

export default defineComponent({
  name: 'AdminDataUpload',
  components: {
    EnvironmentOutlined,
    InboxOutlined,
    DeleteOutlined,
    UploadOutlined,
    ReloadOutlined,
    FileImageOutlined
  },
  setup() {
    // 表单状态
    const formState = reactive<FormState>({
      name: '',
      species: undefined,
      latitude: null,
      longitude: null,
      age: null,
      height: null,
      desc: ''
    });

    // 物种输入为自由文本，系统支持论文所述 iNaturalist 36 个科属类别下的任意树种录入

    // 文件列表
    const fileList = ref<any[]>([]);
    const submitting = ref(false);

    // 自定义上传请求 - 调用后端 /tree/upload
    const customRequest = (options: any) => {
      const { file, onSuccess, onError, onProgress } = options;

      // 创建本地 blob URL 用于预览（上传前即可预览）
      const rawFile = file.originFileObj || file;
      const blobUrl = URL.createObjectURL(rawFile);
      file.url = blobUrl;
      file.thumbUrl = blobUrl;

      const formData = new FormData();
      formData.append('file', rawFile);

      axios.post(`${API_BASE}/tree/upload`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
        onUploadProgress: (e: any) => {
          if (onProgress) {
            onProgress({ percent: Math.round((e.loaded * 100) / e.total) });
          }
        }
      }).then((res) => {
        const data = res.data;
        if (data.success) {
          file.response = { serverFileName: data.content.message };
          if (onSuccess) {
            onSuccess(file.response);
          }
        } else {
          if (onError) onError(new Error(data.message || '上传失败'));
        }
      }).catch((err) => {
        if (onError) onError(err);
      });
    };

    // 处理文件变化
    const handleChange = (info: UploadChangeParam) => {
      const { status } = info.file;
      if (status === 'done') {
        message.success(`${info.file.name} 上传成功`);
      } else if (status === 'error') {
        message.error(`${info.file.name} 上传失败`);
      }
    };

    // 处理拖拽
    const handleDrop = (e: DragEvent) => {
      console.log('Dropped files', e.dataTransfer?.files);
    };

    // 释放所有 blob URL（组件卸载或重置时调用）
    const revokeBlobUrls = () => {
      fileList.value.forEach((f: any) => {
        if (f.url && f.url.startsWith('blob:')) {
          URL.revokeObjectURL(f.url);
        }
      });
    };

    // 移除文件
    const removeFile = (index: number) => {
      const file = fileList.value[index];
      if (file.url && file.url.startsWith('blob:')) {
        URL.revokeObjectURL(file.url);
      }
      fileList.value.splice(index, 1);
      message.success('已移除图片');
    };

    // 提交表单
    const handleSubmit = () => {
      if (!formState.name || !formState.species || !formState.latitude || !formState.longitude) {
        message.error('请填写必填项');
        return;
      }
      if (fileList.value.length === 0) {
        message.error('请至少上传一张图片');
        return;
      }

      // 提取上传成功的图片文件名
      const imageList = fileList.value
        .filter((f: any) => f.status === 'done' && f.response && f.response.serverFileName)
        .map((f: any) => f.response.serverFileName);

      if (imageList.length === 0) {
        message.error('图片尚未上传完成，请稍后再试');
        submitting.value = false;
        return;
      }

      submitting.value = true;
      const payload = {
        name: formState.name,
        species: formState.species,
        latitude: formState.latitude,
        longitude: formState.longitude,
        age: formState.age,
        height: formState.height ? formState.height + '米' : null,
        desc: formState.desc,
        imageList: imageList
      };
      axios.post(`${API_BASE}/tree/save`, payload).then((res) => {
        submitting.value = false;
        const data = res.data;
        if (data.success) {
          message.success(`数据上传成功，古树编号：${data.content.treeCode}`);
        } else {
          message.error(data.message || '保存失败');
        }
      }).catch(() => {
        submitting.value = false;
        message.error('网络错误，保存失败');
      });
    };

    // 重置表单
    const handleReset = () => {
      revokeBlobUrls();
      formState.name = '';
      formState.species = undefined;
      formState.latitude = null;
      formState.longitude = null;
      formState.age = null;
      formState.height = null;
      formState.desc = '';
      fileList.value = [];
      message.success('表单已重置');
    };

    return {
      formState,
      fileList,
      submitting,
      handleChange,
      handleDrop,
      removeFile,
      handleSubmit,
      handleReset,
      customRequest
    };
  }
});
</script>

<style scoped>
.data-upload-container {
  padding: 24px;
  background: #f0f2f5;
  min-height: calc(100vh - 64px);
}

.upload-content {
  margin-top: 16px;
}

.info-card {
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.upload-card {
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  margin-bottom: 16px;
}

.upload-dragger {
  padding: 20px 0;
}

.coordinate-hint {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.preview-section {
  margin-top: 16px;
}

.preview-item {
  position: relative;
  display: inline-block;
  margin: 4px;
}

.preview-image {
  border-radius: 4px;
  object-fit: cover;
}

.remove-btn {
  position: absolute;
  top: -8px;
  right: -8px;
  background: #fff;
  border-radius: 50%;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.submit-card {
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  text-align: center;
}

:deep(.ant-upload-drag-icon) {
  color: #1890ff;
  font-size: 48px;
}

:deep(.ant-upload-text) {
  font-size: 16px;
  color: #333;
  margin: 8px 0;
}

:deep(.ant-upload-hint) {
  font-size: 12px;
  color: #999;
}
</style>
