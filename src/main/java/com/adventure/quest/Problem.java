package com.adventure.quest;

import java.util.Map;

public record Problem(
        String id,
        int level,
        String title,
        String prompt,
        int xpCost,
        Map<String, String> options,
        String correctOption,
        String explanation
) {
}
