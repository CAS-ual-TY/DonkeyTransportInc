package de.cas_ual_ty.donkey.cap;

import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

public interface IWaypointsVisitor extends IWaypointsHolder
{
    @Nullable
    BlockPos getCurrentWayPoint();
    
    void toNextWaypoint();
    
    boolean wasHurt();
    
    void setWasHurt(boolean wasHurt);
    
    boolean reachedDestination();
    
    void setReachedDestination(boolean reachedDestination);
}
