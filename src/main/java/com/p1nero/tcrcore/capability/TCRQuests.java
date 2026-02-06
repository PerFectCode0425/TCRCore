package com.p1nero.tcrcore.capability;

import com.p1nero.tcrcore.capability.TCRQuestManager.Quest;
import com.p1nero.tcrcore.entity.TCREntities;
import com.p1nero.tcrcore.utils.WorldUtil;
import com.p1nero.tcrcore.worldgen.TCRDimensions;
import net.minecraft.core.BlockPos;

public class TCRQuests {

    //First Join
    public static Quest TALK_TO_AINE_1;
    public static Quest TALK_TO_CHRONOS_1;

    public static void init() {
        TALK_TO_AINE_1 = TCRQuestManager.create("talk_to_aine_1")
                .titleParam(TCREntities.AINE_IRIS.get().getDescription())
                .withTrackingPos(new BlockPos(WorldUtil.AINE_POS.above(3)), TCRDimensions.SANCTUM_LEVEL_KEY);
        TALK_TO_CHRONOS_1 = TCRQuestManager.create("talk_to_col_1")
                .withTrackingPos(new BlockPos(WorldUtil.CHRONOS_SOL_BLOCK_POS.above(3)), TCRDimensions.SANCTUM_LEVEL_KEY);
    }
}
