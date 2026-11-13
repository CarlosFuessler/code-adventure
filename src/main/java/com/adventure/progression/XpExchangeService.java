package com.adventure.progression;

import com.adventure.quest.PlayerProgress;
import com.adventure.quest.Problem;
import com.adventure.quest.ProblemRepository;
import com.adventure.quest.ProgressSavedData;
import net.minecraft.server.level.ServerPlayer;

public class XpExchangeService {
    public enum BuyResult{
        SUCCESS,
        NOT_ENOUGH_XP,
        MAX_LEVEL_REACHED
    }
    public static BuyResult tryBuyLevel(ServerPlayer player){
        ProgressSavedData data = ProgressSavedData.get(player.server);
        PlayerProgress progress = data.getOrCreate((player.getUUID()));

        int nextLevel = progress.getBoughtLevel() +1;
        Problem problem = ProblemRepository.getByLevel(nextLevel);
        if(problem == null){
            return BuyResult.MAX_LEVEL_REACHED;
        }
        int cost = problem.xpCost();
        if(player.experienceLevel < cost){
            return BuyResult.NOT_ENOUGH_XP;
        }

        player.giveExperienceLevels(-cost);
        progress.setBoughtLevel(nextLevel);
        data.setDirty();
        return BuyResult.SUCCESS;
    }
}
