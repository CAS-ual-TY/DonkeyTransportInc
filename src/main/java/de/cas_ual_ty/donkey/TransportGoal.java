package de.cas_ual_ty.donkey;

import de.cas_ual_ty.donkey.cap.IWaypointsVisitor;
import de.cas_ual_ty.donkey.cap.WaypointsVisitor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.common.util.LazyOptional;

public class TransportGoal extends Goal
{
    public static final double SPEED = 2D;
    public static final double MAX_DISTANCE_SQUARED = 6D;
    
    public final PathfinderMob mob;
    
    public TransportGoal(PathfinderMob mob)
    {
        this.mob = mob;
    }
    
    @Override
    public boolean canUse()
    {
        if(!hasVisitorCap())
        {
            return false;
        }
        
        IWaypointsVisitor visitor = forceGetVisitorCap();
        
        if(visitor.wasHurt())
        {
            visitor.setWasHurt(false);
            return true;
        }
        
        return !visitor.reachedDestination();
    }
    
    @Override
    public void start()
    {
        getVisitorCap().ifPresent(visitor ->
        {
            BlockPos pos = visitor.getCurrentWayPoint();
            
            if(pos != null)
            {
                moveMobToBlock(pos);
            }
        });
    }
    
    @Override
    public void stop()
    {
        getVisitorCap().ifPresent(visitor ->
        {
            if(visitor.reachedDestination())
            {
                visitor.toNextWaypoint();
            }
        });
    }
    
    @Override
    public void tick()
    {
        getVisitorCap().ifPresent(waypointsVisitor ->
        {
            BlockPos pos = waypointsVisitor.getCurrentWayPoint();
            
            if(pos != null)
            {
                waypointsVisitor.setReachedDestination(waypointsVisitor.getCurrentWayPoint().above().distToCenterSqr(mob.position()) <= MAX_DISTANCE_SQUARED);
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
        return mob.getNavigation().isInProgress() && (hasVisitorCap() && !forceGetVisitorCap().wasHurt());
    }
    
    private void moveMobToBlock(BlockPos blockPos)
    {
        mob.getNavigation().moveTo((double) blockPos.getX() + 0.5D, blockPos.getY() + 1, (double) blockPos.getZ() + 0.5D, SPEED);
    }
    
    private boolean hasVisitorCap()
    {
        return getVisitorCap().isPresent();
    }
    
    private IWaypointsVisitor forceGetVisitorCap()
    {
        return getVisitorCap().orElseThrow(IllegalArgumentException::new);
    }
    
    private LazyOptional<IWaypointsVisitor> getVisitorCap()
    {
        return WaypointsVisitor.getWaypointsVisitor(mob);
    }
}
