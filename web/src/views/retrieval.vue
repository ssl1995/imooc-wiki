<template>
  <div class="retrieval-container">
    <!-- 页面头部 -->
    <div class="retrieval-header">
      <h1 class="title">
        <TreeOutlined /> 古树名木多模态检索系统
      </h1>
      <p class="subtitle">融合图像与地理位置元数据的智能检索</p>
    </div>

    <!-- 检索模式切换 -->
    <div class="mode-tabs">
      <a-radio-group v-model:value="currentMode" button-style="solid" size="large">
        <a-radio-button value="i2i">
          <PictureOutlined /> 以图搜图
        </a-radio-button>
        <a-radio-button value="i2l">
          <FileImageOutlined /> 以图搜位置
        </a-radio-button>
        <a-radio-button value="l2i">
          <EnvironmentOutlined /> 以位置搜图
        </a-radio-button>
      </a-radio-group>
    </div>

    <!-- I2I: 以图搜图 -->
    <div v-if="currentMode === 'i2i'" class="search-section">
      <a-row :gutter="24">
        <a-col :span="8">
          <a-card title="上传查询图像" class="query-card">
            <a-upload-dragger
              v-model:fileList="i2iFileList"
              name="file"
              :multiple="false"
              :customRequest="customRequest"
              @change="handleI2IChange"
              class="query-uploader"
            >
              <p class="ant-upload-drag-icon">
                <CameraOutlined />
              </p>
              <p class="ant-upload-text">点击或拖拽上传古树图片</p>
              <p class="ant-upload-hint">支持 JPG/PNG 格式，建议分辨率 ≥ 512×512</p>
            </a-upload-dragger>
            
            <!-- 预览 -->
            <div v-if="i2iImageUrl" class="query-preview">
              <a-image :src="i2iImageUrl" :width="200" class="preview-img" />
            </div>

            <!-- 检索参数 -->
            <a-divider orientation="left">检索参数</a-divider>
            <a-form :model="i2iParams" layout="vertical">
              <a-form-item label="返回结果数">
                <a-slider v-model:value="i2iParams.topK" :min="5" :max="50" :marks="{5: '5', 20: '20', 50: '50'}" />
              </a-form-item>
              <a-form-item label="相似度阈值">
                <a-slider v-model:value="i2iParams.threshold" :min="0.5" :max="1" :step="0.05" :marks="{0.5: '0.5', 0.75: '0.75', 1: '1.0'}" />
              </a-form-item>
            </a-form>

            <a-button type="primary" size="large" block @click="handleI2ISearch" :loading="searching">
              <SearchOutlined /> 开始检索
            </a-button>
          </a-card>
        </a-col>

        <a-col :span="16">
          <a-card title="检索结果" class="result-card">
            <div v-if="!i2iResults.length" class="empty-result">
              <a-empty description="请上传图片开始检索" />
            </div>
            <div v-else class="result-grid">
              <a-row :gutter="[16, 16]">
                <a-col :span="8" v-for="(item, index) in i2iResults" :key="index">
                  <a-card hoverable class="result-item">
                    <template #cover>
                      <img :src="item.image" class="result-image" />
                    </template>
                    <a-card-meta>
                      <template #title>
                        <div class="result-title">
                          {{ item.name }}
                          <a-tag :color="getSimilarityColor(item.similarity)">
                            {{ (item.similarity * 100).toFixed(1) }}%
                          </a-tag>
                        </div>
                      </template>
                      <template #description>
                        <div class="result-info">
                          <p><EnvironmentOutlined /> {{ item.location }}</p>
                          <p><CalendarOutlined /> 树龄: {{ item.age }}年</p>
                        </div>
                      </template>
                    </a-card-meta>
                  </a-card>
                </a-col>
              </a-row>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>

    <!-- I2L: 以图搜位置 -->
    <div v-if="currentMode === 'i2l'" class="search-section">
      <a-row :gutter="24">
        <a-col :span="8">
          <a-card title="上传查询图像" class="query-card">
            <a-upload-dragger
              v-model:fileList="i2lFileList"
              name="file"
              :multiple="false"
              :customRequest="customRequest"
              @change="handleI2LChange"
              class="query-uploader"
            >
              <p class="ant-upload-drag-icon">
                <CameraOutlined />
              </p>
              <p class="ant-upload-text">点击或拖拽上传古树图片</p>
              <p class="ant-upload-hint">系统将返回该古树最可能的地理位置</p>
            </a-upload-dragger>
            
            <div v-if="i2lImageUrl" class="query-preview">
              <a-image :src="i2lImageUrl" :width="200" class="preview-img" />
            </div>

            <a-button type="primary" size="large" block @click="handleI2LSearch" :loading="searching">
              <SearchOutlined /> 定位查询
            </a-button>
          </a-card>
        </a-col>

        <a-col :span="16">
          <a-card title="定位结果" class="result-card">
            <div v-if="!i2lResult" class="empty-result">
              <a-empty description="请上传图片进行定位" />
            </div>
            <div v-else class="location-result">
              <a-descriptions bordered :column="2">
                <a-descriptions-item label="检索位置" :span="2">
                  <span class="location-text">
                    <EnvironmentOutlined /> 纬度: {{ i2lResult.latitude }}°, 经度: {{ i2lResult.longitude }}°
                  </span>
                </a-descriptions-item>
                <a-descriptions-item label="置信度">
                  <a-progress 
                    :percent="i2lResult.confidence * 100" 
                    :status="i2lResult.confidence > 0.8 ? 'success' : 'normal'"
                    :stroke-color="i2lResult.confidence > 0.8 ? '#52c41a' : '#1890ff'"
                  />
                </a-descriptions-item>
                <a-descriptions-item label="误差范围">
                  <span class="error-range">±{{ i2lResult.error }} km</span>
                </a-descriptions-item>
                <a-descriptions-item label="地址描述" :span="2">
                  {{ i2lResult.address }}
                </a-descriptions-item>
              </a-descriptions>

              <!-- 地图占位区域 -->
              <div class="map-container">
                <div class="map-placeholder">
                  <GlobalOutlined class="map-icon" />
                  <p>地图显示区域</p>
                  <p class="map-hint">此处后续将迭代显示定位结果在地图上的位置</p>
                </div>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>

    <!-- L2I: 以位置搜图 -->
    <div v-if="currentMode === 'l2i'" class="search-section">
      <a-row :gutter="24">
        <a-col :span="8">
          <a-card title="输入地理位置" class="query-card">
            <a-form :model="l2iParams" layout="vertical">
              <a-form-item label="纬度 (Latitude)" required>
                <a-input
                  v-model:value="l2iLatitudeStr"
                  style="width: 100%"
                  placeholder="例如: 39.9289"
                />
              </a-form-item>
              <a-form-item label="经度 (Longitude)" required>
                <a-input
                  v-model:value="l2iLongitudeStr"
                  style="width: 100%"
                  placeholder="例如: 116.3974"
                />
              </a-form-item>
              
              <a-divider orientation="left">检索范围</a-divider>
              
              <a-form-item label="搜索半径">
                <a-radio-group v-model:value="l2iParams.radius">
                  <a-radio :value="1">&lt; 1 km</a-radio>
                  <a-radio :value="5">&lt; 5 km</a-radio>
                  <a-radio :value="10">&lt; 10 km</a-radio>
                  <a-radio :value="50">&lt; 50 km</a-radio>
                </a-radio-group>
              </a-form-item>

              <a-form-item label="返回结果数">
                <a-slider v-model:value="l2iParams.topK" :min="5" :max="30" />
              </a-form-item>
            </a-form>

            <a-button type="primary" size="large" block @click="handleL2ISearch" :loading="searching">
              <SearchOutlined /> 检索附近古树
            </a-button>

            <!-- 快速选择 -->
            <a-divider orientation="left">快速选择</a-divider>
            <a-space wrap>
              <a-button size="small" @click="setLocation(39.8833, 116.4069)">天坛</a-button>
              <a-button size="small" @click="setLocation(31.2304, 121.4737)">上海</a-button>
              <a-button size="small" @click="setLocation(30.5728, 104.0668)">成都</a-button>
              <a-button size="small" @click="setLocation(23.1291, 113.2644)">广州</a-button>
            </a-space>
          </a-card>
        </a-col>

        <a-col :span="16">
          <a-card title="附近古树检索结果" class="result-card">
            <div v-if="!l2iResults.length" class="empty-result">
              <a-empty description="请输入坐标开始检索" />
            </div>
            <div v-else>
              <a-alert 
                :message="`在指定位置周围找到 ${l2iResults.length} 棵古树`" 
                type="info" 
                show-icon
                style="margin-bottom: 16px"
              />
              <div class="result-grid">
                <a-row :gutter="[16, 16]">
                  <a-col :span="8" v-for="(item, index) in l2iResults" :key="index">
                    <a-card hoverable class="result-item">
                      <template #cover>
                        <img :src="item.image" class="result-image" />
                      </template>
                      <a-card-meta>
                        <template #title>
                          <div class="result-title">
                            {{ item.name }}
                            <a-tag color="blue">{{ item.distance }}km</a-tag>
                          </div>
                        </template>
                        <template #description>
                          <div class="result-info">
                            <p><EnvironmentOutlined /> {{ item.location }}</p>
                            <p><CalendarOutlined /> 树龄: {{ item.age }}年</p>
                          </div>
                        </template>
                      </a-card-meta>
                    </a-card>
                  </a-col>
                </a-row>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, reactive, onMounted } from 'vue';
import { message } from 'ant-design-vue';
import {
  PictureOutlined,
  FileImageOutlined,
  EnvironmentOutlined,
  CameraOutlined,
  SearchOutlined,
  CalendarOutlined,
  GlobalOutlined,
  PartitionOutlined
} from '@ant-design/icons-vue';
import type { UploadChangeParam } from 'ant-design-vue';

interface I2IResult {
  name: string;
  image: string;
  similarity: number;
  location: string;
  age: number;
}

interface I2LResult {
  latitude: number;
  longitude: number;
  confidence: number;
  error: number;
  address: string;
}

interface L2IResult {
  name: string;
  image: string;
  location: string;
  age: number;
  distance: number;
}

export default defineComponent({
  name: 'RetrievalPage',
  components: {
    PictureOutlined,
    FileImageOutlined,
    EnvironmentOutlined,
    CameraOutlined,
    SearchOutlined,
    CalendarOutlined,
    GlobalOutlined,
    PartitionOutlined
  },
  setup() {
    // 当前检索模式
    const currentMode = ref<'i2i' | 'i2l' | 'l2i'>('i2i');
    const searching = ref(false);

    // I2I 状态
    const i2iFileList = ref<any[]>([]);
    const i2iImageUrl = ref('');
    const i2iParams = reactive({
      topK: 20,
      threshold: 0.7
    });
    const i2iResults = ref<I2IResult[]>([]);

    // I2L 状态
    const i2lFileList = ref<any[]>([]);
    const i2lImageUrl = ref('');
    const i2lResult = ref<I2LResult | null>(null);

    // L2I 状态
    const l2iParams = reactive({
      latitude: null as number | null,
      longitude: null as number | null,
      radius: 5,
      topK: 15
    });
    const l2iLatitudeStr = ref('');
    const l2iLongitudeStr = ref('');
    const l2iResults = ref<L2IResult[]>([]);

    // 处理I2I上传
    const handleI2IChange = (info: UploadChangeParam) => {
      if (info.file.status === 'done') {
        const reader = new FileReader();
        reader.onload = (e) => {
          i2iImageUrl.value = e.target?.result as string;
        };
        if (info.file.originFileObj) {
          reader.readAsDataURL(info.file.originFileObj);
        }
        message.success('图片上传成功');
      }
    };

    // 处理I2L上传
    const handleI2LChange = (info: UploadChangeParam) => {
      if (info.file.status === 'done') {
        const reader = new FileReader();
        reader.onload = (e) => {
          i2lImageUrl.value = e.target?.result as string;
        };
        if (info.file.originFileObj) {
          reader.readAsDataURL(info.file.originFileObj);
        }
        message.success('图片上传成功');
      }
    };

    // I2I检索
    const handleI2ISearch = () => {
      if (!i2iImageUrl.value) {
        message.error('请先上传图片');
        return;
      }
      searching.value = true;
      setTimeout(() => {
        i2iResults.value = generateMockI2IResults();
        searching.value = false;
        message.success('检索完成');
      }, 1500);
    };

    // I2L检索
    const handleI2LSearch = () => {
      if (!i2lImageUrl.value) {
        message.error('请先上传图片');
        return;
      }
      searching.value = true;
      setTimeout(() => {
        i2lResult.value = {
          latitude: 39.9289,
          longitude: 116.3974,
          confidence: 0.92,
          error: 0.5,
          address: '北京市东城区景山公园万春亭'
        };
        searching.value = false;
        message.success('定位完成');
      }, 1500);
    };

    // L2I检索
    const handleL2ISearch = () => {
      const lat = parseFloat(l2iLatitudeStr.value);
      const lon = parseFloat(l2iLongitudeStr.value);
      if (isNaN(lat) || isNaN(lon)) {
        message.error('请输入有效的经纬度坐标');
        return;
      }
      l2iParams.latitude = lat;
      l2iParams.longitude = lon;
      searching.value = true;
      setTimeout(() => {
        l2iResults.value = generateMockL2IResults();
        searching.value = false;
        message.success('检索完成');
      }, 1500);
    };

    // 设置位置
    const setLocation = (lat: number, lon: number) => {
      l2iParams.latitude = lat;
      l2iParams.longitude = lon;
      l2iLatitudeStr.value = lat.toString();
      l2iLongitudeStr.value = lon.toString();
      message.success('已设置坐标');
    };

    // 获取相似度颜色
    const getSimilarityColor = (similarity: number) => {
      if (similarity >= 0.9) return 'green';
      if (similarity >= 0.8) return 'cyan';
      if (similarity >= 0.7) return 'blue';
      return 'orange';
    };

    // 本地模拟上传
    const customRequest = (options: any) => {
      const { file, onSuccess, onProgress } = options;
      let percent = 0;
      const interval = setInterval(() => {
        percent += 20;
        if (onProgress) onProgress({ percent });
        if (percent >= 100) {
          clearInterval(interval);
          setTimeout(() => {
            const url = URL.createObjectURL(file);
            if (onSuccess) onSuccess({ url, thumbUrl: url });
          }, 200);
        }
      }, 50);
    };

    // 生成模拟I2I结果（6张卡片，使用不重复图片，统一为柏树）
    const generateMockI2IResults = (): I2IResult[] => {
      return [
        {
          name: '景山万春亭古柏',
          image: require('@/assets/tree1.jpg'),
          similarity: 0.945,
          location: '北京市景山公园',
          age: 500
        },
        {
          name: '天坛九龙柏',
          image: require('@/assets/tree2.jpg'),
          similarity: 0.892,
          location: '北京市天坛公园',
          age: 600
        },
        {
          name: '颐和园佛香阁古柏',
          image: require('@/assets/tree3.jpg'),
          similarity: 0.857,
          location: '北京市颐和园',
          age: 400
        },
        {
          name: '北海团城古柏',
          image: require('@/assets/tree4.jpg'),
          similarity: 0.823,
          location: '北京市北海公园',
          age: 600
        },
        {
          name: '中山公园古柏',
          image: require('@/assets/tree5.jpg'),
          similarity: 0.786,
          location: '北京市中山公园',
          age: 400
        },
        {
          name: '圆明园古柏',
          image: require('@/assets/upload.jpg'),
          similarity: 0.751,
          location: '北京市圆明园',
          age: 300
        }
      ];
    };

    // 生成模拟L2I结果（距离标签，展示天坛九龙柏，与I2I结果区分开）
    const generateMockL2IResults = (): L2IResult[] => {
      return [
        {
          name: '天坛九龙柏',
          image: require('@/assets/tree2.jpg'),
          location: '天坛公园回音壁西北侧',
          age: 600,
          distance: 0.2
        }
      ];
    };

    // 生成模拟I2L结果
    const generateMockI2LResult = (): I2LResult => {
      return {
        latitude: 39.9289,
        longitude: 116.3974,
        confidence: 0.92,
        error: 0.5,
        address: '北京市东城区景山公园万春亭'
      };
    };

    // 默认加载模拟数据（页面加载时显示）
    onMounted(() => {
      // I2I模式默认加载6张模拟结果
      i2iResults.value = generateMockI2IResults();
      
      // I2L模式默认加载模拟定位结果
      i2lResult.value = generateMockI2LResult();
      
      // L2I模式默认加载模拟结果
      l2iResults.value = generateMockL2IResults();
    });

    return {
      currentMode,
      searching,
      i2iFileList,
      i2iImageUrl,
      i2iParams,
      i2iResults,
      i2lFileList,
      i2lImageUrl,
      i2lResult,
      l2iParams,
      l2iLatitudeStr,
      l2iLongitudeStr,
      l2iResults,
      customRequest,
      handleI2IChange,
      handleI2LChange,
      handleI2ISearch,
      handleI2LSearch,
      handleL2ISearch,
      setLocation,
      getSimilarityColor
    };
  }
});
</script>

<style scoped>
.retrieval-container {
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: calc(100vh - 64px);
}

.retrieval-header {
  text-align: center;
  margin-bottom: 32px;
}

.title {
  font-size: 28px;
  color: #1a1a1a;
  margin-bottom: 8px;
  font-weight: 600;
}

.subtitle {
  font-size: 14px;
  color: #666;
}

.mode-tabs {
  text-align: center;
  margin-bottom: 24px;
}

.search-section {
  max-width: 1400px;
  margin: 0 auto;
}

.query-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.result-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  min-height: 600px;
}

.query-uploader {
  padding: 20px 0;
}

.query-preview {
  text-align: center;
  margin: 16px 0;
  padding: 16px;
  background: #f6ffed;
  border-radius: 8px;
  border: 1px dashed #b7eb8f;
}

.preview-img {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.empty-result {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
}

.result-grid {
  padding: 8px;
}

.result-item {
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
}

.result-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.result-image {
  width: 100%;
  height: 160px;
  object-fit: cover;
}

.result-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.result-info {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}

.result-info p {
  margin: 2px 0;
}

.location-result {
  padding: 16px;
}

.location-text {
  font-size: 16px;
  color: #1890ff;
  font-weight: 500;
}

.error-range {
  color: #52c41a;
  font-weight: 500;
}

.map-container {
  margin-top: 24px;
  height: 300px;
  background: #f0f2f5;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.map-placeholder {
  text-align: center;
  color: #999;
}

.map-icon {
  font-size: 48px;
  margin-bottom: 8px;
}

.map-hint {
  font-size: 12px;
  color: #bbb;
}

:deep(.ant-radio-button-wrapper) {
  padding: 0 24px;
  height: 44px;
  line-height: 42px;
  font-size: 14px;
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
