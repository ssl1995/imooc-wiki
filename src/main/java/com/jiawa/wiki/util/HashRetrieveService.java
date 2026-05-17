package com.jiawa.wiki.util;

import org.springframework.stereotype.Service;

/**
 * 哈希码生成算法工具类
 */
@Service
public class HashRetrieveService {

    /**
     * 128 位哈希码长度（十六进制字符数）
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

    /**
     * 基于文件名生成确定性伪 128 位哈希码（十六进制字符串 + 字节数组）
     *
     * @param filename 文件名（如上传图片的原始文件名或保存后的文件名）
     * @return HashResult 包含 hashCode（32 位十六进制字符串）和 hashBits（16 字节数组）
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

    /**
     * 基于文件名 hashCode 生成演示阶段用的整数偏移量
     * <p>
     * retrieve 中的 I2I/I2L 演示逻辑使用此偏移对结果做轮转排序。
     *
     * @param filename 文件名
     * @param mod      取模基数
     * @return 非负偏移量 [0, mod)
     */
    public int offset(String filename, int mod) {
        if (filename == null || mod <= 0) {
            return 0;
        }
        return Math.abs(filename.hashCode()) % mod;
    }

    /**
     * 基于文件名 hashCode 生成演示阶段用的相似度值
     *
     * @param filename 文件名
     * @param min      最小值
     * @param max      最大值
     * @return [min, max) 范围内的伪随机相似度
     */
    public double similarity(String filename, double min, double max) {
        if (filename == null) {
            return min;
        }
        int hash = Math.abs(filename.hashCode());
        double range = max - min;
        return min + (hash % 1000) / 1000.0 * range;
    }

    /**
     * 哈希结果封装
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
