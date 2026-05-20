import os
import re
from collections import defaultdict, Counter

def read_file(path):
    with open(path, 'r', encoding='utf-8') as f:
        return f.read()

# Read the full text
base_dir = 'word/第0章/二审/论文/正文'
full_text = read_file(os.path.join(base_dir, '全部正文.txt'))
preface_text = read_file(os.path.join(base_dir, '前言.txt'))

# Split into lines and parse
lines = full_text.strip().split('\n')
preface_lines = preface_text.strip().split('\n')

# Parse paragraphs: extract seq, style, text
paragraphs = []
for line in lines:
    m = re.match(r'\[(\d+)\]\s+\[([^\]]+)\]\s+(.*)', line)
    if m:
        paragraphs.append({
            'seq': int(m.group(1)),
            'style': m.group(2),
            'text': m.group(3)
        })

preface_paras = []
for line in preface_lines:
    m = re.match(r'\[(\d+)\]\s+\[([^\]]+)\]\s+(.*)', line)
    if m:
        preface_paras.append({
            'idx': int(m.group(1)),
            'style': m.group(2),
            'text': m.group(3)
        })

all_text = '\n'.join(p['text'] for p in paragraphs)
full_lines_with_seq = [(p['seq'], p['text']) for p in paragraphs]

report = []
report.append('# 论文二审审查报告')
report.append('')
report.append('> 生成时间：2026-05-20')
report.append('> 审查范围：术语一致性、数据一致性、编号连续性、摘要一致性、目录标题一致性、新增风险点')
report.append('')

# ============================================================
# 1. 术语一致性检查
# ============================================================
report.append('## 一、术语一致性检查')
report.append('')

# Define forbidden terms and their correct forms
term_checks = [
    ('图片', '图像'),
    ('文本', '地理位置元数据'),
    ('文字', '地理位置元数据'),
    ('哈希值', '哈希码'),
    ('搜索', '检索'),
    ('跨模态', '多模态融合'),
    ('经纬度', '位置坐标'),
    ('GPS坐标', '位置坐标'),
    ('预测拍摄地理位置', '检索/返回数据库已知位置'),
    ('特征(?!向量|提取|学习|表示|空间|编码|映射)', '特征向量'),  # 简单检查
]

# Also check for DHLAM naming consistency
dhlam_issues = []
for seq, text in full_lines_with_seq:
    # Check for DHLM vs DHLAM inconsistency
    if 'DHLM' in text and 'DHLAM' not in text:
        # This might be OK if the model is now called DHLM
        pass
    # Check for forbidden terms
    for bad, good in term_checks:
        if bad in text:
            dhlam_issues.append((seq, bad, good, text[:80]))

# Filter out some false positives
dhlam_issues = [(s, b, g, t) for s, b, g, t in dhlam_issues 
                if not (b == '特征' and ('特征向量' in t or '特征提取' in t or '特征学习' in t or '特征表示' in t or '特征空间' in t or '特征编码' in t or '特征映射' in t))]

if dhlam_issues:
    report.append('### 发现术语混用问题')
    report.append('')
    seen = set()
    for seq, bad, good, context in dhlam_issues:
        key = (bad, context[:40])
        if key not in seen:
            seen.add(key)
            report.append(f'- **[{bad}]** → 应改为「{good}」  （段落[{seq}]：...{context}...）')
    report.append('')
else:
    report.append('✅ 未发现明显的术语混用问题。')
    report.append('')

# Check DHLAM/DHLM naming
report.append('### 模型命名检查')
dhlm_count = all_text.count('DHLM')
dhlam_count = all_text.count('DHLAM')
report.append(f'- 「DHLM」出现 {dhlm_count} 次')
report.append(f'- 「DHLAM」出现 {dhlam_count} 次')
if dhlm_count > 0 and dhlam_count > 0:
    report.append('⚠️ **警告**：DHLM 与 DHLAM 混用！请统一模型名称。')
elif dhlm_count > 0:
    report.append('ℹ️ 论文使用「DHLM」作为模型简称（与二审-content中导师建议一致）。')
report.append('')

# ============================================================
# 2. 图号/表号/公式号连续性检查
# ============================================================
report.append('## 二、图号/表号/公式号连续性检查')
report.append('')

# Extract figure numbers: 图X.Y
fig_pattern = re.compile(r'图(\d+)\.(\d+)')
table_pattern = re.compile(r'表(\d+)\.(\d+)')
formula_pattern = re.compile(r'[（(]\s*(\d+)\s*[-－]\s*(\d+)\s*[）)]')
# Also match 公式（X-Y） or 式（X-Y）
formula_pattern2 = re.compile(r'(?:公式|式)\s*[（(]\s*(\d+)\s*[-－]\s*(\d+)\s*[）)]')

figures = []
tables = []
formulas = []

for seq, text in full_lines_with_seq:
    for m in fig_pattern.finditer(text):
        figures.append((int(m.group(1)), int(m.group(2)), seq, text[:60]))
    for m in table_pattern.finditer(text):
        tables.append((int(m.group(1)), int(m.group(2)), seq, text[:60]))
    for m in formula_pattern.finditer(text):
        # Avoid matching years like (2023)
        if int(m.group(1)) < 10 and int(m.group(2)) < 50:
            formulas.append((int(m.group(1)), int(m.group(2)), seq, text[:60]))
    for m in formula_pattern2.finditer(text):
        formulas.append((int(m.group(1)), int(m.group(2)), seq, text[:60]))

# Check figures by chapter
report.append('### 图号统计')
for ch in range(1, 6):
    ch_figs = [(y, s) for x, y, s, t in figures if x == ch]
    if ch_figs:
        nums = sorted(set(y for y, s in ch_figs))
        missing = [i for i in range(1, max(nums)+1) if i not in nums]
        dupes = [y for y, cnt in Counter(y for y, s in ch_figs).items() if cnt > 1]
        report.append(f'- 第{ch}章：图 {nums}，共{len(nums)}个')
        if missing:
            report.append(f'  ⚠️ 缺失图号：{missing}')
        if dupes:
            report.append(f'  ⚠️ 重复图号：{dupes}')

# Check actual figure files
fig_dir = 'word/第0章/二审/论文/图'
fig_files = [f for f in os.listdir(fig_dir) if f.endswith('.png')]
fig_file_nums = []
for f in fig_files:
    m = re.match(r'图(\d+)\.(\d+)', f)
    if m:
        fig_file_nums.append((int(m.group(1)), int(m.group(2)), f))

report.append('')
report.append('### 图片文件与正文引用对照')
file_nums = set((x, y) for x, y, f in fig_file_nums)
text_nums = set((x, y) for x, y, s, t in figures)
only_in_files = file_nums - text_nums
only_in_text = text_nums - file_nums
if only_in_files:
    report.append(f'- ⚠️ 图片文件存在但正文未引用：{sorted(only_in_files)}')
if only_in_text:
    report.append(f'- ⚠️ 正文引用但图片文件不存在：{sorted(only_in_text)}')
if not only_in_files and not only_in_text:
    report.append('✅ 图片文件与正文引用一致。')

report.append('')
report.append('### 表号统计')
for ch in range(1, 6):
    ch_tables = [(y, s) for x, y, s, t in tables if x == ch]
    if ch_tables:
        nums = sorted(set(y for y, s in ch_tables))
        missing = [i for i in range(1, max(nums)+1) if i not in nums]
        dupes = [y for y, cnt in Counter(y for y, s in ch_tables).items() if cnt > 1]
        report.append(f'- 第{ch}章：表 {nums}，共{len(nums)}个')
        if missing:
            report.append(f'  ⚠️ 缺失表号：{missing}')
        if dupes:
            report.append(f'  ⚠️ 重复表号：{dupes}')

report.append('')
report.append('### 公式号统计')
for ch in range(1, 6):
    ch_formulas = [(y, s) for x, y, s, t in formulas if x == ch]
    if ch_formulas:
        nums = sorted(set(y for y, s in ch_formulas))
        missing = [i for i in range(1, max(nums)+1) if i not in nums]
        dupes = [y for y, cnt in Counter(y for y, s in ch_formulas).items() if cnt > 1]
        report.append(f'- 第{ch}章：公式 {nums}，共{len(nums)}个')
        if missing:
            report.append(f'  ⚠️ 缺失公式号：{missing}')
        if dupes:
            report.append(f'  ⚠️ 重复公式号：{dupes}')

report.append('')

# ============================================================
# 3. 数据一致性检查
# ============================================================
report.append('## 三、数据一致性检查')
report.append('')

# Extract key numbers
report.append('### 关键实验数据提取')
report.append('')

# mAP values
map_matches = re.findall(r'mAP\s*(?:达到|为|至|等于|[:：]\s*)\s*(0\.\d+)', all_text)
report.append(f'- 正文中提到的 mAP 值：{list(set(map_matches))}')

# Dataset sizes
inat_match = re.findall(r'iNaturalist\s*2018.*?([\d,]+)\s*张', all_text)
bfath_match = re.findall(r'BFATH.*?([\d,]+)\s*张', all_text)
if inat_match:
    report.append(f'- iNaturalist 2018 数据集规模：{inat_match}')
if bfath_match:
    report.append(f'- BFATH 数据集规模：{bfath_match}')

# Hash code length
hash_len = re.findall(r'(\d+)\s*位\s*二进制\s*哈希码', all_text)
if hash_len:
    report.append(f'- 哈希码长度：{hash_len}')

# Feature dimensions
feat_dim = re.findall(r'(\d+)\s*维\s*(?:特征|向量)', all_text)
if feat_dim:
    report.append(f'- 特征维度提及：{list(set(feat_dim))}')

# Number of classes
class_num = re.findall(r'(\d+)\s*个?\s*(?:类别|类|树种)', all_text)
if class_num:
    report.append(f'- 类别/树种数量提及：{list(set(class_num))}')

report.append('')

# Check specific data points between abstract and body
report.append('### 中英文摘要与正文数据对比')
report.append('')

# Find abstract paragraphs in preface
zh_abstract = []
en_abstract = []
in_abstract = False
for p in preface_paras:
    if '摘要' in p['text'] and len(p['text']) < 10:
        in_abstract = True
    elif 'Abstract' in p['text'] and len(p['text']) < 15:
        in_abstract = False
    elif in_abstract and p['text']:
        zh_abstract.append(p['text'])

in_en_abstract = False
for p in preface_paras:
    if p['text'] == 'Abstract':
        in_en_abstract = True
    elif p['text'] == 'Key Words: Ancient and Famous Tree Retrieval, Deep Hashing Learning, Multimodal Hashing Retrieval, Multimodal Fusion':
        in_en_abstract = False
    elif in_en_abstract:
        en_abstract.append(p['text'])

zh_abstract_text = '\n'.join(zh_abstract)
en_abstract_text = '\n'.join(en_abstract)

# Check key numbers in abstract vs body
report.append(f'- 中文摘要中 mAP 值：{re.findall(r"(0\.\d+)", zh_abstract_text)}')
report.append(f'- 英文摘要中 mAP 值：{re.findall(r"(0\.\d+)", en_abstract_text)}')

# Check for 128-bit in abstract
if '128' in zh_abstract_text:
    report.append('- 中文摘要提及 128')
if '128' in en_abstract_text:
    report.append('- 英文摘要提及 128')

report.append('')

# ============================================================
# 4. 目录标题与实际标题一致性
# ============================================================
report.append('## 四、目录标题与实际标题一致性')
report.append('')

# Extract all heading paragraphs
headings = [(p['seq'], p['style'], p['text']) for p in paragraphs if 'Heading' in p['style']]

# Find chapter-level headings (Heading 1)
ch_headings = [(s, t) for s, st, t in headings if st == 'Heading 1']
report.append('### 一级标题（章标题）')
for s, t in ch_headings:
    report.append(f'- [{s}] {t}')

report.append('')
report.append('### 二级标题（节标题）抽查')
sec_headings = [(s, t) for s, st, t in headings if st == 'Heading 2']
for s, t in sec_headings[:20]:
    report.append(f'- [{s}] {t}')
if len(sec_headings) > 20:
    report.append(f'- ... 共 {len(sec_headings)} 个二级标题')

report.append('')

# Check if 1.4 title matches
sec_14 = [t for s, t in sec_headings if '1.4' in t]
if sec_14:
    report.append(f'- 1.4 节标题：「{sec_14[0]}」')

# Check chapter 4 title
ch4_title = [t for s, t in ch_headings if t.startswith('4 ')]
if ch4_title:
    report.append(f'- 第4章标题：「{ch4_title[0]}」')

report.append('')

# ============================================================
# 5. 新增风险点
# ============================================================
report.append('## 五、新增风险点（客观硬伤）')
report.append('')

# Check for straight quotes
straight_quotes = []
for seq, text in full_lines_with_seq:
    if '"' in text or "'" in text:
        # Check if it's actually a straight quote (not part of citation or code)
        if re.search(r'[\u4e00-\u9fff]["\']|["\'][\u4e00-\u9fff]', text):
            straight_quotes.append((seq, text[:80]))

if straight_quotes:
    report.append('### 直引号风险')
    report.append('盲审3曾指出双引号标点不对。以下位置使用了直引号：')
    for seq, ctx in straight_quotes[:10]:
        report.append(f'- [{seq}] ...{ctx}...')
    if len(straight_quotes) > 10:
        report.append(f'- ... 共 {len(straight_quotes)} 处')
    report.append('')

# Check for inconsistent citation format
citation_issues = []
for seq, text in full_lines_with_seq:
    # Check for [a-b] with only 2 citations
    m = re.search(r'\[(\d+)-(\d+)\]', text)
    if m:
        a, b = int(m.group(1)), int(m.group(2))
        if b - a == 1:
            citation_issues.append((seq, text[:80], f'[{a}-{b}] 应改为 [{a},{b}]'))

if citation_issues:
    report.append('### 引用格式风险')
    report.append('2篇连续文献应使用逗号而非连字符：')
    for seq, ctx, fix in citation_issues:
        report.append(f'- [{seq}] {fix}  （...{ctx}...）')
    report.append('')

# Check for English author names in ALL CAPS
caps_authors = []
for seq, text in full_lines_with_seq:
    # Match patterns like LIU H M or HE K M
    if re.search(r'\b[A-Z]{2,}\s+[A-Z]\s*(?:,|\.)', text):
        caps_authors.append((seq, text[:80]))

if caps_authors:
    report.append('### 作者名全大写风险')
    report.append('参考文献中外文作者应首字母大写，禁用全大写：')
    for seq, ctx in caps_authors[:5]:
        report.append(f'- [{seq}] ...{ctx}...')
    report.append('')

# Check for model name inconsistency in text
report.append('### 模型名称一致性')
if dhlm_count > 0 and dhlam_count > 0:
    report.append(f'⚠️ DHLM ({dhlm_count}次) 与 DHLAM ({dhlam_count}次) 混用，需统一！')
    # Find examples
    for seq, text in full_lines_with_seq:
        if 'DHLAM' in text:
            report.append(f'  - DHLAM 出现在 [{seq}]')
            break
    for seq, text in full_lines_with_seq:
        if 'DHLM' in text:
            report.append(f'  - DHLM 出现在 [{seq}]')
            break
else:
    report.append('✅ 模型名称使用一致。')
report.append('')

# Check for figure/table caption issues
report.append('### 图表标注风险')
fig_captions = [(s, t) for s, t in full_lines_with_seq if re.match(r'图\d+\.\d+', t)]
table_captions = [(s, t) for s, t in full_lines_with_seq if re.match(r'表\d+\.\d+', t)]

for seq, text in fig_captions:
    if 'Figure' not in text and 'figure' not in text:
        # Chinese figure caption without English
        pass  # This is OK if not required

report.append(f'- 图注共 {len(fig_captions)} 处')
report.append(f'- 表注共 {len(table_captions)} 处')

# Check if any table is missing three-line style mention
report.append('')

# ============================================================
# 6. 导师关注的问题点快速检查
# ============================================================
report.append('## 六、导师关注问题点检查')
report.append('')

report.append('### 1. 创新性相关')
report.append('')

# Check if 3.2.4 section exists and what it says
sec_324 = [(s, t) for s, st, t in headings if '3.2.4' in t]
if sec_324:
    report.append(f'- 3.2.4 节标题：「{sec_324[0][1]}」')
    # Find content of 3.2.4
    start_seq = sec_324[0][0]
    sec_324_content = []
    for p in paragraphs:
        if p['seq'] > start_seq:
            if p['style'] == 'Heading 2' or p['style'] == 'Heading 1':
                break
            if p['text']:
                sec_324_content.append(p['text'])
    report.append(f'- 3.2.4 节内容长度：约 {sum(len(t) for t in sec_324_content)} 字')
else:
    report.append('- ⚠️ 未找到 3.2.4 节')

report.append('')
report.append('### 2. 数据集描述相关')
report.append('')

# Check 3.3.1 section
sec_331 = [(s, t) for s, st, t in headings if '3.3.1' in t]
if sec_331:
    report.append(f'- 3.3.1 节标题：「{sec_331[0][1]}」')
    start_seq = sec_331[0][0]
    sec_331_content = []
    for p in paragraphs:
        if p['seq'] > start_seq:
            if p['style'] == 'Heading 2' or p['style'] == 'Heading 1':
                break
            if 'Heading 3' in p['style']:
                break
            if p['text']:
                sec_331_content.append(p['text'])
    content_text = '\n'.join(sec_331_content)
    report.append(f'- 3.3.1 节内容长度：约 {len(content_text)} 字')
    if 'iNaturalist' in content_text:
        report.append('- ✅ 提及 iNaturalist 数据集')
    if '预训练' in content_text:
        report.append('- ⚠️ 提及「预训练」，需确认首次出现位置及说明是否清晰')
else:
    report.append('- ⚠️ 未找到 3.3.1 节')

report.append('')
report.append('### 3. 消融实验相关')
report.append('')

# Search for消融实验 in text
ablation_mentions = [(s, t) for s, t in full_lines_with_seq if '消融' in t]
report.append(f'- 「消融实验」提及 {len(ablation_mentions)} 次')
for s, t in ablation_mentions[:5]:
    report.append(f'  - [{s}] {t[:80]}')

report.append('')

# ============================================================
# 7. 章节结构一致性
# ============================================================
report.append('## 七、章节结构一致性')
report.append('')

# Check if each chapter has consistent intro/summary
for ch in range(1, 6):
    ch_head = [t for s, t in ch_headings if t.startswith(f'{ch} ')]
    if ch_head:
        report.append(f'- 第{ch}章标题：「{ch_head[0]}」')

report.append('')

# Write report
report_path = 'word/第0章/二审/论文审查报告.md'
with open(report_path, 'w', encoding='utf-8') as f:
    f.write('\n'.join(report))

print(f'Report written to {report_path}')
print(f'Total paragraphs analyzed: {len(paragraphs)}')
print(f'Figures: {len(figures)}, Tables: {len(tables)}, Formulas: {len(formulas)}')
