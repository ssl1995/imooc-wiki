<template>
  <div class="retrieval-container">
    <!-- 页面头部 -->
    <div class="retrieval-header">
      <h1 class="title">
        <TreeOutlined /> 古树名木信息查询
      </h1>
      <p class="subtitle">融合图像与地理位置元数据的智能检索</p>
    </div>

    <!-- 检索模式切换 -->
    <div class="mode-tabs">
      <a-radio-group v-model:value="currentMode" button-style="solid" size="large">
        <a-radio-button value="taxonomy">
          <ApartmentOutlined /> 科属种查询
        </a-radio-button>
        <a-radio-button value="name">
          <PartitionOutlined /> 树种名称查询
        </a-radio-button>
      </a-radio-group>
    </div>

    <!-- NameSearch: 按树种名称检索 -->
    <div v-if="currentMode === 'name'" class="search-section">
      <a-row :gutter="24">
        <a-col :span="8">
          <a-card title="树种名称查询" class="query-card">
            <a-form :model="nameParams" layout="vertical">
              <a-form-item label="树种名称" required>
                <a-input-search
                  v-model:value="nameParams.keyword"
                  placeholder="例如: 银杏、侧柏、油松"
                  enter-button
                  @search="handleNameSearch"
                  :loading="nameSearching"
                />
              </a-form-item>
              <a-form-item label="树龄范围">
                <a-slider v-model:value="nameParams.ageRange" range :min="0" :max="2000" :marks="{0: '0', 500: '500', 1000: '1000', 1500: '1500', 2000: '2000'}" />
              </a-form-item>
            </a-form>
            <a-button type="primary" size="large" block @click="handleNameSearch" :loading="nameSearching">
              <SearchOutlined /> 查询
            </a-button>

          </a-card>
        </a-col>

        <a-col :span="16">
          <a-card title="查询结果" class="result-card">
            <div v-if="!nameResults.length" class="empty-result">
              <a-empty description="请输入树种名称开始查询" />
            </div>
            <div v-else>
              <a-alert
                :message="`共查询到 ${nameResults.length} 条古树名木记录`"
                type="success"
                show-icon
                style="margin-bottom: 16px"
              />
              <a-table
                :columns="nameColumns"
                :data-source="nameResults"
                :pagination="{ pageSize: 5 }"
                row-key="id"
                size="middle"
                bordered
              />
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>

    <!-- Taxonomy: 科属种查询 -->
    <div v-if="currentMode === 'taxonomy'" class="search-section">
      <a-row :gutter="24">
        <a-col :span="8">
          <a-card title="科属种导航" class="query-card">
            <a-form :model="taxonomyParams" layout="vertical">
              <a-form-item label="选择科">
                <a-select
                  v-model:value="taxonomyParams.family"
                  placeholder="请选择科"
                  @change="onFamilyChange"
                  allowClear
                >
                  <a-select-option v-for="f in familyList" :key="f" :value="f">{{ f }}</a-select-option>
                </a-select>
              </a-form-item>
              <a-form-item label="选择属">
                <a-select
                  v-model:value="taxonomyParams.genus"
                  placeholder="请先选择科"
                  @change="onGenusChange"
                  :disabled="!taxonomyParams.family"
                  allowClear
                >
                  <a-select-option v-for="g in genusList" :key="g" :value="g">{{ g }}</a-select-option>
                </a-select>
              </a-form-item>
              <a-form-item label="选择种">
                <a-select
                  v-model:value="taxonomyParams.species"
                  placeholder="请先选择属"
                  :disabled="!taxonomyParams.genus"
                  allowClear
                >
                  <a-select-option v-for="s in speciesSelectList" :key="s" :value="s">{{ s }}</a-select-option>
                </a-select>
              </a-form-item>
            </a-form>
          </a-card>

          <a-card title="属性筛选" class="query-card" style="margin-top: 16px;">
            <a-form :model="filterParams" layout="vertical">
              <a-form-item label="保护级别">
                <a-select v-model:value="filterParams.protectionLevel" placeholder="全部级别" allowClear>
                  <a-select-option value="一级">一级</a-select-option>
                  <a-select-option value="二级">二级</a-select-option>
                  <a-select-option value="三级">三级</a-select-option>
                </a-select>
              </a-form-item>
              <a-form-item label="树龄范围">
                <a-slider v-model:value="filterParams.ageRange" range :min="0" :max="2000" :marks="{0: '0', 500: '500', 1000: '1000', 1500: '1500', 2000: '2000'}" />
              </a-form-item>
            </a-form>
          </a-card>

          <a-button type="primary" size="large" block @click="handleTaxonomySearch" :loading="taxonomySearching" style="margin-top: 16px;">
            <SearchOutlined /> 查询古树
          </a-button>

        </a-col>

        <a-col :span="16">
          <a-card title="古树名木列表" class="result-card">
            <div v-if="!taxonomyResults.length" class="empty-result">
              <a-empty description="请选择科属种条件进行查询" />
            </div>
            <div v-else>
              <a-alert
                :message="`共查询到 ${taxonomyResults.length} 条古树名木记录`"
                type="success"
                show-icon
                style="margin-bottom: 16px"
              />
              <div class="result-grid">
                <a-row :gutter="[16, 16]">
                  <a-col :span="8" v-for="(item, index) in taxonomyResults" :key="index">
                    <a-card hoverable class="result-item" @click="showDetail(item)">
                      <template #cover>
                        <img :src="item.image" class="result-image" />
                      </template>
                      <a-card-meta>
                        <template #title>
                          <div class="result-title">
                            {{ item.name }}
                            <a-tag color="green">{{ item.protectionLevel }}</a-tag>
                          </div>
                        </template>
                        <template #description>
                          <div class="result-info">
                            <p><ApartmentOutlined /> {{ item.family }} / {{ item.genus }} / {{ item.species }}</p>
                            <p><CalendarOutlined /> 树龄: {{ item.age }}年</p>
                            <p><EnvironmentOutlined /> {{ item.location }}</p>
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

    <!-- 古树详情抽屉 -->
    <a-drawer
      v-model:visible="detailVisible"
      title="古树名木详情"
      width="600"
      placement="right"
    >
      <div v-if="detailData" class="detail-content">
        <div class="detail-image-wrapper">
          <img :src="detailData.image" class="detail-image" />
        </div>
        <a-descriptions bordered :column="1" size="middle">
          <a-descriptions-item label="古树名木名称">{{ detailData.name }}</a-descriptions-item>
          <a-descriptions-item label="古树名木编号">{{ detailData.treeCode }}</a-descriptions-item>
          <a-descriptions-item label="科">
            <a-tag color="blue">{{ detailData.family }}</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="属">
            <a-tag color="cyan">{{ detailData.genus }}</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="种">{{ detailData.species }}</a-descriptions-item>
          <a-descriptions-item label="保护级别">
            <a-tag :color="getProtectionColor(detailData.protectionLevel)">{{ detailData.protectionLevel }}</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="树龄">{{ detailData.age }} 年</a-descriptions-item>
          <a-descriptions-item label="树高">{{ detailData.height }}</a-descriptions-item>
          <a-descriptions-item label="纬度">{{ detailData.latitude }}</a-descriptions-item>
          <a-descriptions-item label="经度">{{ detailData.longitude }}</a-descriptions-item>
          <a-descriptions-item label="位置描述">{{ detailData.location }}</a-descriptions-item>
        </a-descriptions>
      </div>
    </a-drawer>
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
    const currentMode = ref<'name' | 'taxonomy'>('taxonomy');
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
        if (nameParams.ageRange && nameParams.ageRange.length === 2) {
          formData.append('minAge', nameParams.ageRange[0].toString());
          formData.append('maxAge', nameParams.ageRange[1].toString());
        }
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

    // 页面加载时预加载科列表，并默认查询全部古树数据
    onMounted(() => {
      loadFamilies();
      handleTaxonomySearch();
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
