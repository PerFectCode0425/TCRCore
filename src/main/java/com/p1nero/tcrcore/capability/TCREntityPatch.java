package com.p1nero.tcrcore.capability;

import net.minecraft.nbt.CompoundTag;

public class TCREntityPatch {

    private boolean fighting;

    public void setFighting(boolean fighting) {
        this.fighting = fighting;
    }

    public boolean isFighting() {
        return fighting;
    }

    public void saveNBTData(CompoundTag tag) {
        tag.putBoolean("fighting", fighting);
    }

    public void loadNBTData(CompoundTag tag) {
        fighting = tag.getBoolean("fighting");
    }
}
