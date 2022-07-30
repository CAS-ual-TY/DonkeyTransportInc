package de.cas_ual_ty.donkey;

import de.cas_ual_ty.donkey.cap.Hypnotized;
import de.cas_ual_ty.donkey.cap.WaypointsHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DonkeyHypnotizerTM extends Item
{
    public static final String BY_KEY = ".by";
    public static final String COMPANY_KEY = ".company";
    public static final String INSTRUCTIONS_KEY = ".instructions";
    public static final String INSTRUCTION_1_KEY = ".instruction.start";
    public static final String INSTRUCTION_2_KEY = ".instruction.waypoint";
    public static final String INSTRUCTION_3_KEY = ".instruction.hypnotize";
    
    public static final String SHARE_TAG_KEY = "shared_waypoints_holder";
    
    public DonkeyHypnotizerTM(Properties properties)
    {
        super(properties);
    }
    
    @Override
    public boolean onLeftClickEntity(ItemStack itemStack, Player player, Entity entity)
    {
        if(entity instanceof Donkey donkey)
        {
            if(!entity.level.isClientSide)
            {
                WaypointsHolder.getWaypointsHolder(itemStack).ifPresent(waypointsHolder ->
                {
                    Hypnotized.hypnotizeDonkey(donkey, waypointsHolder, player);
                });
            }
            
            return true;
        }
        
        return super.onLeftClickEntity(itemStack, player, entity);
    }
    
    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
    {
        if(!context.getLevel().getBlockState(context.getClickedPos()).isAir())
        {
            clickBlock(context.getLevel(), context.getItemInHand(), context.getClickedPos(), false);
            return InteractionResult.SUCCESS;
        }
        
        return super.onItemUseFirst(stack, context);
    }
    
    public void clickBlock(Level level, ItemStack itemStack, BlockPos pos, boolean left)
    {
        if(!level.isClientSide)
        {
            WaypointsHolder.getWaypointsHolder(itemStack).ifPresent(waypointsHolder ->
            {
                if(left)
                {
                    waypointsHolder.start(pos);
                }
                else
                {
                    waypointsHolder.addWaypoint(pos);
                }
            });
        }
    }
    
    @Override
    public CompoundTag getShareTag(ItemStack itemStack)
    {
        CompoundTag superTag = super.getShareTag(itemStack);
        CompoundTag finalTag = superTag != null ? superTag : new CompoundTag();
        
        WaypointsHolder.getWaypointsHolder(itemStack).ifPresent(waypointsHolder ->
        {
            finalTag.put(SHARE_TAG_KEY, waypointsHolder.serializeNBT());
        });
        
        return finalTag;
    }
    
    @Override
    public void readShareTag(ItemStack stack, CompoundTag nbt)
    {
        super.readShareTag(stack, nbt);
        
        if(nbt != null)
        {
            WaypointsHolder.getWaypointsHolder(stack).ifPresent(waypointsHolder ->
            {
                waypointsHolder.deserializeNBT(nbt.getCompound(SHARE_TAG_KEY));
            });
        }
    }
    
    @Override
    public void appendHoverText(ItemStack itemStack, Level level, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(Component.empty()
                .append(Component.translatable(getDescriptionId() + BY_KEY).withStyle(ChatFormatting.GRAY))
                .append(" ")
                .append(Component.translatable(getDescriptionId() + COMPANY_KEY).withStyle(ChatFormatting.YELLOW))
        );
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable(getDescriptionId() + INSTRUCTIONS_KEY).withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.translatable(getDescriptionId() + INSTRUCTION_1_KEY));
        tooltip.add(Component.translatable(getDescriptionId() + INSTRUCTION_2_KEY));
        tooltip.add(Component.translatable(getDescriptionId() + INSTRUCTION_3_KEY));
    }
    
    @Override
    public Rarity getRarity(ItemStack p_41461_)
    {
        return Rarity.EPIC;
    }
    
    public static void entityInteract(PlayerInteractEvent.EntityInteract event)
    {
    
    }
    
    @SubscribeEvent
    public static void leftClickBlock(PlayerInteractEvent.LeftClickBlock event)
    {
        if(event.getItemStack().getItem() == DonkeyTransportINC.DONKEY_HYPNOTIZER_TM.get())
        {
            event.setCanceled(true);
            DonkeyTransportINC.DONKEY_HYPNOTIZER_TM.get().clickBlock(event.getLevel(), event.getItemStack(), event.getPos(), true);
        }
    }
    
    @SubscribeEvent
    public static void entityStruckByLightning(EntityStruckByLightningEvent event)
    {
        if(!event.getEntity().level.isClientSide && event.getEntity() instanceof Donkey)
        {
            event.getEntity().spawnAtLocation(new ItemStack(DonkeyTransportINC.DONKEY_HYPNOTIZER_TM.get()));
        }
    }
}
