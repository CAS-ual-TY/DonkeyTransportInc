package de.cas_ual_ty.donkey.cap;

import de.cas_ual_ty.donkey.DonkeyTransportINC;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WaypointsVisitor extends WaypointsHolder implements IWaypointsVisitor
{
    public static final String KEY_CURRENT_WAYPOINT = "current_waypoint";
    public static final String KEY_WAS_HURT = "was_hurt";
    public static final String KEY_REACHED_DESTINATION = "reached_destination";
    
    private int waypoint;
    private boolean wasHurt;
    private boolean reachedDestination;
    
    public WaypointsVisitor()
    {
        waypoint = 0;
        wasHurt = false;
        reachedDestination = false;
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
    public boolean wasHurt()
    {
        return wasHurt;
    }
    
    @Override
    public void setWasHurt(boolean wasHurt)
    {
        this.wasHurt = wasHurt;
    }
    
    @Override
    public boolean reachedDestination()
    {
        return reachedDestination;
    }
    
    @Override
    public void setReachedDestination(boolean reachedDestination)
    {
        this.reachedDestination = reachedDestination;
    }
    
    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = super.serializeNBT();
        tag.putInt(KEY_CURRENT_WAYPOINT, waypoint);
        tag.putBoolean(KEY_WAS_HURT, wasHurt);
        tag.putBoolean(KEY_REACHED_DESTINATION, reachedDestination);
        return tag;
    }
    
    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        super.deserializeNBT(nbt);
        waypoint = nbt.getInt(KEY_CURRENT_WAYPOINT);
        wasHurt = nbt.getBoolean(KEY_WAS_HURT);
        reachedDestination = nbt.getBoolean(KEY_REACHED_DESTINATION);
    }
    
    @SubscribeEvent
    public static void livingHurt(LivingHurtEvent event)
    {
        if(!event.getEntity().level.isClientSide)
        {
            getWaypointsVisitor(event.getEntity()).ifPresent(visitor ->
            {
                visitor.setWasHurt(true);
            });
        }
    }
    
    public static LazyOptional<IWaypointsVisitor> getWaypointsVisitor(LivingEntity entity)
    {
        return entity.getCapability(DonkeyTransportINC.WAYPOINTS_VISITOR_CAPABILITY).cast();
    }
}
