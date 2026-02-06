package com.p1nero.tcrcore.capability;

import com.p1nero.fast_tpa.network.PacketRelay;
import com.p1nero.tcrcore.network.TCRPacketHandler;
import com.p1nero.tcrcore.network.packet.clientbound.RefreshClientQuestsPacket;
import com.p1nero.tcrcore.utils.WorldUtil;
import com.p1nero.tcrcore.worldgen.TCRDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TCRQuestManager {
    public static final int NO_QUEST = 0;
    public static final String PRE = "tcrcore.task.";
    public static final Map<Integer, Quest> QUEST_MAP = new HashMap<>();
    public static int id = 0;
    public static Quest EMPTY;
    public static void init() {
        QUEST_MAP.clear();
        EMPTY = createTask("empty");//不知道为嘛默认0改不了= =
        TCRQuests.init();
    }

    public static Quest getQuestById(int id) {
        return QUEST_MAP.getOrDefault(id, EMPTY);
    }

    public static Quest getCurrentQuest(Player player) {
        return getQuestById(PlayerDataManager.currentQuestId.getInt(player));
    }

    public static int getCurrentQuestId(Player player) {
        return PlayerDataManager.currentQuestId.getInt(player);
    }

    public static Quest createTask(String desc) {
        Quest quest = new Quest(id, PRE + desc);
        QUEST_MAP.put(id, quest);
        id++;
        return quest;
    }

    public static boolean hasQuest(Player player) {
        return PlayerDataManager.currentQuestId.get(player) != NO_QUEST;
    }

    public static void startQuest(ServerPlayer player, Quest quest) {
        if(quest.equals(EMPTY)) {
            return;
        }
        TCRPlayer tcrPlayer = TCRCapabilityProvider.getTCRPlayer(player);
        tcrPlayer.addQuest(quest);
        PlayerDataManager.currentQuestId.put(player, quest.getId());
        PacketRelay.sendToPlayer(TCRPacketHandler.INSTANCE, new RefreshClientQuestsPacket(), player);
        tcrPlayer.syncToClient(player);
    }

    public static void finishQuest(ServerPlayer player, Quest quest) {
        int currentQuestId = PlayerDataManager.currentQuestId.getInt(player);
        if(quest.getId() != currentQuestId) {
            return;
        }
        TCRPlayer tcrPlayer = TCRCapabilityProvider.getTCRPlayer(player);
        tcrPlayer.finishQuest(quest);
        List<Quest> currentQuests = tcrPlayer.getCurrentQuests();
        if(!currentQuests.isEmpty()) {
            PlayerDataManager.currentQuestId.put(player, currentQuests.get(0).getId());
        } else {
            PlayerDataManager.currentQuestId.put(player, EMPTY.getId());
        }
        PacketRelay.sendToPlayer(TCRPacketHandler.INSTANCE, new RefreshClientQuestsPacket(), player);
        tcrPlayer.syncToClient(player);
    }

    public static boolean hasFinished(ServerPlayer player, Quest quest) {
        TCRPlayer tcrPlayer = TCRCapabilityProvider.getTCRPlayer(player);
        return tcrPlayer.hasFinished(quest);
    }

    public static Component getCurrentQuestShortDesc(Player player) {
        if(!hasQuest(player)) {
            return Component.empty();
        }
        int id = PlayerDataManager.currentQuestId.getInt(player);
        return QUEST_MAP.getOrDefault(id, EMPTY).shortDesc;
    }

    public static class Quest {

        private final int id;
        private final String key;
        private final Component shortDesc;
        private final Component desc;
        private final Component title;
        private ResourceLocation icon;
        private BlockPos trackingPos;
        private ResourceKey<Level> dimension;

        public Quest(int id, String key) {
            this.id = id;
            this.key = key;
            desc = Component.translatable(key + ".desc");
            shortDesc = Component.translatable(key + ".short_desc");
            title = Component.translatable(key + ".title");
        }

        public void start(ServerPlayer player) {
            TCRQuestManager.startQuest(player, this);
        }

        public void finish(ServerPlayer player) {
            TCRQuestManager.finishQuest(player, this);
        }

        public boolean isFinished(ServerPlayer serverPlayer) {
            return TCRQuestManager.hasFinished(serverPlayer, this);
        }

        @Nullable
        public ResourceLocation getIcon() {
            return icon;
        }

        public BlockPos getTrackingPos() {
            return trackingPos;
        }

        public ResourceKey<Level> getDimension() {
            return dimension;
        }

        public Component getTitle() {
            return title;
        }

        public Component getShortDesc() {
            return shortDesc;
        }

        public Component getDesc() {
            return desc;
        }

        public String getKey() {
            return key;
        }

        public int getId() {
            return id;
        }

        public Quest withIcon(ResourceLocation icon) {
            this.icon = icon;
            return this;
        }

        public Quest withTrackingPos(BlockPos trackingPos, ResourceKey<Level> dimension) {
            this.trackingPos = trackingPos;
            this.dimension = dimension;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Quest quest = (Quest) o;
            return id == quest.id;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }
    }
}
