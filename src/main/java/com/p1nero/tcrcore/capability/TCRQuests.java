package com.p1nero.tcrcore.capability;

import com.p1nero.tcrcore.capability.TCRQuestManager.Quest;
import com.p1nero.tcrcore.utils.WorldUtil;
import com.p1nero.tcrcore.worldgen.TCRDimensions;
import net.minecraft.core.BlockPos;

public class TCRQuests {

    //First Join
    public static Quest TALK_TO_AINE_1;
    public static Quest TALK_TO_COL_1;

    public static void init() {
        TALK_TO_AINE_1 = TCRQuestManager.createTask("talk_to_aine_1").withTrackingPos(new BlockPos(WorldUtil.AINE_POS), TCRDimensions.SANCTUM_LEVEL_KEY);
        TALK_TO_COL_1 = TCRQuestManager.createTask("talk_to_col_1").withTrackingPos(new BlockPos(WorldUtil.COL_GUIDER_BLOCK_POS), TCRDimensions.SANCTUM_LEVEL_KEY);
    }
}
