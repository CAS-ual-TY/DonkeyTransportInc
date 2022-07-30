package de.cas_ual_ty.donkey;

import com.mojang.logging.LogUtils;
import de.cas_ual_ty.donkey.cap.*;
import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(DonkeyTransportINC.MOD_ID)
public class DonkeyTransportINC
{
    public static final String MOD_ID = "donkey_transport_inc";
    
    public static final Logger LOGGER = LogUtils.getLogger();
    
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final RegistryObject<DonkeyHypnotizerTM> DONKEY_HYPNOTIZER_TM = ITEMS.register("donkey_hypnotizer_tm", () -> new DonkeyHypnotizerTM(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS)));
    
    public static Capability<IHypnotized> HYPNOTIZED_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static Capability<IWaypointsHolder> WAYPOINTS_HOLDER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static Capability<IWaypointsVisitor> WAYPOINTS_VISITOR_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    
    public DonkeyTransportINC()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        bus.addListener(DonkeyTransportINC::registerCapabilities);
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, DonkeyTransportINC::attachEntityCapabilities);
        MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, DonkeyTransportINC::attachItemCapabilities);
    }
    
    private static void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        event.register(IHypnotized.class);
    }
    
    private static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject() instanceof Donkey donkey)
        {
            Hypnotized hypnotized = new Hypnotized();
            attachCapability(event, hypnotized, HYPNOTIZED_CAPABILITY, "hypnotized");
            
            WaypointsVisitor waypointsVisitor = new WaypointsVisitor();
            attachCapability(event, waypointsVisitor, WAYPOINTS_VISITOR_CAPABILITY, "waypoints_visitor");
            attachCapability(event, waypointsVisitor, WAYPOINTS_HOLDER_CAPABILITY, "waypoints_holder");
        }
    }
    
    private static void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event)
    {
        if(event.getObject().getItem() == DONKEY_HYPNOTIZER_TM.get())
        {
            WaypointsHolder posAPosBHolder = new WaypointsHolder();
            attachCapability(event, posAPosBHolder, WAYPOINTS_HOLDER_CAPABILITY, "waypoints_holder");
        }
    }
    
    private static <T extends Tag, C extends INBTSerializable<T>> void attachCapability(AttachCapabilitiesEvent<?> event, C capData, Capability<C> capability, String name)
    {
        LazyOptional<C> optional = LazyOptional.of(() -> capData);
        ICapabilitySerializable<T> provider = new ICapabilitySerializable<>()
        {
            @Override
            public <S> LazyOptional<S> getCapability(Capability<S> cap, Direction side)
            {
                if(cap == capability)
                {
                    return optional.cast();
                }
                
                return LazyOptional.empty();
            }
            
            @Override
            public T serializeNBT()
            {
                return capData.serializeNBT();
            }
            
            @Override
            public void deserializeNBT(T tag)
            {
                capData.deserializeNBT(tag);
            }
        };
        
        event.addCapability(new ResourceLocation(MOD_ID, name), provider);
    }
}
