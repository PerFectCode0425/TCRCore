package com.p1nero.tcrcore.client.gui;

import com.brass_amber.ba_bt.block.blockentity.spawner.BTAbstractSpawnerBlockEntity;
import com.mojang.blaze3d.platform.Window;
import com.p1nero.tcrcore.capability.TCRQuests;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

public class BTSpawnerBlockIndicatorRenderer {

    private static final Set<BlockPos> TARGET_SPAWNER_BLOCKS = new HashSet<>();

    public static final ResourceLocation QUEST_ICON = TCRQuests.SIDE_QUEST_1;

    public static boolean hasTarget() {
        return !TARGET_SPAWNER_BLOCKS.isEmpty();
    }

    public static void addTargetSpawner(BlockPos pos) {
        TARGET_SPAWNER_BLOCKS.add(pos);
    }

    public static void removeTargetSpawner(BlockPos pos) {
        TARGET_SPAWNER_BLOCKS.remove(pos);
    }

    public static void tick() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) return;

        if (!TARGET_SPAWNER_BLOCKS.isEmpty()) {
            TARGET_SPAWNER_BLOCKS.removeIf(pos -> !(minecraft.level.getBlockEntity(pos) instanceof BTAbstractSpawnerBlockEntity));
        }
    }

    public static void render(LocalPlayer localPlayer, GuiGraphics guiGraphics, float partialTick) {
        if (TARGET_SPAWNER_BLOCKS.isEmpty()) return;
        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();
        Camera camera = minecraft.gameRenderer.getMainCamera();
        Vec3 playerPos = camera.getPosition();
        Vec3 forward = Vec3.directionFromRotation(camera.getXRot(), camera.getYRot());
        Vec3 upWorld = new Vec3(0.0, 1.0, 0.0);
        Vec3 right = forward.cross(upWorld).normalize();
        Vec3 up = right.cross(forward).normalize();

        float fovDegrees = minecraft.options.fov().get();
        double fovRad = Math.toRadians(fovDegrees);
        double tanHalfFovY = Math.tan(fovRad / 2.0);
        int screenWidth = window.getGuiScaledWidth();
        int screenHeight = window.getGuiScaledHeight();
        double aspect = (double) screenWidth / (double) screenHeight;
        double tanHalfFovX = tanHalfFovY * aspect;

        int iconSize;

        for (BlockPos pos : TARGET_SPAWNER_BLOCKS) {
            Vec3 targetPos = Vec3.atCenterOf(pos);
            Vec3 camToTarget = targetPos.subtract(playerPos);
            double zCam = camToTarget.dot(forward);
            double distance = playerPos.distanceTo(targetPos);
            iconSize = (int) (16 - distance / 2.5);
            if (zCam > 0.1) {
                double xCam = camToTarget.dot(right);
                double yCam = camToTarget.dot(up);

                double nx = xCam / (zCam * tanHalfFovX);
                double ny = yCam / (zCam * tanHalfFovY);

                // Check if inside screen
                if (nx >= -1.0 && nx <= 1.0 && ny >= -1.0 && ny <= 1.0) {
                    float screenX = (float) ((nx * 0.5 + 0.5) * screenWidth);
                    float screenY = (float) ((-ny * 0.5 + 0.5) * screenHeight);

                    int drawX = (int) (screenX - iconSize / 2);
                    int drawY = (int) (screenY - iconSize / 2);

                    guiGraphics.blit(QUEST_ICON, drawX, drawY, 0, 0, iconSize, iconSize, iconSize, iconSize);

                    String distanceText = (int) distance + "m";
                    int textWidth = minecraft.font.width(distanceText);
                    int textX = (int) (screenX - textWidth / 2);
                    int textY = (int) (screenY + iconSize / 2 + 2);

                    guiGraphics.drawString(minecraft.font, distanceText, textX, textY, 0xFFFFFFFF, true);
                }
            }
        }
    }
}
