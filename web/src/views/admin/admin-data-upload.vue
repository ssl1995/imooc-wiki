<template>
  <div class="data-upload-container">
    <a-page-header
      title="古树名木数据上传"
      sub-title="管理员数据录入界面"
      :back-icon="false"
    />
    
    <a-row :gutter="24" class="upload-content">
      <!-- 左侧：表单区域 -->
      <a-col :span="14">
        <a-card title="基础信息录入" class="info-card">
          <a-form :model="formState" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
            <!-- 古树名称 -->
            <a-form-item label="古树名称" required>
              <a-input 
                v-model:value="formState.treeName" 
                placeholder="请输入古树名称"
                prefix="<FileImageOutlined />"
              />
            </a-form-item>
            
            <!-- 物种分类 -->
            <a-form-item label="物种分类" required>
              <a-select 
                v-model:value="formState.species" 
                placeholder="请选择物种"
                :options="speciesOptions"
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
            
            <!-- 保护级别 -->
            <a-form-item label="保护级别">
              <a-radio-group v-model:value="formState.protectionLevel">
                <a-radio value="一级">一级</a-radio>
                <a-radio value="二级">二级</a-radio>
                <a-radio value="三级">三级</a-radio>
              </a-radio-group>
            </a-form-item>
            
            <!-- 拍摄日期 -->
            <a-form-item label="拍摄日期">
              <a-date-picker 
                v-model:value="formState.photoDate" 
                style="width: 100%"
                placeholder="请选择拍摄日期"
              />
            </a-form-item>
            
            <!-- 备注 -->
            <a-form-item label="备注">
              <a-textarea 
                v-model:value="formState.remark" 
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
            action="https://www.mocky.io/v2/5cc8019d300000980a055e76"
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
import {
  EnvironmentOutlined,
  InboxOutlined,
  DeleteOutlined,
  UploadOutlined,
  ReloadOutlined,
  FileImageOutlined
} from '@ant-design/icons-vue';
import type { UploadChangeParam } from 'ant-design-vue';

interface FormState {
  treeName: string;
  species: string | undefined;
  latitude: number | null;
  longitude: number | null;
  age: number | null;
  height: number | null;
  protectionLevel: string;
  photoDate: any;
  remark: string;
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
      treeName: '',
      species: undefined,
      latitude: null,
      longitude: null,
      age: null,
      height: null,
      protectionLevel: '一级',
      photoDate: null,
      remark: ''
    });

    // 物种选项
    const speciesOptions = [
      { value: '银杏', label: '银杏' },
      { value: '柏树', label: '柏树' },
      { value: '松树', label: '松树' },
      { value: '槐树', label: '槐树' },
      { value: '榕树', label: '榕树' },
      { value: '樟树', label: '樟树' },
      { value: '其他', label: '其他' }
    ];

    // 文件列表
    const fileList = ref<any[]>([]);
    const submitting = ref(false);

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

    // 移除文件
    const removeFile = (index: number) => {
      fileList.value.splice(index, 1);
      message.success('已移除图片');
    };

    // 提交表单
    const handleSubmit = () => {
      if (!formState.treeName || !formState.species || !formState.latitude || !formState.longitude) {
        message.error('请填写必填项');
        return;
      }
      if (fileList.value.length === 0) {
        message.error('请至少上传一张图片');
        return;
      }
      
      submitting.value = true;
      setTimeout(() => {
        submitting.value = false;
        message.success('数据上传成功');
      }, 1500);
    };

    // 重置表单
    const handleReset = () => {
      formState.treeName = '';
      formState.species = undefined;
      formState.latitude = null;
      formState.longitude = null;
      formState.age = null;
      formState.height = null;
      formState.protectionLevel = '一级';
      formState.photoDate = null;
      formState.remark = '';
      fileList.value = [];
      message.success('表单已重置');
    };

    return {
      formState,
      speciesOptions,
      fileList,
      submitting,
      handleChange,
      handleDrop,
      removeFile,
      handleSubmit,
      handleReset
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
