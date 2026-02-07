package com.p1nero.tcrcore.client.gui.screen;

import com.p1nero.tcrcore.TCRCoreMod;
import com.p1nero.tcrcore.capability.PlayerDataManager;
import com.p1nero.tcrcore.capability.TCRCapabilityProvider;
import com.p1nero.tcrcore.capability.TCRQuestManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TCRQuestScreen extends Screen {

    private static List<TCRQuestManager.Quest> quests;

    private static TCRQuestManager.Quest selectedQuest; // 当前真正追踪的任务
    private static TCRQuestManager.Quest uiSelectedQuest; // UI 中当前高亮的任务

    private LocalPlayer player;

    public static final ResourceLocation TASK_ICON_TEXTURE = ResourceLocation.fromNamespaceAndPath(TCRCoreMod.MOD_ID, "textures/gui/task_icon.png");

    private static final int LIST_WIDTH = 220;
    private static final int LIST_MARGIN_VERTICAL = 24;
    private static final int ENTRY_PADDING = 4;
    private static final int ICON_SIZE = 20;
    private static final int BOTTOM_AREA_HEIGHT = 48;

    private int listX0;
    private int listX1;
    private int listY0;
    private int listY1;

    private int detailX0;
    private int detailX1;
    private int detailY0;
    private int detailY1;

    private double scrollAmount;
    private boolean scrolling;
    private double scrollGrabOffset;

    public TCRQuestScreen() {
        super(Component.empty());
        player = Minecraft.getInstance().player;
        if (player == null) {
            Minecraft.getInstance().setScreen(null);
            return;
        }
        refreshSelectedQuest();
        uiSelectedQuest = selectedQuest;
    }

    @Override
    protected void init() {
        super.init();
        refreshSelectedQuest();
        uiSelectedQuest = selectedQuest;
        int listHeight = this.height - LIST_MARGIN_VERTICAL - BOTTOM_AREA_HEIGHT;
        if (listHeight < 40) {
            listHeight = 40;
        }
        int margin = 40;
        int gap = 12;
        int targetPanelWidth = 520;
        int maxPanelWidth = this.width - margin * 2;
        int panelWidth = Math.min(targetPanelWidth, maxPanelWidth);
        if (panelWidth < 200) {
            panelWidth = maxPanelWidth;
        }
        int contentWidth = panelWidth - gap;
        int listWidth = (int) (contentWidth * 0.382F);
        int detailWidth = contentWidth - listWidth;
        int panelLeft = (this.width - panelWidth) / 2;
        listX0 = panelLeft;
        listX1 = listX0 + listWidth;
        listY0 = LIST_MARGIN_VERTICAL;
        listY1 = listY0 + listHeight;
        detailY0 = listY0;
        detailY1 = listY1;
        detailX0 = listX1 + gap;
        detailX1 = detailX0 + detailWidth;
        scrollAmount = 0.0;
        scrolling = false;
        scrollGrabOffset = 0.0;
        ensureSelectedQuestVisible();
        int buttonWidth = 80;
        int buttonHeight = 20;
        int buttonY = listY1 + (BOTTOM_AREA_HEIGHT - buttonHeight) / 2;
        int centerX = this.width / 2;
        int startButtonX = centerX - buttonWidth - 8;
        int exitButtonX = centerX + 8;
        this.addRenderableWidget(Button.builder(TCRCoreMod.getInfo("start_tracking_quest"), button -> this.startTrackingSelectedQuest()).bounds(startButtonX, buttonY, buttonWidth, buttonHeight).build());
        this.addRenderableWidget(Button.builder(TCRCoreMod.getInfo("exit_quest_screen"), button -> this.onClose()).bounds(exitButtonX, buttonY, buttonWidth, buttonHeight).build());
    }

    public void setSelectedQuest(TCRQuestManager.Quest quest) {
        if (isEmptyQuest(quest)) {
            return;
        }
        uiSelectedQuest = quest;
        ensureSelectedQuestVisible();
    }

    public static TCRQuestManager.Quest getSelectedQuest() {
        if(selectedQuest == null) {
            refreshSelectedQuest();
        }
        return selectedQuest;
    }

    public static List<TCRQuestManager.Quest> getQuests() {
        if(quests == null) {
            refreshSelectedQuest();
        }
        return quests;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        if (quests == null || !hasRenderableQuests()) {
            guiGraphics.drawCenteredString(font, TCRCoreMod.getInfo("no_quest"), width / 2, height / 2 - font.lineHeight / 2, 0xFFFFFFFF);
            super.render(guiGraphics, mouseX, mouseY, partialTick);
            return;
        }
        int contentHeight = getContentHeight();
        int visibleHeight = listY1 - listY0;
        int maxScroll = Math.max(0, contentHeight - visibleHeight);
        if (scrollAmount < 0.0) {
            scrollAmount = 0.0;
        } else if (scrollAmount > maxScroll) {
            scrollAmount = maxScroll;
        }
        guiGraphics.enableScissor(listX0, listY0, listX1, listY1);
        int y = listY0 - (int) scrollAmount;
        int index = 0;
        for (TCRQuestManager.Quest quest : quests) {
            if (isEmptyQuest(quest)) {
                continue;
            }
            boolean selected = uiSelectedQuest != null && uiSelectedQuest.getId() == quest.getId();
            int entryHeight = getEntryHeight(quest, selected);
            int entryTop = y;
            int entryBottom = y + entryHeight;
            if (entryBottom >= listY0 && entryTop <= listY1) {
                boolean hovered = mouseX >= listX0 && mouseX <= listX1 && mouseY >= entryTop && mouseY <= entryBottom;
                renderQuestEntry(guiGraphics, quest, index, entryTop, entryHeight, hovered, selected);
            }
            y += entryHeight;
            index++;
        }
        guiGraphics.disableScissor();
        renderListBorder(guiGraphics);
        renderDetailPanel(guiGraphics);
        renderScrollbar(guiGraphics, contentHeight);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private int getContentHeight() {
        if (quests == null) {
            return 0;
        }
        int heightSum = 0;
        for (TCRQuestManager.Quest quest : quests) {
            if (isEmptyQuest(quest)) {
                continue;
            }
            boolean selected = uiSelectedQuest != null && uiSelectedQuest.getId() == quest.getId();
            heightSum += getEntryHeight(quest, selected);
        }
        return heightSum;
    }

    private int getEntryHeight(TCRQuestManager.Quest quest, boolean selected) {
        int heightBase = 0;
        heightBase += ENTRY_PADDING; // top padding
        heightBase += font.lineHeight; // title
        heightBase += 2; // title to shortDesc gap
        heightBase += font.lineHeight; // shortDesc
        heightBase += 4; // shortDesc to bottom gap
        heightBase += ENTRY_PADDING; // bottom padding
        return heightBase;
    }

    private void renderQuestEntry(GuiGraphics guiGraphics, TCRQuestManager.Quest quest, int index, int top, int entryHeight, boolean hovered, boolean selected) {
        int left = listX0;
        int right = listX1;
        int backgroundColor;
        if (selected) {
            backgroundColor = 0x804080FF;
        } else if (hovered) {
            backgroundColor = 0x404080FF;
        } else {
            backgroundColor = 0x40000000;
        }
        guiGraphics.fill(left, top, right, top + entryHeight, backgroundColor);
        if (selected || hovered) {
            int borderColor = selected ? 0xFFFFFFFF : 0xFFAAAAAA;
            guiGraphics.fill(left, top, right, top + 1, borderColor);
            guiGraphics.fill(left, top + entryHeight - 1, right, top + entryHeight, borderColor);
            guiGraphics.fill(left, top, left + 1, top + entryHeight, borderColor);
            guiGraphics.fill(right - 1, top, right, top + entryHeight, borderColor);
        }
        int iconX = left + ENTRY_PADDING;
        int iconY = top + ENTRY_PADDING;
        ResourceLocation currentIcon = quest.getIcon() == null ? TASK_ICON_TEXTURE : quest.getIcon();
        guiGraphics.blit(currentIcon, iconX, iconY, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        int textX = iconX + ICON_SIZE + ENTRY_PADDING;
        int textY = top + ENTRY_PADDING;
        int titleColor = selected ? 0xFFFFE070 : 0xFFFFFFFF;
        int shortDescColor = 0xFFAAAAAA;
        guiGraphics.drawString(font, quest.getTitle(), textX, textY, titleColor, false);
        if (selectedQuest != null && selectedQuest.getId() == quest.getId()) {
            Component tracking = TCRCoreMod.getInfo("tracking_quest");
            int titleWidth = font.width(quest.getTitle());
            int trackX = textX + titleWidth + 6;
            guiGraphics.drawString(font, tracking, trackX, textY, 0xFF00FF80, false);
        }
        textY += font.lineHeight + 2;
        guiGraphics.drawString(font, quest.getShortDesc(), textX, textY, shortDescColor, false);
        int separatorColor = 0x60FFFFFF;
        guiGraphics.fill(left + 2, top + entryHeight - 1, right - 2, top + entryHeight, separatorColor);
    }

    private void renderScrollbar(GuiGraphics guiGraphics, int contentHeight) {
        int visibleHeight = listY1 - listY0;
        if (contentHeight <= visibleHeight) {
            return;
        }
        int barWidth = 6;
        int x0 = listX1 - barWidth;
        int x1 = listX1;
        guiGraphics.fill(x0, listY0, x1, listY1, 0x80000000);
        int thumbHeight = getThumbHeight(contentHeight, visibleHeight);
        int thumbTop = listY0 + getThumbTop(contentHeight, visibleHeight, thumbHeight);
        int thumbBottom = thumbTop + thumbHeight;
        guiGraphics.fill(x0, thumbTop, x1, thumbBottom, 0xFFC0C0C0);
    }

    private int getThumbHeight(int contentHeight, int visibleHeight) {
        int minThumbHeight = 32;
        int thumbHeight = (int) ((double) visibleHeight * (double) visibleHeight / (double) contentHeight);
        if (thumbHeight < minThumbHeight) {
            thumbHeight = minThumbHeight;
        }
        if (thumbHeight > visibleHeight) {
            thumbHeight = visibleHeight;
        }
        return thumbHeight;
    }

    private int getThumbTop(int contentHeight, int visibleHeight, int thumbHeight) {
        if (contentHeight <= visibleHeight) {
            return 0;
        }
        int maxScroll = contentHeight - visibleHeight;
        double scrollRatio = scrollAmount / (double) maxScroll;
        int maxThumbOffset = visibleHeight - thumbHeight;
        return (int) (scrollRatio * maxThumbOffset);
    }

    private boolean isMouseOverList(double mouseX, double mouseY) {
        return mouseX >= listX0 && mouseX <= listX1 && mouseY >= listY0 && mouseY <= listY1;
    }

    private boolean isMouseOverScrollbar(double mouseX, double mouseY, int contentHeight) {
        int visibleHeight = listY1 - listY0;
        if (contentHeight <= visibleHeight) {
            return false;
        }
        int barWidth = 6;
        return mouseX >= listX1 - barWidth && mouseX <= listX1 && mouseY >= listY0 && mouseY <= listY1;
    }

    private TCRQuestManager.Quest getQuestAtPosition(double mouseY) {
        if (quests == null) {
            return null;
        }
        int y = listY0 - (int) scrollAmount;
        for (TCRQuestManager.Quest quest : quests) {
            if (isEmptyQuest(quest)) {
                continue;
            }
            boolean selected = uiSelectedQuest != null && uiSelectedQuest.getId() == quest.getId();
            int entryHeight = getEntryHeight(quest, selected);
            if (mouseY >= y && mouseY <= y + entryHeight) {
                return quest;
            }
            y += entryHeight;
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isMouseOverList(mouseX, mouseY)) {
            int contentHeight = getContentHeight();
            if (isMouseOverScrollbar(mouseX, mouseY, contentHeight)) {
                int visibleHeight = listY1 - listY0;
                int thumbHeight = getThumbHeight(contentHeight, visibleHeight);
                int thumbTop = listY0 + getThumbTop(contentHeight, visibleHeight, thumbHeight);
                scrollGrabOffset = mouseY - thumbTop;
                scrolling = true;
                return true;
            } else {
                TCRQuestManager.Quest clicked = getQuestAtPosition(mouseY);
                if (clicked != null) {
                    setSelectedQuest(clicked);
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (scrolling && button == 0) {
            int contentHeight = getContentHeight();
            int visibleHeight = listY1 - listY0;
            if (contentHeight > visibleHeight) {
                int thumbHeight = getThumbHeight(contentHeight, visibleHeight);
                int maxScroll = contentHeight - visibleHeight;
                int maxThumbOffset = visibleHeight - thumbHeight;
                double thumbPos = Mth.clamp(mouseY - listY0 - scrollGrabOffset, 0.0, maxThumbOffset);
                double scrollRatio = maxThumbOffset > 0 ? thumbPos / (double) maxThumbOffset : 0.0;
                scrollAmount = scrollRatio * maxScroll;
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && scrolling) {
            scrolling = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta != 0.0 && isMouseOverList(mouseX, mouseY)) {
            int contentHeight = getContentHeight();
            int visibleHeight = listY1 - listY0;
            int maxScroll = Math.max(0, contentHeight - visibleHeight);
            if (maxScroll > 0) {
                scrollAmount = Mth.clamp(scrollAmount - delta * 20.0, 0.0, maxScroll);
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private void ensureSelectedQuestVisible() {
        if (uiSelectedQuest == null) {
            return;
        }
        int visibleHeight = listY1 - listY0;
        if (visibleHeight <= 0) {
            return;
        }
        int contentHeight = getContentHeight();
        int maxScroll = Math.max(0, contentHeight - visibleHeight);
        int y = 0;
        if (quests == null) {
            return;
        }
        for (TCRQuestManager.Quest quest : quests) {
            if (isEmptyQuest(quest)) {
                continue;
            }
            boolean selected = uiSelectedQuest != null && uiSelectedQuest.getId() == quest.getId();
            int entryHeight = getEntryHeight(quest, selected);
            if (selected) {
                int entryTop = y;
                int entryBottom = y + entryHeight;
                if (entryTop < scrollAmount) {
                    scrollAmount = entryTop;
                } else if (entryBottom > scrollAmount + visibleHeight) {
                    scrollAmount = entryBottom - visibleHeight;
                }
                scrollAmount = Mth.clamp(scrollAmount, 0.0, maxScroll);
                return;
            }
            y += entryHeight;
        }
    }

    private void startTrackingSelectedQuest() {
        if (player == null || uiSelectedQuest == null || isEmptyQuest(uiSelectedQuest)) {
            return;
        }
        selectedQuest = uiSelectedQuest;
        PlayerDataManager.currentQuestId.put(player, selectedQuest.getId());
        Minecraft.getInstance().setScreen(null);
    }

    public static void refreshSelectedQuest() {
        LocalPlayer player = Minecraft.getInstance().player;
        if(player == null) {
            return;
        }
        quests = TCRCapabilityProvider.getTCRPlayer(player).getCurrentQuests();
        selectedQuest = TCRQuestManager.getCurrentQuest(player);
//        TCRQuestManager.ensureQuest(player);
    }

    private static boolean isEmptyQuest(TCRQuestManager.Quest quest) {
        return quest == null || quest.equals(TCRQuestManager.EMPTY);
    }

    private boolean hasRenderableQuests() {
        if (quests == null || quests.isEmpty()) {
            return false;
        }
        for (TCRQuestManager.Quest quest : quests) {
            if (!isEmptyQuest(quest)) {
                return true;
            }
        }
        return false;
    }

    private void renderListBorder(GuiGraphics guiGraphics) {
        int outer = 0xFF444444;
        int inner = 0xFFAAAAAA;
        int x0 = listX0 - 4;
        int x1 = listX1 + 4;
        int y0 = listY0 - 4;
        int y1 = listY1 + 4;
        guiGraphics.fill(x0, y0, x1, y0 + 1, outer);
        guiGraphics.fill(x0, y1 - 1, x1, y1, outer);
        guiGraphics.fill(x0, y0, x0 + 1, y1, outer);
        guiGraphics.fill(x1 - 1, y0, x1, y1, outer);
        guiGraphics.fill(x0 + 1, y0 + 1, x1 - 1, y0 + 2, inner);
        guiGraphics.fill(x0 + 1, y1 - 2, x1 - 1, y1 - 1, inner);
        guiGraphics.fill(x0 + 1, y0 + 1, x0 + 2, y1 - 1, inner);
        guiGraphics.fill(x1 - 2, y0 + 1, x1 - 1, y1 - 1, inner);
        if (detailX1 > detailX0 + 8) {
            int dx0 = detailX0 - 4;
            int dx1 = detailX1 + 4;
            int dy0 = detailY0 - 4;
            int dy1 = detailY1 + 4;
            guiGraphics.fill(dx0, dy0, dx1, dy0 + 1, outer);
            guiGraphics.fill(dx0, dy1 - 1, dx1, dy1, outer);
            guiGraphics.fill(dx0, dy0, dx0 + 1, dy1, outer);
            guiGraphics.fill(dx1 - 1, dy0, dx1, dy1, outer);
            guiGraphics.fill(dx0 + 1, dy0 + 1, dx1 - 1, dy0 + 2, inner);
            guiGraphics.fill(dx0 + 1, dy1 - 2, dx1 - 1, dy1 - 1, inner);
            guiGraphics.fill(dx0 + 1, dy0 + 1, dx0 + 2, dy1 - 1, inner);
            guiGraphics.fill(dx1 - 2, dy0 + 1, dx1 - 1, dy1 - 1, inner);
        }
    }

    private void renderDetailPanel(GuiGraphics guiGraphics) {
        if (detailX1 <= detailX0 + 8 || detailY1 <= detailY0 + 8) {
            return;
        }
        int bg = 0x40000000;
        guiGraphics.fill(detailX0, detailY0, detailX1, detailY1, bg);
        if (uiSelectedQuest == null || isEmptyQuest(uiSelectedQuest)) {
            return;
        }
        int padding = 8;
        int x = detailX0 + padding;
        int y = detailY0 + padding;
        int titleColor = 0xFFFFE070;
        int shortDescColor = 0xFFAAAAAA;
        int descColor = 0xFFDDDDDD;
        guiGraphics.drawString(font, uiSelectedQuest.getTitle(), x, y, titleColor, false);
        y += font.lineHeight + 4;
        guiGraphics.drawString(font, uiSelectedQuest.getShortDesc(), x, y, shortDescColor, false);
        y += font.lineHeight + 8;
        int textWidth = detailX1 - x - padding;
        if (textWidth < 40) {
            textWidth = 40;
        }
        List<FormattedCharSequence> descLines = font.split(uiSelectedQuest.getDesc(), textWidth);
        for (FormattedCharSequence line : descLines) {
            if (y > detailY1 - padding - font.lineHeight) {
                break;
            }
            guiGraphics.drawString(font, line, x, y, descColor, false);
            y += font.lineHeight + 1;
        }
    }
}
