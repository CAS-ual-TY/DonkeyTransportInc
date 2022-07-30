package de.cas_ual_ty.donkey.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.cas_ual_ty.donkey.cap.WaypointsHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WaypointsRenderer
{
    @SubscribeEvent
    public static void renderWorld(RenderLevelStageEvent event)
    {
        if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS)
        {
            Player player = Minecraft.getInstance().player;
            
            if(player == null)
            {
                return;
            }
            
            for(InteractionHand hand : InteractionHand.values())
            {
                ItemStack itemStack = player.getItemInHand(hand);
                
                WaypointsHolder.getWaypointsHolder(itemStack).ifPresent(waypointsHolder ->
                {
                    Vec3 pos = event.getCamera().getPosition();
                    PoseStack poseStack = event.getPoseStack();
                    
                    poseStack.pushPose();
                    poseStack.translate(-pos.x(), -pos.y(), -pos.z());
                    
                    waypointsHolder.forEach(blockPos -> renderBlockPos(player.level, poseStack, blockPos, 1F, 1F, 1F));
                    
                    poseStack.popPose();
                });
            }
            
            // F3+B
            if(Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes())
            {
                Vec3 pos = event.getCamera().getPosition();
                PoseStack poseStack = event.getPoseStack();
                
                poseStack.pushPose();
                poseStack.translate(-pos.x(), -pos.y(), -pos.z());
                
                for(Donkey mob : player.level.getEntitiesOfClass(Donkey.class, player.getBoundingBox().inflate(50D)))
                {
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
                            BlockPos above = new BlockPos(x, aboveY, z);
                            renderBlockPos(player.level, poseStack, below, 1F, 0F, 0F);
                            renderBlockPos(player.level, poseStack, above, 1F, 0F, 0F);
                        }
                    }
                }
                
                poseStack.popPose();
            }
        }
    }
    
    private static void renderBlockPos(Level level, PoseStack poseStack, BlockPos blockPos, float r, float g, float b)
    {
        RenderSystem.lineWidth(10F);
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.LINES);
        
        AABB aabb = AABB.unitCubeFromLowerCorner(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        
        LevelRenderer.renderLineBox(poseStack, vertexConsumer, aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ, r, g, b, 1F);
        RenderSystem.disableDepthTest();
        bufferSource.endBatch(RenderType.LINES);
    }
}
