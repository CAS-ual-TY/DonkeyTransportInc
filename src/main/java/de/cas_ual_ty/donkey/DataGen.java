package de.cas_ual_ty.donkey;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGen
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event)
    {
        event.getGenerator().addProvider(true, new ModelGen(event.getGenerator(), DonkeyTransportINC.MOD_ID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(true, new LangGen(event.getGenerator(), DonkeyTransportINC.MOD_ID, "en_us"));
    }
    
    private static class ModelGen extends ItemModelProvider
    {
        public ModelGen(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper)
        {
            super(generator, modid, existingFileHelper);
        }
        
        @Override
        protected void registerModels()
        {
            basicItem(DonkeyTransportINC.DONKEY_HYPNOTIZER_TM.get());
        }
    }
    
    private static class LangGen extends LanguageProvider
    {
        public LangGen(DataGenerator gen, String modid, String locale)
        {
            super(gen, modid, locale);
        }
        
        @Override
        protected void addTranslations()
        {
            add(DonkeyTransportINC.DONKEY_HYPNOTIZER_TM.get(), "Donkey Hypnotizer™");
            add(DonkeyTransportINC.DONKEY_HYPNOTIZER_TM.get().getDescriptionId() + DonkeyHypnotizerTM.BY_KEY, "by");
            add(DonkeyTransportINC.DONKEY_HYPNOTIZER_TM.get().getDescriptionId() + DonkeyHypnotizerTM.COMPANY_KEY, "DONKEY TRANSPORT INC.");
            add(DonkeyTransportINC.DONKEY_HYPNOTIZER_TM.get().getDescriptionId() + DonkeyHypnotizerTM.INSTRUCTIONS_KEY, "Instructions:");
            add(DonkeyTransportINC.DONKEY_HYPNOTIZER_TM.get().getDescriptionId() + DonkeyHypnotizerTM.INSTRUCTION_1_KEY, "- Left click a block to set the 1st waypoint");
            add(DonkeyTransportINC.DONKEY_HYPNOTIZER_TM.get().getDescriptionId() + DonkeyHypnotizerTM.INSTRUCTION_2_KEY, "- Right click a block to add waypoints to it");
            add(DonkeyTransportINC.DONKEY_HYPNOTIZER_TM.get().getDescriptionId() + DonkeyHypnotizerTM.INSTRUCTION_3_KEY, "- Then left click a tamed Donkey to hypnotize it");
        }
    }
}
