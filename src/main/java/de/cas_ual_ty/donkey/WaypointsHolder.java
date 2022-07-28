package de.cas_ual_ty.donkey;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WaypointsHolder implements IWaypointsHolder
{
    public static final String KEY_WAYPOINTS_LIST = "waypoints";
    
    private List<BlockPos> waypoints;
    
    public WaypointsHolder()
    {
        waypoints = new ArrayList<>();
    }
    
    @Override
    public int getWaypointsAmount()
    {
        return waypoints.size();
    }
    
    @Override
    public BlockPos getWaypoint(int index)
    {
        return index >= 0 && index < waypoints.size() ? waypoints.get(index) : null;
    }
    
    @Override
    public void start(BlockPos pos)
    {
        waypoints.clear();
        waypoints.add(pos);
    }
    
    @Override
    public void addWaypoint(BlockPos pos)
    {
        waypoints.add(pos);
    }
    
    @Override
    public void forEach(Consumer<BlockPos> consumer)
    {
        waypoints.forEach(consumer);
    }
    
    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        waypoints.forEach(pos -> list.add(NbtUtils.writeBlockPos(pos)));
        tag.put(KEY_WAYPOINTS_LIST, list);
        return tag;
    }
    
    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        ListTag list = nbt.getList(KEY_WAYPOINTS_LIST, Tag.TAG_COMPOUND);
        waypoints.clear();
        list.stream().map(t -> (CompoundTag) t).forEach(t -> waypoints.add(NbtUtils.readBlockPos(t)));
    }
    
    public static LazyOptional<WaypointsHolder> getWaypointsHolder(LivingEntity entity)
    {
        return entity.getCapability(DonkeyTransportINC.WAYPOINTS_HOLDER_CAPABILITY).cast();
    }
    
    public static LazyOptional<WaypointsHolder> getWaypointsHolder(ItemStack itemStack)
    {
        return itemStack.getCapability(DonkeyTransportINC.WAYPOINTS_HOLDER_CAPABILITY).cast();
    }
}
