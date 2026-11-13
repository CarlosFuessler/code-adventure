package com.adventure.quest;

import java.util.HashSet;
import java.util.Set;

public class PlayerProgress {
    private int boughtLevel = 0;
    private final Set<String> solved = new HashSet<>();

    public int getBoughtLevel() {
        return boughtLevel;
    }
    public void setBoughtLevel(int level) {
        this.boughtLevel = level;
    }
    public boolean isSolved(String problemId) {
        return solved.contains(problemId);
    }
    public void addSolved(String problemId) {
        solved.add(problemId);
    }
    public void markSolved(String problemId) {
        solved.add(problemId);
    }
    public Set<String> getSolved(){
        return solved;
    }
}
