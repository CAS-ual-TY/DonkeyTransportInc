package de.cas_ual_ty.donkey;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;

public class WaypointsVisitor extends WaypointsHolder implements IWaypointsVisitor
{
    public static final String KEY_CURRENT_WAYPOINT = "current_waypoint";
    
    private int waypoint;
    
    public WaypointsVisitor()
    {
        waypoint = 0;
    }
    
    @Override
    public BlockPos getCurrentWayPoint()
    {
        return getWaypoint(waypoint);
    }
    
    @Override
    public void toNextWaypoint()
    {
        if(getWaypointsAmount() > 0)
        {
            waypoint = (waypoint + 1) % getWaypointsAmount();
        }
    }
    
    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = super.serializeNBT();
        tag.putInt(KEY_CURRENT_WAYPOINT, waypoint);
        return tag;
    }
    
    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        super.deserializeNBT(nbt);
        if(nbt.contains(KEY_CURRENT_WAYPOINT))
        {
            waypoint = nbt.getInt(KEY_CURRENT_WAYPOINT);
        }
        else
        {
            waypoint = 0;
        }
    }
    
    public static LazyOptional<WaypointsVisitor> getWaypointsVisitor(LivingEntity entity)
    {
        return entity.getCapability(DonkeyTransportINC.WAYPOINTS_VISITOR_CAPABILITY).cast();
    }
}
