# -*- coding: utf-8 -*-
import re
with open("word/第0章/二审/_extracted_paragraphs.txt", "r", encoding="utf-8") as f2:
    lines = f2.readlines()

# 术语残留扫描
terms_to_check = {
    "图像信息": [],
    "视觉信息": [],
    "视觉特征": [],
    "GPS坐标": [],
    "gps坐标": [],
    "图片": [],
    "搜索": [],
    "哈希值": [],
    "跨模态": [],
    "混合向量": [],
    "经纬度": [],
    "预测拍摄地理位置": [],
    "相本": [],
    "特征提取层": [],
    "预测位置": [],
    "预测拍摄位置": [],
    "位置元数据": [],
}
for line in lines:
    for term in terms_to_check:
        if term in line:
            if term == "位置元数据" and "地理位置元数据" in line:
                continue
            terms_to_check[term].append(line.strip())
print("=== 术语残留扫描 ===")
for term, hits in terms_to_check.items():
    if hits:
        print(f"[{term}] 发现 {len(hits)} 处")
        for h in hits[:5]:
            print(f"  {h[:200]}")
    else:
        print(f"[{term}] 未检出")
