package com.adventure.quest;

public record Problem (
        String id,
        int level,
        String title,
        String prompt,
        String starterCode,
        String language,
        int xpCost
){}


