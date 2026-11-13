package com.adventure.quest;

import java.util.List;
public class ProblemRepository {
    private static final List<Problem> PROBLEMS = List.of(
            new Problem("java_1", 1, "Hello Return",
                    "Write a function that returns 42.",
                    "public class Solution {\\n    public static int solve() {\\n        // TODO\\n        return 0;\\n    }\\n}\\n\"",
                    "java",
                    10
                    )
    );
    public static Problem getByLevel(int level) {
        return PROBLEMS.stream().filter(p -> p.level() == level).findFirst().orElse(null);
    }

    public static Problem getById(String id) {
        return PROBLEMS.stream().filter(p -> p.id().equals(id)).findFirst().orElse(null);
    }

    public static int maxLevel(){
        return PROBLEMS.stream().mapToInt(Problem::level).max().orElse(0);
    }

}
