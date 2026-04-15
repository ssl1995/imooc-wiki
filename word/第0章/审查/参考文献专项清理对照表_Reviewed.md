================================================================================
参考文献专项清理对照表（审查修正版）
生成日期：2026-04-15
审查结论：1)编号映射表存在重大错误需修正  2)确认5篇文献可删除  3)补充2篇高质量顶会文献，总数达51篇
================================================================================

【一、审查意见】
--------------------------------------------------------------------------------
1. 【重大错误】编号映射表中[49]与[50]的映射关系颠倒且移位：
   - 原对照表错误写法：旧[49]→新[50]，旧[50]→新[44]
   - 正确映射应为：旧[49](Zheng L SIFT meets CNN)→新[44]，旧[50](Sommerville Software Engineering)→新[45]
   - 由此导致原对照表中旧[51]→[45]、旧[52]→[46]、旧[53]→[47]、旧[54]→[48]均整体错位+1。若按原表执行批量替换，将造成正文引用大面积错误。
   - 本表已在【三】中给出完全修正后的映射关系。

2. 【删除文献确认】经对论文原文全文检索，以下5篇文献正文确无引用或学科匹配度不足，同意删除：
   - 原[5]  ZHANG S W... Plantvillage...                    → 正文无引用 ✓
   - 原[33] CHEN Z S... Deep hashing via discrepancy...       → 正文无引用 ✓
   - 原[34] LI W J... Feature learning based deep supervised hashing → 正文无引用 ✓
   - 原[37] ZADEH A... Tensor fusion network...              → 正文仅在[35-37]出现一次，且为情感分析领域论文，与古树名木/地理信息学科匹配度低，同意删除并将[35-37]改为[32-33] ✓
   - 原[39] PENG Y X... An overview of cross-media retrieval → 正文无引用 ✓

3. 【格式修正确认】以下格式问题已在清理后的列表中修正，无需再次调整：
   - 原[44](新[39]) Cao Y等已补全et al
   - 原[52](新[47]) You Y大小写已修正
   - 原[53](新[48]) Spring Boot电子文献格式已修正
   - 原[54](新[49]) MySQL电子文献格式已修正

4. 【补充文献】当前清理后数量为49篇。补充2篇与论文主题（图像-地理位置多模态检索、多模态哈希学习）高度相关的顶会文献（NeurIPS 2023、ICMR 2023），总数达到51篇，超过50篇要求。

================================================================================
【二、补充文献清单】
================================================================================

[50] Vivanco Cepeda V, Nayak G K, Shah M. GeoCLIP: CLIP-inspired alignment between locations and images for effective worldwide geo-localization[C]//Advances in Neural Information Processing Systems 36. New Orleans, USA, 2023: 8690-8701.
      建议引用位置：1.2.2节“地理位置元数据检索研究现状”中，紧跟在SatCLIP相关论述之后（正文约第120行“全球范围的位置嵌入技术使用卫星图像预训练位置编码器...”之后），作为图像-地理位置对齐学习的最新代表性工作，引用格式写作“[50]”。

[51] Pegia M, Jónsson B T, Moumtzidou A, et al. MuseHash: Supervised Bayesian hashing for multimodal image representation[C]//Proceedings of the 2023 ACM International Conference on Multimedia Retrieval. Thessaloniki, Greece, 2023: 434-442.
      建议引用位置：1.2.1节“深度哈希学习与图像检索方法”中，紧跟在“SSAH引入对抗学习机制...”一句之后（正文约第113行），与PromptHash、Deep Evidential Hashing等共同支撑“近年来多模态哈希方法持续演进”的论述，引用格式写作“[51]”；或作为2.3.2节多模态融合技术的补充引用。

================================================================================
【三、修正后的编号映射表（旧→新）】
================================================================================
旧编号 | 新编号 | 说明
-------|--------|------
[1]    | [1]    | 不变
[2]    | [2]    | 不变
[3]    | [3]    | 不变
[4]    | [4]    | 不变
[5]    | —      | 删除（正文无引用）
[6]    | [5]    | Liu H M等(DPSH)
[7]    | [6]    | Zhu H等(DHN)
[8]    | [7]    | Cao Z J等(HashNet)
[9]    | [8]    | Yang E K等(PRDH)
[10]   | [9]    | Jiang Q Y等(DCMH)
[11]   | [10]   | Li C等(SSAH)
[12]   | [11]   | Zou Q等(PromptHash)
[13]   | [12]   | Li Y等(Deep Evidential)
[14]   | [13]   | Li J X等(Lightweight)
[15]   | [14]   | Wang J D等(A survey on learning to hash)
[16]   | [15]   | Mai G等(Space2Vec)
[17]   | [16]   | Chu G等(Geo-Aware)
[18]   | [17]   | Mai G等(Contrastive Spatial)
[19]   | [18]   | Klemmer K等(SatCLIP)
[20]   | [19]   | Van Horn G等(iNaturalist)
[21]   | [20]   | Krizhevsky A等(AlexNet)
[22]   | [21]   | Srivastava N等(Dropout)
[23]   | [22]   | Glorot X等(Xavier)
[24]   | [23]   | Lin M等(NIN)
[25]   | [24]   | Simonyan K等(VGGNet)
[26]   | [25]   | Szegedy C等(GoogLeNet)
[27]   | [26]   | He K M等(ResNet)
[28]   | [27]   | Schroff F等(FaceNet/Triplet Loss)
[29]   | [28]   | Hadsell R等(Contrastive Loss)
[30]   | [29]   | Dosovitskiy A等(ViT)
[31]   | [30]   | Tan M等(EfficientNet)
[32]   | [31]   | Zhang S等(Multi-Modal Survey)
[33]   | —      | 删除（正文无引用）
[34]   | —      | 删除（正文无引用）
[35]   | [32]   | Snoek C G M等(Early vs Late Fusion)
[36]   | [33]   | Ramachandram D等(Deep Multimodal Survey)
[37]   | —      | 删除（低相关度）
[38]   | [34]   | Baltrusaitis T等(Multimodal Machine Learning Survey)
[39]   | —      | 删除（正文无引用）
[40]   | [35]   | Fukui A等(Multimodal Compact Bilinear Pooling)
[41]   | [36]   | Ben-Younes H等(MUTAN)
[42]   | [37]   | Lu J等(Hierarchical Co-attention)
[43]   | [38]   | Li X等(CBIR Survey)
[44]   | [39]   | Cao Y等(DCH) — 已修正et al
[45]   | [40]   | Wang Q等(MDSH)
[46]   | [41]   | Shrivastava A等(OHEM)
[47]   | [42]   | 京津冀古树寻踪
[48]   | [43]   | Shorten C等(Data Augmentation Survey)
[49]   | [44]   | Zheng L等(SIFT meets CNN)
[50]   | [45]   | Sommerville I (Software Engineering)
[51]   | [46]   | Sandhu R S等(RBAC)
[52]   | [47]   | You Y (Vue.js) — 已修正大小写
[53]   | [48]   | Spring Boot Documentation — 已修正空格
[54]   | [49]   | MySQL 8.0 Reference Manual — 已修正空格
—      | [50]   | 新增：GeoCLIP (NeurIPS 2023)
—      | [51]   | 新增：MuseHash (ICMR 2023)

================================================================================
【四、修正后的完整参考文献列表（可直接复制到Word）】
================================================================================

见/Users/ssl/code/imooc-wiki/word/第0章/审查/4月16日参考文献-1.txt

================================================================================
【五、修改操作清单】
================================================================================
1. 从参考文献列表中删除：原[5]、[33]、[34]、[37]、[39]（共5条）。
2. 将剩余的参考文献按【四】中的新编号重新排序。
3. 在正文中使用Word“查找替换”功能，按【三、修正后的编号映射表】将所有旧编号替换为新编号。
   特别注意以下高风险区域（因原对照表存在错误）：
   - 第277行：将[35-37]改为[32-33]
   - 第475行：将[49]改为[44]（评价指标引用）
   - 第821行：将[50]改为[45]（软件工程引用）
   - 第~841行附近：将[51]改为[46]（RBAC引用）
   - 第~869行附近：将[52]改为[47]（Vue引用）；将[53]改为[48]（Spring Boot引用）；将[54]改为[49]（MySQL引用）
4. 在1.2.2节适当位置插入对[50] GeoCLIP的引用。
5. 在1.2.1节（或2.3.2节）适当位置插入对[51] MuseHash的引用。
6. 统一英文作者姓名大小写（按Title Case格式）。
7. 确认[39]（新编号，原[44]）的et al格式已补全。
8. 确认[48]、[49]（新编号，原[53]、[54]）的电子文献格式和空格已修正。

================================================================================
