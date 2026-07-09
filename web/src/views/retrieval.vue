<template>
  <div class="retrieval-container">
    <!-- 页面头部 -->
    <div class="retrieval-header">
      <h1 class="title">
        <TreeOutlined /> 古树名木多模态检索
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
              :beforeUpload="beforeUploadI2I"
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
                <a-slider v-model:value="i2iParams.topK" :min="1" :max="50" :marks="{1: '1', 5: '5', 10: '10', 20: '20', 50: '50'}" />
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
              :beforeUpload="beforeUploadI2L"
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

    <!-- NameSearch: 按树种名称检索 -->
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
                <a-slider v-model:value="l2iParams.topK" :min="1" :max="50" :marks="{1: '1', 5: '5', 10: '10', 20: '20', 50: '50'}" />
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

    <!-- Taxonomy: 科属种查询 -->
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, reactive, onMounted } from 'vue';
import { message } from 'ant-design-vue';
import axios from 'axios';
import {
  PictureOutlined,
  FileImageOutlined,
  EnvironmentOutlined,
  CameraOutlined,
  SearchOutlined,
  CalendarOutlined,
  GlobalOutlined,
  PartitionOutlined,
  ApartmentOutlined,
  InfoCircleOutlined,
  FilterOutlined
} from '@ant-design/icons-vue';
import type { UploadChangeParam } from 'ant-design-vue';

const API_BASE = process.env.VUE_APP_SERVER || 'http://localhost:8099';

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
    PartitionOutlined,
    ApartmentOutlined,
    InfoCircleOutlined,
    FilterOutlined
  },
  setup() {
    // 当前检索模式
    const currentMode = ref<'i2i' | 'i2l' | 'l2i'>('i2i');
    const searching = ref(false);

    // I2I 状态
    const i2iFileList = ref<any[]>([]);
    const i2iImageUrl = ref('');
    const i2iParams = reactive({
      topK: 6,
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
      topK: 6
    });
    const l2iLatitudeStr = ref('');
    const l2iLongitudeStr = ref('');
    const l2iResults = ref<L2IResult[]>([]);

    // NameSearch 状态
    const nameParams = reactive({
      keyword: '',
      ageRange: [0, 2000] as number[]
    });
    const nameSearching = ref(false);
    const nameResults = ref<any[]>([]);
    const nameColumns = [
      { title: '序号', dataIndex: 'id', width: 60, align: 'center' },
      { title: '古树名木名称', dataIndex: 'name', width: 160 },
      { title: '树种', dataIndex: 'species', width: 100 },
      { title: '树龄', dataIndex: 'age', width: 80, align: 'center', customRender: ({ text }: any) => text + '年' },
      { title: '位置', dataIndex: 'location' }
    ];

    // Taxonomy 状态（科属种查询）
    const taxonomyParams = reactive({
      family: '',
      genus: '',
      species: ''
    });
    const filterParams = reactive({
      protectionLevel: '',
      ageRange: [0, 2000] as number[]
    });
    const taxonomySearching = ref(false);
    const taxonomyResults = ref<any[]>([]);
    const familyList = ref<string[]>([]);
    const genusList = ref<string[]>([]);
    const speciesSelectList = ref<string[]>([]);

    // 详情抽屉状态
    const detailVisible = ref(false);
    const detailData = ref<any>(null);

    // 上传前清空旧文件和预览
    const beforeUploadI2I = () => {
      i2iFileList.value = [];
      i2iImageUrl.value = '';
      i2iResults.value = [];
      return true;
    };

    const beforeUploadI2L = () => {
      i2lFileList.value = [];
      i2lImageUrl.value = '';
      i2lResult.value = null;
      return true;
    };

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

    // I2I检索：调用后端 /tree/retrieve
    const handleI2ISearch = async () => {
      if (!i2iImageUrl.value) {
        message.error('请先上传图片');
        return;
      }
      searching.value = true;
      try {
        const formData = new FormData();
        formData.append('type', 'I2I');
        formData.append('topK', i2iParams.topK.toString());
        formData.append('threshold', i2iParams.threshold.toString());
        if (i2iFileList.value.length > 0 && i2iFileList.value[0].originFileObj) {
          formData.append('file', i2iFileList.value[0].originFileObj);
        }
        const res = await axios.post(`${API_BASE}/tree/retrieve`, formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        });
        const list = res.data.content || [];
        i2iResults.value = list.map((item: any) => ({
          name: item.name,
          image: item.image ? `${API_BASE}${item.image}` : '',
          similarity: item.similarity || 0,
          location: item.location || item.desc || '',
          age: item.age || 0
        }));
        message.success('检索完成');
      } catch (e) {
        message.error('检索失败');
        console.error(e);
      }
      searching.value = false;
    };

    // I2L检索：调用后端 /tree/retrieve
    const handleI2LSearch = async () => {
      if (!i2lImageUrl.value) {
        message.error('请先上传图片');
        return;
      }
      searching.value = true;
      try {
        const formData = new FormData();
        formData.append('type', 'I2L');
        if (i2lFileList.value.length > 0 && i2lFileList.value[0].originFileObj) {
          formData.append('file', i2lFileList.value[0].originFileObj);
        }
        const res = await axios.post(`${API_BASE}/tree/retrieve`, formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        });
        const list = res.data.content || [];
        if (list.length > 0) {
          const item = list[0];
          i2lResult.value = {
            latitude: item.latitude,
            longitude: item.longitude,
            confidence: item.confidence || 0.92,
            error: item.error || 0.5,
            address: item.location || item.name || ''
          };
        }
        message.success('定位完成');
      } catch (e) {
        message.error('定位失败');
        console.error(e);
      }
      searching.value = false;
    };

    // L2I检索：调用后端 /tree/retrieve
    const handleL2ISearch = async () => {
      const lat = parseFloat(l2iLatitudeStr.value);
      const lon = parseFloat(l2iLongitudeStr.value);
      if (isNaN(lat) || isNaN(lon)) {
        message.error('请输入有效的经纬度坐标');
        return;
      }
      l2iParams.latitude = lat;
      l2iParams.longitude = lon;
      searching.value = true;
      try {
        const formData = new FormData();
        formData.append('type', 'L3I');
        formData.append('latitude', lat.toString());
        formData.append('longitude', lon.toString());
        formData.append('radius', l2iParams.radius.toString());
        formData.append('topK', l2iParams.topK.toString());
        const res = await axios.post(`${API_BASE}/tree/retrieve`, formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        });
        const list = res.data.content || [];
        l2iResults.value = list.map((item: any) => ({
          name: item.name,
          image: item.image ? `${API_BASE}${item.image}` : '',
          location: item.location || item.desc || '',
          age: item.age || 0,
          distance: item.distance || 0
        }));
        message.success('检索完成');
      } catch (e) {
        message.error('检索失败');
        console.error(e);
      }
      searching.value = false;
    };

    // NAME检索：调用后端 /tree/retrieve
    const handleNameSearch = async () => {
      if (!nameParams.keyword.trim()) {
        message.error('请输入树种名称');
        return;
      }
      nameSearching.value = true;
      try {
        const formData = new FormData();
        formData.append('type', 'NAME');
        formData.append('speciesName', nameParams.keyword.trim());
        const res = await axios.post(`${API_BASE}/tree/retrieve`, formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        });
        nameResults.value = res.data.content || [];
        message.success('查询完成');
      } catch (e) {
        message.error('查询失败');
        console.error(e);
      }
      nameSearching.value = false;
    };

    // 设置树种名称关键词
    const setNameKeyword = (keyword: string) => {
      nameParams.keyword = keyword;
      handleNameSearch();
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

    // ==================== 科属种查询方法 ====================

    // 加载所有科
    const loadFamilies = async () => {
      try {
        const res = await axios.get(`${API_BASE}/tree/taxonomy/families`);
        familyList.value = res.data.content || [];
      } catch (e) {
        console.error('加载科列表失败', e);
      }
    };

    // 科变化时加载属
    const onFamilyChange = async (family: string) => {
      taxonomyParams.genus = '';
      taxonomyParams.species = '';
      genusList.value = [];
      speciesSelectList.value = [];
      if (!family) return;
      try {
        const res = await axios.get(`${API_BASE}/tree/taxonomy/genera`, { params: { family } });
        genusList.value = res.data.content || [];
      } catch (e) {
        console.error('加载属列表失败', e);
      }
    };

    // 属变化时加载种
    const onGenusChange = async (genus: string) => {
      taxonomyParams.species = '';
      speciesSelectList.value = [];
      if (!genus || !taxonomyParams.family) return;
      try {
        const res = await axios.get(`${API_BASE}/tree/taxonomy/species`, {
          params: { family: taxonomyParams.family, genus }
        });
        speciesSelectList.value = res.data.content || [];
      } catch (e) {
        console.error('加载种列表失败', e);
      }
    };

    // 科属种查询：调用后端 /tree/filter 进行综合筛选
    const handleTaxonomySearch = async () => {
      taxonomySearching.value = true;
      try {
        const params: any = {};
        if (taxonomyParams.family) params.family = taxonomyParams.family;
        if (taxonomyParams.genus) params.genus = taxonomyParams.genus;
        if (taxonomyParams.species) params.species = taxonomyParams.species;
        if (filterParams.protectionLevel) params.protectionLevel = filterParams.protectionLevel;
        if (filterParams.ageRange && filterParams.ageRange.length === 2) {
          params.minAge = filterParams.ageRange[0];
          params.maxAge = filterParams.ageRange[1];
        }
        const res = await axios.get(`${API_BASE}/tree/filter`, { params });
        const list = res.data.content || [];
        taxonomyResults.value = list.map((item: any) => ({
          id: item.id,
          treeCode: item.treeCode,
          name: item.name,
          species: item.species,
          family: item.family,
          genus: item.genus,
          protectionLevel: item.protectionLevel || '未知',
          age: item.age || 0,
          height: item.height,
          latitude: item.latitude,
          longitude: item.longitude,
          location: item.location || item.desc || '',
          image: item.image ? `${API_BASE}${item.image}` : ''
        }));
        message.success(`查询完成，共 ${taxonomyResults.value.length} 条记录`);
      } catch (e) {
        message.error('查询失败');
        console.error(e);
      }
      taxonomySearching.value = false;
    };

    // 快速选择科属种
    const quickSelectTaxonomy = (family: string, genus: string, species: string) => {
      taxonomyParams.family = family;
      onFamilyChange(family).then(() => {
        taxonomyParams.genus = genus;
        onGenusChange(genus).then(() => {
          taxonomyParams.species = species;
          handleTaxonomySearch();
        });
      });
    };

    // 显示古树详情
    const showDetail = (item: any) => {
      detailData.value = item;
      detailVisible.value = true;
    };

    // 保护级别颜色
    const getProtectionColor = (level: string) => {
      if (level === '一级') return 'orange';
      if (level === '二级') return 'blue';
      if (level === '三级') return 'green';
      return 'default';
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

    // 页面加载时预加载科列表（用于科属种查询）
    onMounted(() => {
      loadFamilies();
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
      nameParams,
      nameSearching,
      nameResults,
      nameColumns,
      customRequest,
      beforeUploadI2I,
      beforeUploadI2L,
      handleI2IChange,
      handleI2LChange,
      handleI2ISearch,
      handleI2LSearch,
      handleL2ISearch,
      handleNameSearch,
      setNameKeyword,
      setLocation,
      getSimilarityColor,
      // taxonomy
      taxonomyParams,
      filterParams,
      taxonomySearching,
      taxonomyResults,
      familyList,
      genusList,
      speciesSelectList,
      onFamilyChange,
      onGenusChange,
      handleTaxonomySearch,
      quickSelectTaxonomy,
      // detail
      detailVisible,
      detailData,
      showDetail,
      getProtectionColor
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

/* 详情抽屉样式 */
.detail-content {
  padding: 8px;
}

.detail-image-wrapper {
  text-align: center;
  margin-bottom: 24px;
}

.detail-image {
  width: 100%;
  max-height: 300px;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
</style>
