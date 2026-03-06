package com.jsyl.utils;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class RecommendationEngine {

    public double calculateHotScore(int viewCount, int acceptCount, int commentCount, LocalDateTime createTime) {
        double viewWeight = 1.0;
        double acceptWeight = 5.0;
        double commentWeight = 3.0;

        double baseScore = viewCount * viewWeight + acceptCount * acceptWeight + commentCount * commentWeight;

        long hoursSinceCreation = ChronoUnit.HOURS.between(createTime, LocalDateTime.now());
        double timeDecay = Math.pow(0.95, hoursSinceCreation / 24.0);

        return baseScore * timeDecay;
    }

    public Map<Long, Double> calculateUserSimilarity(Map<Long, Set<Long>> userItems) {
        Map<Long, Double> similarity = new HashMap<>();

        for (Map.Entry<Long, Set<Long>> entry1 : userItems.entrySet()) {
            for (Map.Entry<Long, Set<Long>> entry2 : userItems.entrySet()) {
                if (entry1.getKey().equals(entry2.getKey())) {
                    continue;
                }

                Set<Long> items1 = entry1.getValue();
                Set<Long> items2 = entry2.getValue();

                Set<Long> intersection = new HashSet<>(items1);
                intersection.retainAll(items2);

                Set<Long> union = new HashSet<>(items1);
                union.addAll(items2);

                double jaccardSimilarity = union.isEmpty() ? 0 : (double) intersection.size() / union.size();
                similarity.put(entry2.getKey(), jaccardSimilarity);
            }
        }

        return similarity;
    }

    public List<Long> getTopNRecommendations(Map<Long, Double> scores, int n) {
        return scores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(n)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public double calculateContentSimilarity(Set<String> tags1, Set<String> tags2) {
        if (tags1.isEmpty() || tags2.isEmpty()) {
            return 0.0;
        }

        Set<String> intersection = new HashSet<>(tags1);
        intersection.retainAll(tags2);

        Set<String> union = new HashSet<>(tags1);
        union.addAll(tags2);

        return union.isEmpty() ? 0 : (double) intersection.size() / union.size();
    }
}
