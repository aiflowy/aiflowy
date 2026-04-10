package tech.aiflowy.ai.utils;

import com.agentsflex.core.document.Document;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 多渠道文档评分计算工具类
 * 适配规则：
 * 1. QA/摘要/向量库：score为0-1 → 转换为0-100分
 * 2. 搜索引擎：score无上限 → 归一化到0-100分
 * 3. 权重百分比自动归一化，最终返回带总分的Document列表
 */
public class DocumentScoreCalculator {

    /**
     * 核心计算方法
     * @param vectorDbDocs 向量库渠道文档列表（score 0-1）
     * @param searchEngineDocs 搜索引擎渠道文档列表（score无上限）
     * @param vectorDbWeightPercent 向量库权重百分比
     * @param searchEngineWeightPercent 搜索引擎权重百分比
     * @return 包含最终加权总分的Document列表（score为Double类型，0-100区间）
     */
    public static List<Document> calculateTotalScore(
            List<Document> vectorDbDocs,
            List<Document> searchEngineDocs,
            double vectorDbWeightPercent,
            double searchEngineWeightPercent) {

        // 1. 权重转换与归一化（double→BigDecimal，确保总和=1）
        BigDecimal vectorDbWeightBig = BigDecimal.valueOf(vectorDbWeightPercent);
        BigDecimal searchEngineWeightBig = BigDecimal.valueOf(searchEngineWeightPercent);

        // 计算权重总和
        BigDecimal totalWeight = vectorDbWeightBig.add(searchEngineWeightBig);

        // 归一化权重（保留4位小数）
        BigDecimal vectorDbWeight = vectorDbWeightBig.divide(totalWeight, 4, RoundingMode.HALF_UP);
        BigDecimal searchEngineWeight = searchEngineWeightBig.divide(totalWeight, 4, RoundingMode.HALF_UP);

        // 2. 处理各渠道Score
        Map<Object, Double> vectorDbScoreMap = process0To1Channel(vectorDbDocs); // 0-1 → 0-100
        Map<Object, Double> searchScoreMap = processSearchChannel(searchEngineDocs); // 无上限 → 0-100

        // 3. 收集所有文档ID（两个渠道并集，ID为Object类型）
        Set<Object> allDocIds = new HashSet<>();
        allDocIds.addAll(vectorDbScoreMap.keySet());
        allDocIds.addAll(searchScoreMap.keySet());

        // 4. 计算每个文档的最终总分，生成新的Document列表
        List<Document> resultDocs = new ArrayList<>();
        for (Object docId : allDocIds) {
            // 各渠道得分（无数据则为0.0）
            Double vectorDbScore = vectorDbScoreMap.getOrDefault(docId, 0.0);
            Double searchScore = searchScoreMap.getOrDefault(docId, 0.0);

            // 加权计算总分（BigDecimal保证精度，最后转Double）
            BigDecimal totalScoreBig = new BigDecimal(vectorDbScore).multiply(vectorDbWeight)
                    .add(new BigDecimal(searchScore).multiply(searchEngineWeight))
                    .setScale(2, RoundingMode.HALF_UP);
            Float totalScore = totalScoreBig.floatValue();

            // 生成新的Document（复用原有类，设置ID和最终总分）
            Document doc = new Document();
            doc.setId(docId);
            doc.setScore(totalScore);
            copyDocMetadata(doc, docId, vectorDbDocs, searchEngineDocs);
            resultDocs.add(doc);
        }

        return resultDocs;
    }
    /**
     * 处理0-1区间的渠道（QA/摘要/向量库）：0-1 → 0-100分
     */
    private static Map<Object, Double> process0To1Channel(List<Document> docs) {
        Map<Object, Double> scoreMap = new HashMap<>();
        if (docs == null || docs.isEmpty()) {
            return scoreMap;
        }

        for (Document doc : docs) {
            Object docId = doc.getId();
            Double rawScore = doc.getScore() == null ? 0.0 : doc.getScore();

            // 步骤1：校验原始score在0-1区间（防止异常值）
            rawScore = Math.max(0.0, Math.min(1.0, rawScore));
            // 步骤2：转换为0-100分（乘以100）
            Double normalizedScore = rawScore * 100.0;
            // 保留2位小数
            normalizedScore = Math.round(normalizedScore * 100.0) / 100.0;

            scoreMap.put(docId, normalizedScore);
        }
        return scoreMap;
    }

    /**
     * 处理搜索引擎渠道：无上限score → 归一化到0-100分
     */
    private static Map<Object, Double> processSearchChannel(List<Document> docs) {
        Map<Object, Double> scoreMap = new HashMap<>();
        if (docs == null || docs.isEmpty()) {
            return scoreMap;
        }

        // 提取所有有效Score，找最大值
        List<Float> allScores = new ArrayList<>();
        for (Document doc : docs) {
            Float score = doc.getScore();
            if (score != null && score > 0) {
                allScores.add(score);
            }
        }
        if (allScores.isEmpty()) {
            return scoreMap;
        }
        Float maxScore = Collections.max(allScores);

        // 线性归一化到0-100分
        for (Document doc : docs) {
            Object docId = doc.getId();
            Double rawScore = doc.getScore() == null ? 0.0 : doc.getScore();

            double normalized = (rawScore / maxScore) * 100.0;
            // 限制0-100区间
            normalized = Math.max(0.0, Math.min(100.0, normalized));
            // 保留2位小数
            normalized = Math.round(normalized * 100.0) / 100.0;
            scoreMap.put(docId, normalized);
        }
        return scoreMap;
    }

    /**
     * 可选：从原文档复制content/title等元数据
     */
    private static void copyDocMetadata(Document targetDoc, Object docId, List<Document>... docLists) {
        for (List<Document> docList : docLists) {
            if (docList == null) continue;
            for (Document doc : docList) {
                if (docId.equals(doc.getId())) {
                    targetDoc.setTitle(doc.getTitle());
                    targetDoc.setContent(doc.getContent());
                    return; // 找到即退出，优先取第一个渠道的元数据
                }
            }
        }
    }

}
