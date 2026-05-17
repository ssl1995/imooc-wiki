package com.jiawa.wiki.service.impl;

import com.jiawa.wiki.domain.Image;
import com.jiawa.wiki.domain.Tree;
import com.jiawa.wiki.mapper.ImageInfoMapper;
import com.jiawa.wiki.mapper.TreeMapper;
import com.jiawa.wiki.resp.TreeRetrieveResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.CRC32;

/**
 * DHLAM 模型检索服务 —— 模拟真实模型调用
 * =================================================================
 * 【定位】HashRetrieveService 的进阶版本，接口与真实 DHLAM 模型对齐，
 *        内部先用确定性伪逻辑实现，后续可无缝替换为真实模型调用。
 *
 * 【与 HashRetrieveService 的差异】
 *   - HashRetrieveService : 基于文件名 hashCode 生成伪哈希（演示阶段）
 *   - HashModelRetrieveService: 基于图像内容 + 位置坐标生成哈希码，
 *                              模拟 DHLAM 的 ImageEncoder→Fusion→HashLayer 流程
 *
 * 【模拟的模型流程】（对应论文图3.1）
 *   图像文件 → CRC32内容特征 → [可选] 位置坐标融合 → 128位哈希码
 *   查询哈希码 ↔ 数据库哈希码 汉明距离计算 → Top-K 排序返回
 *
 * 【后续替换路径】
 *   方案A（推荐）: 将 generateFromImage() 内部替换为 HTTP 调用 Python Flask 推理服务
 *   方案B       : 将 generateFromImage() 内部替换为 ProcessBuilder 调用 inference.py
 *   方案C       : 导出 ONNX 模型，Java 端用 ONNX Runtime 本地推理
 *
 * 【当前状态】
 *   项目代码暂不引用此 Service，等真实模型环境就绪后，在 TreeController 中
 *   将 @Resource HashRetrieveService 替换为 @Resource HashModelRetrieveService 即可。
 */
@Service
public class HashModelRetrieveService {

    private static final Logger LOG = LoggerFactory.getLogger(HashModelRetrieveService.class);

    /**
     * 128 位哈希码长度（二进制位数）
     */
    public static final int HASH_BITS = 128;

    /**
     * 128 位 = 16 字节
     */
    public static final int BYTE_LENGTH = HASH_BITS / 8;

    /**
     * 128 位 = 32 个十六进制字符
     */
    public static final int HEX_LENGTH = HASH_BITS / 4;

    @Resource
    private ImageInfoMapper imageInfoMapper;

    @Resource
    private TreeMapper treeMapper;

    // ==================== 配置：真实模型调用开关 ====================

    /**
     * Python Flask 推理服务地址，如 http://localhost:5000
     * 为空或 demo-mode=true 时走模拟逻辑
     */
    @Value("${dhlam.api.url:}")
    private String apiUrl;

    /**
     * 演示模式开关
     * true  = 走 Java 端模拟逻辑（不依赖 Python 环境）
     * false = HTTP 调用 Python Flask 推理服务（需要真实模型环境）
     */
    @Value("${dhlam.demo-mode:true}")
    private boolean demoMode;

    private final RestTemplate restTemplate = new RestTemplate();

    // ==================== 核心：模拟 DHLAM 模型推理 ====================

    /**
     * 【模拟真实 DHLAM 模型调用】基于图像内容 + 位置坐标生成 128 位哈希码
     *
     * 模拟数据流向（对应论文3.2节图3.1）：
     *   1. ImageEncoder : 读取图像文件 → 提取内容特征（CRC32）→ 64位图像特征
     *   2. [多模态] LocationEncoder: lat/lon → 编码为64位位置特征
     *   3. Fusion       : 图像特征 XOR 位置特征 → 融合特征
     *   4. HashLayer    : 融合特征 → 展开为128位二进制码 → hex字符串
     *
     * 【与真实模型的差异】
     *   真实模型: ResNet-50提取512d视觉特征 + MLP编码256d位置特征 + FC→Tanh→Sign
     *   模拟实现: CRC32内容哈希 + 坐标确定性编码 + XOR融合
     *   替换点  : 将本方法体替换为外部模型调用即可，接口和返回值保持不变
     *
     * @param imagePath 图像文件的绝对路径或相对路径
     * @param lat       纬度（可选，多模态时使用）
     * @param lon       经度（可选，多模态时使用）
     * @return HashResult 包含 hashCode（32位hex字符串）和 hashBits（16字节数组）
     */
    public HashResult generateFromImage(String imagePath, Double lat, Double lon) {
        if (imagePath == null || imagePath.isEmpty()) {
            LOG.warn("generateFromImage 收到空路径，返回默认哈希码");
            return generateDefaultHash();
        }

        // ========== 分支：真实模型调用 vs 模拟逻辑 ==========
        // 当 demo-mode=false 且 apiUrl 已配置时，优先调用 Python 推理服务
        if (!demoMode && apiUrl != null && !apiUrl.isEmpty()) {
            HashResult result = callPythonModel(imagePath, lat, lon);
            if (result != null) {
                LOG.info("真实模型调用成功: hashCode={}", result.getHashCode().substring(0, 8) + "...");
                return result;
            }
            LOG.warn("Python 模型调用失败，fallback 到模拟逻辑");
        }

        // Step 1: 提取图像内容特征（模拟 ImageEncoder）
        long imageFeature = extractImageFeature(imagePath);

        // Step 2: [多模态融合] 结合位置坐标（模拟 LocationEncoder + Fusion）
        long fusedFeature = imageFeature;
        if (lat != null && lon != null) {
            long locationFeature = encodeLocation(lat, lon);
            // 模拟融合层：XOR + 常量混合（确定性）
            fusedFeature = fuseFeatures(imageFeature, locationFeature);
            LOG.debug("多模态融合: imageFeature={}, locationFeature={}, fused={}",
                    imageFeature, locationFeature, fusedFeature);
        }

        // Step 3: 映射到 128 位哈希码（模拟 HashLayer: FC→Tanh→Sign）
        String hashCode = featureToHashCode(fusedFeature);
        byte[] hashBits = hexToBytes(hashCode);

        LOG.info("模拟模型推理完成: imagePath={}, lat={}, lon={}, hashCode={}",
                imagePath, lat, lon, hashCode.substring(0, 8) + "...");

        return new HashResult(hashCode, hashBits);
    }

    /**
     * I2I 检索：以图搜图
     *
     * 【完整检索流程】（对应论文3.4节表3.3 I2I实验）
     *   1. 查询图像 → generateFromImage() → 128位查询哈希码
     *   2. 遍历数据库全部 image 记录，获取其预存哈希码
     *   3. 计算查询图像与库中图像的汉明距离
     *   4. 按相似度降序排序（汉明距离越小越相似）
     *   5. 取 Top-K，关联查询 tree 信息组装响应
     *
     * 与论文指标对应:
     *   - mAP@K    : 需标注数据计算，本方法返回排序结果
     *   - Recall@K : 返回的K个结果中相关样本比例
     *   - 检索速度 : 128位汉明距离计算极快（论文表3.8: 0.8ms）
     *
     * @param queryImagePath 查询图像路径
     * @param lat            查询位置纬度（可选）
     * @param lon            查询位置经度（可选）
     * @param topK           返回结果数量
     * @return 按相似度降序排列的 TreeRetrieveResp 列表
     */
    public List<TreeRetrieveResp> retrieveI2I(String queryImagePath,
                                               Double lat, Double lon,
                                               int topK) {
        LOG.info("I2I 检索: queryImagePath={}, lat={}, lon={}, topK={}",
                queryImagePath, lat, lon, topK);

        // 1. 生成查询图像的哈希码
        HashResult queryHash = generateFromImage(queryImagePath, lat, lon);

        // 2. 获取数据库所有图像记录及其哈希码
        List<Image> allImages = imageInfoMapper.selectAll();
        if (allImages == null || allImages.isEmpty()) {
            LOG.warn("数据库中无图像记录");
            return Collections.emptyList();
        }

        // 3. 计算汉明距离并排序
        List<ScoredTree> scoredList = new ArrayList<>();
        for (Image img : allImages) {
            if (img.getHashCode() == null || img.getHashCode().isEmpty()) {
                continue;
            }
            int hammingDist = hammingDistance(queryHash.getHashCode(), img.getHashCode());
            // 相似度 = 1 - 汉明距离/128，范围 [0, 1]
            double similarity = 1.0 - (hammingDist / (double) HASH_BITS);

            Tree tree = treeMapper.selectById(img.getTreeId());
            if (tree != null) {
                scoredList.add(new ScoredTree(tree, similarity, hammingDist));
            }
        }

        // 4. 按相似度降序排序（相似度越高越靠前）
        scoredList.sort(new Comparator<ScoredTree>() {
            @Override
            public int compare(ScoredTree a, ScoredTree b) {
                return Double.compare(b.similarity, a.similarity);
            }
        });

        // 5. 取 Top-K 组装响应
        List<TreeRetrieveResp> results = new ArrayList<>();
        int limit = Math.min(topK, scoredList.size());
        for (int i = 0; i < limit; i++) {
            ScoredTree st = scoredList.get(i);
            results.add(toTreeRetrieveResp(st.tree, st.similarity));
        }

        LOG.info("I2I 检索完成: 查询到 {} 条记录，返回 Top-{}", scoredList.size(), results.size());
        return results;
    }

    /**
     * I2L 检索：以图搜位置
     *
     * 【完整检索流程】
     *   1. 查询图像 → generateFromImage() → 128位查询哈希码
     *   2. 在数据库中找到汉明距离最小的匹配图像
     *   3. 返回该图像所属古树的地理位置信息
     *
     * 与论文对应：
     *   - 论文强调"返回数据库已知位置"而非"预测拍摄地理位置"
     *   - 避免与 Geo-localization 任务混淆
     *
     * @param queryImagePath 查询图像路径
     * @param lat            查询位置纬度（可选，用于多模态融合）
     * @param lon            查询位置经度（可选，用于多模态融合）
     * @return 最匹配古树的地理位置信息（singletonList）
     */
    public List<TreeRetrieveResp> retrieveI2L(String queryImagePath,
                                               Double lat, Double lon) {
        LOG.info("I2L 检索: queryImagePath={}, lat={}, lon={}", queryImagePath, lat, lon);

        // 1. 生成查询图像的哈希码
        HashResult queryHash = generateFromImage(queryImagePath, lat, lon);

        // 2. 获取数据库所有图像记录
        List<Image> allImages = imageInfoMapper.selectAll();
        if (allImages == null || allImages.isEmpty()) {
            LOG.warn("数据库中无图像记录");
            return Collections.emptyList();
        }

        // 3. 找到汉明距离最小的匹配
        Image bestMatch = null;
        int minDistance = HASH_BITS + 1;

        for (Image img : allImages) {
            if (img.getHashCode() == null || img.getHashCode().isEmpty()) {
                continue;
            }
            int dist = hammingDistance(queryHash.getHashCode(), img.getHashCode());
            if (dist < minDistance) {
                minDistance = dist;
                bestMatch = img;
            }
        }

        if (bestMatch == null) {
            return Collections.emptyList();
        }

        // 4. 返回匹配古树的地理位置
        Tree tree = treeMapper.selectById(bestMatch.getTreeId());
        if (tree == null) {
            return Collections.emptyList();
        }

        double similarity = 1.0 - (minDistance / (double) HASH_BITS);
        TreeRetrieveResp resp = toTreeRetrieveResp(tree, similarity);
        resp.setConfidence(Math.max(0.75, similarity));
        resp.setError(0.5);

        LOG.info("I2L 检索完成: 匹配 treeCode={}, 汉明距离={}, 相似度={}",
                tree.getTreeCode(), minDistance, similarity);
        return Collections.singletonList(resp);
    }

    // ==================== 兼容：原有伪哈希方法（保留） ====================

    /**
     * 基于文件名生成确定性伪 128 位哈希码（与 HashRetrieveService 兼容）
     *
     * 用途：
     *   - 演示阶段 fallback
     *   - 与现有 HashRetrieveService 输出格式完全一致
     *   - 数据库已有数据使用此方法生成的哈希码仍可正常检索
     */
    public HashResult generate(String filename) {
        if (filename == null || filename.isEmpty()) {
            filename = "default";
        }
        int seed = filename.hashCode();

        StringBuilder hex = new StringBuilder(HEX_LENGTH);
        byte[] bits = new byte[BYTE_LENGTH];

        for (int i = 0; i < BYTE_LENGTH; i++) {
            int b = (seed + i * 0x9e3779b9) & 0xFF;
            hex.append(String.format("%02x", b));
            bits[i] = (byte) b;
        }

        return new HashResult(hex.toString(), bits);
    }

    public int offset(String filename, int mod) {
        if (filename == null || mod <= 0) {
            return 0;
        }
        return Math.abs(filename.hashCode()) % mod;
    }

    public double similarity(String filename, double min, double max) {
        if (filename == null) {
            return min;
        }
        int hash = Math.abs(filename.hashCode());
        double range = max - min;
        return min + (hash % 1000) / 1000.0 * range;
    }

    // ==================== 工具方法 ====================

    /**
     * 提取图像内容特征（模拟 ImageEncoder）
     *
     * 实现方式：
     *   - 优先使用 CRC32 计算文件内容哈希（相同文件内容 → 相同特征）
     *   - 若文件不存在或读取失败，使用文件路径字符串的 hashCode 作为 fallback
     *   - 对于支持图像解码的格式（JPG/PNG），额外提取缩略图像素特征混合
     *
     * @param imagePath 图像文件路径
     * @return 64位图像特征值
     */
    private long extractImageFeature(String imagePath) {
        File file = new File(imagePath);
        if (!file.exists()) {
            // 尝试从项目根目录解析
            String projectRoot = System.getProperty("user.dir");
            file = new File(projectRoot, imagePath);
        }
        if (!file.exists()) {
            LOG.warn("图像文件不存在: {}, 使用路径哈希作为 fallback", imagePath);
            return imagePath.hashCode() & 0xFFFFFFFFL;
        }

        long feature = 0L;

        // 方式1: 文件内容 CRC32（最稳定，相同内容相同哈希）
        try {
            CRC32 crc = new CRC32();
            byte[] buffer = new byte[8192];
            FileInputStream fis = new FileInputStream(file);
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                crc.update(buffer, 0, bytesRead);
            }
            fis.close();
            feature = crc.getValue();
        } catch (IOException e) {
            LOG.warn("CRC32 计算失败: {}, fallback 到文件大小+修改时间", imagePath);
            feature = (file.length() ^ file.lastModified()) & 0xFFFFFFFFL;
        }

        // 方式2: 若可解码为图像，提取缩略图像素特征混合
        try {
            BufferedImage img = ImageIO.read(file);
            if (img != null) {
                long pixelFeature = extractPixelFeature(img);
                // 混合两种特征
                feature = feature ^ (pixelFeature << 32);
            }
        } catch (Exception e) {
            // 非图像格式或解码失败，仅使用 CRC32 特征
            LOG.debug("图像解码失败（可能非标准图像格式）: {}", imagePath);
        }

        return feature;
    }

    /**
     * 提取缩略图像素特征
     *
     * 模拟 ResNet-50 的特征提取：
     *   - 将图像缩放到 8x8
     *   - 每个像素提取亮度并二值化
     *   - 64 个比特组成 64 位特征值
     */
    private long extractPixelFeature(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        long feature = 0L;

        // 采样 8x8 网格
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int x = w * col / 8;
                int y = h * row / 8;
                // 边界保护
                x = Math.min(x, w - 1);
                y = Math.min(y, h - 1);

                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int gray = (r * 299 + g * 587 + b * 114) / 1000; // 标准灰度公式

                int bit = (gray > 128) ? 1 : 0;
                feature = (feature << 1) | bit;
            }
        }
        return feature;
    }

    /**
     * 编码位置坐标（模拟 LocationEncoder）
     *
     * 将经纬度映射到 64 位确定性特征值。
     * 真实模型使用 MLP(2→128→128→256)，此处用数学变换模拟。
     */
    private long encodeLocation(double lat, double lon) {
        // 将坐标规范化到合理范围后做确定性混合
        long latBits = Double.doubleToRawLongBits(lat * 1000.0);
        long lonBits = Double.doubleToRawLongBits(lon * 1000.0);
        // 黄金比例混合，保证不同坐标产生明显差异
        return latBits ^ (lonBits * 0x9e3779b97f4a7c15L);
    }

    /**
     * 融合图像特征和位置特征（模拟 Fusion Layer）
     *
     * 真实模型: Concat([512d, 256d]) → Linear(768→512) + ReLU
     * 模拟实现: XOR + 旋转 + 常量混合（保持确定性）
     */
    private long fuseFeatures(long imageFeature, long locationFeature) {
        long fused = imageFeature ^ locationFeature;
        // 模拟非线性激活：循环移位 + 常量加性混合
        fused = fused ^ Long.rotateLeft(fused, 17);
        fused = fused * 0x9e3779b97f4a7c15L;
        return fused;
    }

    /**
     * 将 64 位特征值展开为 128 位十六进制哈希码（模拟 HashLayer: FC→Tanh→Sign）
     *
     * 通过伪随机扩散将 64 位输入映射到 128 位输出，
     * 保证输入微小变化导致输出大幅变化（雪崩效应）。
     */
    private String featureToHashCode(long feature) {
        // 使用 SplitMix64 风格的伪随机生成器展开 64 位 → 128 位
        long x = feature;
        StringBuilder hex = new StringBuilder(HEX_LENGTH);

        for (int i = 0; i < BYTE_LENGTH; i++) {
            x += 0x9e3779b97f4a7c15L;
            long z = x;
            z = (z ^ (z >>> 30)) * 0xbf58476d1ce4e5b9L;
            z = (z ^ (z >>> 27)) * 0x94d049bb133111ebL;
            z = z ^ (z >>> 31);
            int byteVal = (int) (z & 0xFF);
            hex.append(String.format("%02x", byteVal));
        }

        return hex.toString();
    }

    /**
     * 计算两个十六进制哈希码之间的汉明距离
     *
     * 汉明距离 = 两个二进制码按位 XOR 后统计 1 的个数。
     * 距离越小表示越相似，距离为 0 表示完全相同。
     *
     * 与论文对应：表3.8 中 128 位汉明距离计算仅需 0.8ms。
     *
     * @param hex1 十六进制哈希码1
     * @param hex2 十六进制哈希码2
     * @return 汉明距离 [0, 128]
     */
    public int hammingDistance(String hex1, String hex2) {
        if (hex1 == null || hex2 == null || hex1.length() != hex2.length()) {
            return HASH_BITS; // 最大距离表示完全不相似
        }
        int distance = 0;
        for (int i = 0; i < hex1.length(); i += 2) {
            int b1 = Integer.parseInt(hex1.substring(i, i + 2), 16);
            int b2 = Integer.parseInt(hex2.substring(i, i + 2), 16);
            distance += Integer.bitCount((b1 ^ b2) & 0xFF);
        }
        return distance;
    }

    /**
     * 将 Tree 实体转换为前端响应对象
     */
    private TreeRetrieveResp toTreeRetrieveResp(Tree tree, double similarity) {
        TreeRetrieveResp resp = new TreeRetrieveResp();
        resp.setId(tree.getId());
        resp.setTreeCode(tree.getTreeCode());
        resp.setName(tree.getName());
        resp.setSpecies(tree.getSpecies());
        resp.setAge(tree.getAge());
        resp.setHeight(tree.getHeight());
        resp.setLatitude(tree.getLatitude());
        resp.setLongitude(tree.getLongitude());
        resp.setLocation(tree.getDesc());
        resp.setSimilarity(Math.round(similarity * 1000.0) / 1000.0);
        resp.setImage("/tree/image/" + tree.getTreeCode());
        return resp;
    }

    /**
     * 【真实模型调用】通过 HTTP 请求 Python Flask 推理服务
     *
     * 调用链路：
     *   Java HashModelRetrieveService
     *     → HTTP POST http://localhost:5000/infer
     *     → Python api_server.py /infer
     *     → inference.py infer() → model.py ImageRetrievalModel.forward()
     *     → 返回 128 位哈希码
     *
     * Request Body:
     *   {"image_path": "...", "latitude": 39.9, "longitude": 116.4}
     *
     * Response Body:
     *   {"hash_code": "a1b2c3d4...", "bit_count": 64, "model_loaded": true}
     *
     * @param imagePath 图像文件绝对路径（Python 端需能直接访问此路径）
     * @param lat       纬度（可选）
     * @param lon       经度（可选）
     * @return HashResult 或 null（调用失败时）
     */
    private HashResult callPythonModel(String imagePath, Double lat, Double lon) {
        try {
            String url = apiUrl + "/infer";

            // 组装 JSON 请求体（Java 8 兼容写法）
            java.util.Map<String, Object> request = new java.util.HashMap<String, Object>();
            request.put("image_path", imagePath);
            if (lat != null) {
                request.put("latitude", lat);
            }
            if (lon != null) {
                request.put("longitude", lon);
            }

            // 发送 POST 请求，接收 JSON 响应
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> response = restTemplate.postForObject(url, request, java.util.Map.class);

            if (response != null && response.containsKey("hash_code")) {
                String hashCode = (String) response.get("hash_code");
                byte[] hashBits = hexToBytes(hashCode);
                return new HashResult(hashCode, hashBits);
            } else {
                LOG.warn("Python 服务返回异常: {}", response);
            }
        } catch (Exception e) {
            LOG.error("调用 Python 推理服务失败 [{}]: {}", apiUrl, e.getMessage());
        }
        return null;
    }

    /**
     * 生成默认哈希码（全零）
     */
    private HashResult generateDefaultHash() {
        StringBuilder hex = new StringBuilder(HEX_LENGTH);
        for (int i = 0; i < BYTE_LENGTH; i++) {
            hex.append("00");
        }
        return new HashResult(hex.toString(), new byte[BYTE_LENGTH]);
    }

    /**
     * Hex 字符串转字节数组
     */
    private byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    // ==================== 内部数据结构 ====================

    /**
     * 带分数的古树记录（内部排序用）
     */
    private static class ScoredTree {
        final Tree tree;
        final double similarity;
        final int hammingDistance;

        ScoredTree(Tree tree, double similarity, int hammingDistance) {
            this.tree = tree;
            this.similarity = similarity;
            this.hammingDistance = hammingDistance;
        }
    }

    /**
     * 哈希结果封装（与 HashRetrieveService.HashResult 兼容）
     */
    public static class HashResult {
        private final String hashCode;
        private final byte[] hashBits;

        public HashResult(String hashCode, byte[] hashBits) {
            this.hashCode = hashCode;
            this.hashBits = hashBits;
        }

        public String getHashCode() {
            return hashCode;
        }

        public byte[] getHashBits() {
            return hashBits;
        }
    }
}
