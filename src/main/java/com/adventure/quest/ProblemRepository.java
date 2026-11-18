package com.adventure.quest;

import com.adventure.Code_Adventure;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ProblemRepository {
    private static final String PROBLEMS_PATH = "/data/code_adventure/problems.md";
    private static List<Problem> PROBLEMS = List.of();

    static {
        loadProblems();
    }

    public static void loadProblems() {
        try (InputStream in = ProblemRepository.class.getResourceAsStream(PROBLEMS_PATH)) {
            if (in == null) {
                Code_Adventure.LOGGER.error("Could not find problems file: {}", PROBLEMS_PATH);
                PROBLEMS = List.of();
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                PROBLEMS = parseProblems(reader);
                Code_Adventure.LOGGER.info("Loaded {} coding problems", PROBLEMS.size());
            }
        } catch (Exception e) {
            Code_Adventure.LOGGER.error("Failed to load problems", e);
            PROBLEMS = List.of();
        }
    }

    private static List<Problem> parseProblems(BufferedReader reader) throws Exception {
        List<Problem> result = new ArrayList<>();

        String line;
        boolean inProblem = false;

        String id = null, title = null, prompt = null, correct = null, explanation = "";
        int level = 0, xp = 0;
        Map<String, String> options = new LinkedHashMap<>();

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.equals("## problem")) {
                // save previous problem if valid
                if (inProblem) {
                    addIfValid(result, id, level, title, prompt, xp, options, correct, explanation);
                }
                // reset for new problem
                inProblem = true;
                id = null; title = null; prompt = null; correct = null; explanation = "";
                level = 0; xp = 0;
                options = new LinkedHashMap<>();
                continue;
            }

            if (!inProblem || line.isEmpty() || line.equals("---") || line.startsWith("#")) {
                continue;
            }

            if (!line.startsWith("- ")) continue;
            String entry = line.substring(2);

            if (entry.startsWith("option ")) {
                // format: option A: text
                String rest = entry.substring("option ".length()); // "A: text"
                int colon = rest.indexOf(':');
                if (colon >= 0) {
                    String key = rest.substring(0, colon).trim();      // A
                    String value = rest.substring(colon + 1).trim();   // text
                    options.put(key, value);
                }
                continue;
            }

            int colon = entry.indexOf(':');
            if (colon < 0) continue;

            String key = entry.substring(0, colon).trim();
            String value = entry.substring(colon + 1).trim();

            switch (key) {
                case "id" -> id = value;
                case "level" -> level = Integer.parseInt(value);
                case "xp" -> xp = Integer.parseInt(value);
                case "title" -> title = value;
                case "prompt" -> prompt = value;
                case "correct" -> correct = value.toUpperCase(Locale.ROOT);
                case "explanation" -> explanation = value;
            }
        }

        // add last problem
        if (inProblem) {
            addIfValid(result, id, level, title, prompt, xp, options, correct, explanation);
        }

        result.sort(Comparator.comparingInt(Problem::level));
        return List.copyOf(result);
    }

    private static void addIfValid(List<Problem> result, String id, int level, String title, String prompt,
                                   int xp, Map<String, String> options, String correct, String explanation) {
        if (id == null || title == null || prompt == null || correct == null || options.isEmpty()) {
            Code_Adventure.LOGGER.warn("Skipping invalid problem definition (id={})", id);
            return;
        }
        result.add(new Problem(id, level, title, prompt, xp, Map.copyOf(options), correct, explanation));
    }

    public static Problem getByLevel(int level) {
        return PROBLEMS.stream().filter(p -> p.level() == level).findFirst().orElse(null);
    }

    public static Problem getById(String id) {
        return PROBLEMS.stream().filter(p -> p.id().equals(id)).findFirst().orElse(null);
    }

    public static int maxLevel() {
        return PROBLEMS.stream().mapToInt(Problem::level).max().orElse(0);
    }
}