package de.cas_ual_ty.donkey;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.function.IntPredicate;

public class TransferItemsGoal extends Goal
{
    public final Donkey mob;
    
    private int cooldown;
    
    public TransferItemsGoal(Donkey mob)
    {
        this.mob = mob;
        cooldown = 0;
    }
    
    @Override
    public boolean canUse()
    {
        return mob.hasChest();
    }
    
    @Override
    public void tick()
    {
        // just to be sure
        if(!mob.hasChest())
        {
            return;
        }
        
        if(cooldown > 0)
        {
            --cooldown;
            return;
        }
        
        boolean found = false;
        
        AABB aab = mob.getBoundingBox();
        
        int minX = (int) Math.floor(aab.minX);
        int maxX = (int) Math.floor(aab.maxX);
        int minZ = (int) Math.floor(aab.minZ);
        int maxZ = (int) Math.floor(aab.maxZ);
        
        int belowY = ((int) Math.floor(aab.minY)) - 1;
        int aboveY = ((int) Math.ceil(aab.maxY));
        
        for(int x = minX; x <= maxX; x++)
        {
            for(int z = minZ; z <= maxZ; z++)
            {
                BlockPos below = new BlockPos(x, belowY, z);
                found = found || insertOnBlockPos(below);
            }
        }
        
        for(int x = minX; x <= maxX; x++)
        {
            for(int z = minZ; z <= maxZ; z++)
            {
                BlockPos above = new BlockPos(x, aboveY, z);
                found = found || ejectOnBlockPos(above);
            }
        }
        
        if(found)
        {
            cooldown = HopperBlockEntity.MOVE_ITEM_SPEED;
        }
    }
    
    private boolean ejectOnBlockPos(BlockPos blockPos)
    {
        if(mob.level.getBlockEntity(blockPos) instanceof HopperBlockEntity hopper && !mob.level.getBlockState(blockPos).hasProperty(BlockStateProperties.POWERED))
        {
            // 0 is saddle slot, 1 is reserved
            if(moveFromOneContainerToOther(hopper, mob.inventory, slot -> true, slot -> slot >= 2))
            {
                mob.containerChanged(mob.inventory);
                hopper.setChanged();
                return true;
            }
        }
        
        return false;
    }
    
    private boolean insertOnBlockPos(BlockPos blockPos)
    {
        if(mob.level.getBlockEntity(blockPos) instanceof HopperBlockEntity hopper)
        {
            // 0 is saddle slot, 1 is reserved
            if(moveFromOneContainerToOther(mob.inventory, hopper, slot -> slot >= 2, slot -> true))
            {
                mob.containerChanged(mob.inventory);
                hopper.setChanged();
                return true;
            }
        }
        
        return false;
    }
    
    private boolean moveFromOneContainerToOther(Container from, Container to, IntPredicate permittedFromSlot, IntPredicate permittedToSlot)
    {
        for(int fromSlot = 0; fromSlot < from.getContainerSize(); fromSlot++)
        {
            if(!permittedFromSlot.test(fromSlot))
            {
                continue;
            }
            
            for(int toSlot = 0; toSlot < to.getContainerSize(); toSlot++)
            {
                if(!permittedToSlot.test(toSlot))
                {
                    continue;
                }
                
                ItemStack original = from.getItem(fromSlot).copy();
                ItemStack destination = to.getItem(toSlot);
                
                if(!original.isEmpty())
                {
                    if(destination.isEmpty())
                    {
                        ItemStack itemStack = from.removeItem(fromSlot, 1);
                        to.setItem(toSlot, itemStack);
                        return true;
                    }
                    else if(ItemHandlerHelper.canItemStacksStack(original, destination) && original.getCount() < original.getMaxStackSize())
                    {
                        ItemStack itemStack = from.removeItem(fromSlot, 1);
                        destination.grow(1);
                        to.setItem(toSlot, destination);
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    @Override
    public boolean canContinueToUse()
    {
        return mob.hasChest();
    }
    
    @Override
    public boolean requiresUpdateEveryTick()
    {
        return true;
    }
}
