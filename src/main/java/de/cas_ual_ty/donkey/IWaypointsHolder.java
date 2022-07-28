package de.cas_ual_ty.donkey;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.function.Consumer;

public interface IWaypointsHolder extends INBTSerializable<CompoundTag>
{
    int getWaypointsAmount();
    
    BlockPos getWaypoint(int index);
    
    void forEach(Consumer<BlockPos> consumer);
    
    void start(BlockPos pos);
    
    void addWaypoint(BlockPos pos);
}
