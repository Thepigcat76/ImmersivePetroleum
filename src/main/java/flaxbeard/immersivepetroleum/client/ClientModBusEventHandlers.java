package flaxbeard.immersivepetroleum.client;

import java.util.function.Supplier;

import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockRenderer;
import blusunrize.immersiveengineering.client.ClientProxy;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.client.render.tile.AutoLubricatorRenderer;
import flaxbeard.immersivepetroleum.client.render.entity.MotorboatRenderer;
import flaxbeard.immersivepetroleum.client.render.tile.SeismicSurveyBarrelRenderer;
import flaxbeard.immersivepetroleum.client.render.tile.mb.DerrickRenderer;
import flaxbeard.immersivepetroleum.client.render.tile.mb.DistillationTowerRenderer;
import flaxbeard.immersivepetroleum.client.render.tile.mb.OilTankRenderer;
import flaxbeard.immersivepetroleum.client.render.tile.mb.PumpjackRenderer;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.DistillationTowerLogic;
import flaxbeard.immersivepetroleum.common.entity.IPEntityTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;

@EventBusSubscriber(modid = ImmersivePetroleum.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class ClientModBusEventHandlers {
    @SubscribeEvent
    public static void registerRenders(EntityRenderersEvent.RegisterRenderers ev) {
        // FIXME Find a way to replace this
        registerBERenderNoContext(ev, IPContent.Multiblock.DISTILLATIONTOWER.masterBE(), DistillationTowerRenderer::new);
        registerBERenderNoContext(ev, IPContent.Multiblock.PUMPJACK.masterBE(), PumpjackRenderer::new);
        registerBERenderNoContext(ev, IPContent.Multiblock.OILTANK.masterBE(), OilTankRenderer::new);
        registerBERenderNoContext(ev, IPContent.Multiblock.DERRICK.masterBE(), DerrickRenderer::new);

        registerBERender(ev, IPTileTypes.AUTOLUBE.get(), AutoLubricatorRenderer::new);
        registerBERender(ev, IPTileTypes.SEISMIC_SURVEY.get(), SeismicSurveyBarrelRenderer::new);

        registerEntityRenderingHandler(ev, IPEntityTypes.MOTORBOAT, MotorboatRenderer::new);
        registerEntityRenderingHandler(ev, IPEntityTypes.MOLOTOV, ThrownItemRenderer::new);
    }

    private static <T extends BlockEntity> void registerBERenderNoContext(
            RegisterRenderers event, Supplier<BlockEntityType<? extends T>> type, Supplier<BlockEntityRenderer<T>> render
    ) {
        registerBERenderNoContext(event, type.get(), render);
    }

    private static <T extends BlockEntity> void registerBERenderNoContext(
            RegisterRenderers event, BlockEntityType<? extends T> type, Supplier<BlockEntityRenderer<T>> render
    ) {
        event.registerBlockEntityRenderer(type, $ -> render.get());
    }

    private static <T extends BlockEntity> void registerBERender(RegisterRenderers ev, BlockEntityType<T> type, Supplier<BlockEntityRenderer<T>> factory) {
        ev.registerBlockEntityRenderer(type, ctx -> factory.get());
    }

    private static <T extends Entity, T2 extends T> void registerEntityRenderingHandler(EntityRenderersEvent.RegisterRenderers ev, Supplier<EntityType<T2>> type, EntityRendererProvider<T> renderer) {
        ev.registerEntityRenderer(type.get(), renderer);
    }
}
