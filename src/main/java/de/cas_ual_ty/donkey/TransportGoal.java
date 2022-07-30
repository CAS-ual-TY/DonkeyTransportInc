package de.cas_ual_ty.donkey;

import de.cas_ual_ty.donkey.cap.WaypointsVisitor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TransportGoal extends Goal
{
    public static final double SPEED = 2D;
    public static final double MAX_DISTANCE_SQUARED = 6D;
    
    public final PathfinderMob mob;
    
    private boolean wasHurt;
    private boolean reachedDestination;
    
    public TransportGoal(PathfinderMob mob)
    {
        this.mob = mob;
        wasHurt = true;
        reachedDestination = false;
    }
    
    public void setWasHurt()
    {
        wasHurt = true;
    }
    
    @Override
    public boolean canUse()
    {
        if(WaypointsVisitor.getWaypointsVisitor(mob).isPresent() && (wasHurt || !reachedDestination))
        {
            wasHurt = false;
            return true;
        }
        
        return false;
    }
    
    @Override
    public void start()
    {
        WaypointsVisitor.getWaypointsVisitor(mob).ifPresent(waypointsVisitor ->
        {
            BlockPos pos = waypointsVisitor.getCurrentWayPoint();
            
            if(pos != null)
            {
                moveMobToBlock(pos);
            }
        });
    }
    
    @Override
    public void stop()
    {
        WaypointsVisitor.getWaypointsVisitor(mob).ifPresent(waypointsVisitor ->
        {
            if(reachedDestination)
            {
                waypointsVisitor.toNextWaypoint();
            }
        });
    }
    
    @Override
    public void tick()
    {
        WaypointsVisitor.getWaypointsVisitor(mob).ifPresent(waypointsVisitor ->
        {
            BlockPos pos = waypointsVisitor.getCurrentWayPoint();
            
            if(pos != null)
            {
                reachedDestination = waypointsVisitor.getCurrentWayPoint().above().distToCenterSqr(mob.position()) <= MAX_DISTANCE_SQUARED;
            }
        });
    }
    
    @Override
    public boolean requiresUpdateEveryTick()
    {
        return true;
    }
    
    @Override
    public boolean canContinueToUse()
    {
        return mob.getNavigation().isInProgress() && !wasHurt;
    }
    
    protected void moveMobToBlock(BlockPos blockPos)
    {
        mob.getNavigation().moveTo((double) blockPos.getX() + 0.5D, blockPos.getY() + 1, (double) blockPos.getZ() + 0.5D, SPEED);
    }
    
    @SubscribeEvent
    public static void livingHurt(LivingHurtEvent event)
    {
        if(!event.getEntity().level.isClientSide && event.getEntity() instanceof PathfinderMob mob)
        {
            mob.goalSelector.getAvailableGoals().stream().forEach(goal ->
            {
                if(goal.getGoal() instanceof TransportGoal transportGoal)
                {
                    transportGoal.setWasHurt();
                }
            });
        }
    }
}
