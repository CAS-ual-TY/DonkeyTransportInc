package de.cas_ual_ty.donkey;

import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

public interface IWaypointsVisitor extends IWaypointsHolder
{
    @Nullable
    BlockPos getCurrentWayPoint();
    
    void toNextWaypoint();
}
