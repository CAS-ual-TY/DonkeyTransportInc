package de.cas_ual_ty.donkey.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Donkey.class)
public abstract class DonkeyMixin extends AbstractChestedHorse implements Container
{
    // account for saddle and chest slot
    private static final int SKIP_SLOTS = 2;
    
    protected DonkeyMixin(EntityType<? extends AbstractChestedHorse> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }
    
    @Override
    public int getContainerSize()
    {
        return inventory.getContainerSize() - SKIP_SLOTS;
    }
    
    @Override
    public boolean isEmpty()
    {
        for(int i = SKIP_SLOTS; i < inventory.getContainerSize(); i++)
        {
            if(!inventory.getItem(i).isEmpty())
            {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public @NotNull ItemStack getItem(int slot)
    {
        return inventory.getItem(slot + SKIP_SLOTS);
    }
    
    @Override
    public @NotNull ItemStack removeItem(int slot, int amount)
    {
        return inventory.removeItem(slot + SKIP_SLOTS, amount);
    }
    
    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot)
    {
        return inventory.removeItemNoUpdate(slot + SKIP_SLOTS);
    }
    
    @Override
    public void setItem(int slot, @NotNull ItemStack itemStack)
    {
        inventory.setItem(slot + SKIP_SLOTS, itemStack);
    }
    
    @Override
    public void setChanged()
    {
        inventory.setChanged();
    }
    
    @Override
    public boolean stillValid(@NotNull Player player)
    {
        return inventory.stillValid(player);
    }
    
    @Override
    public void clearContent()
    {
        inventory.clearContent();
    }
}
