package de.cas_ual_ty.donkey.cap;

import de.cas_ual_ty.donkey.DonkeyTransportINC;
import de.cas_ual_ty.donkey.TransportGoal;
import net.minecraft.nbt.ByteTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Hypnotized implements IHypnotized
{
    private boolean isHypnotized;
    
    public Hypnotized()
    {
        isHypnotized = false;
    }
    
    @Override
    public boolean isHypnotized()
    {
        return isHypnotized;
    }
    
    @Override
    public void hypnotize()
    {
        isHypnotized = true;
    }
    
    @Override
    public void unhypnotize()
    {
        isHypnotized = false;
    }
    
    @Override
    public ByteTag serializeNBT()
    {
        return ByteTag.valueOf(isHypnotized);
    }
    
    @Override
    public void deserializeNBT(ByteTag nbt)
    {
        isHypnotized = nbt.getAsByte() == 1;
    }
    
    public static LazyOptional<Hypnotized> getHypnotized(LivingEntity entity)
    {
        return entity.getCapability(DonkeyTransportINC.HYPNOTIZED_CAPABILITY).cast();
    }
    
    public static void hypnotizeDonkey(Donkey donkey, WaypointsHolder waypointsHolderItem, Player player)
    {
        if(donkey.isTamed() && player.getUUID().equals(donkey.getOwnerUUID()))
        {
            getHypnotized(donkey).ifPresent(hypnotized ->
            {
                WaypointsVisitor.getWaypointsVisitor(donkey).ifPresent(waypointsVisitorDonkey ->
                {
                    donkey.tameWithName(player);
                    
                    hypnotized.hypnotize();
                    waypointsVisitorDonkey.deserializeNBT(waypointsHolderItem.serializeNBT());
                    
                    setNewDonkeyGoals(donkey);
                    player.level.playSound(null, donkey.blockPosition(), SoundEvents.DONKEY_CHEST, SoundSource.PLAYERS, 1F, 1F);
                });
            });
        }
    }
    
    private static void setNewDonkeyGoals(Donkey donkey)
    {
        donkey.goalSelector.removeAllGoals();
        donkey.goalSelector.addGoal(1, new TransportGoal(donkey));
        donkey.goalSelector.addGoal(0, new FloatGoal(donkey));
    }
    
    @SubscribeEvent
    public static void entityJoinLevel(EntityJoinLevelEvent event)
    {
        // re-hypnotize entity if it has capability
        // override AI goals
        
        if(!event.getLevel().isClientSide && event.getEntity() instanceof Donkey donkey)
        {
            getHypnotized(donkey).ifPresent(hypnotized ->
            {
                if(hypnotized.isHypnotized())
                {
                    setNewDonkeyGoals(donkey);
                }
            });
        }
    }
}
