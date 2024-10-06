package flaxbeard.immersivepetroleum.client.render.tile.mb;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import flaxbeard.immersivepetroleum.client.model.IPModel;
import flaxbeard.immersivepetroleum.client.model.IPModels;
import flaxbeard.immersivepetroleum.client.model.ModelPumpjack;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.PumpjackLogic;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class PumpjackRenderer implements MultiblockRenderer<PumpjackLogic.State> {
    private static final Supplier<IPModel> PUMPJACK_ARM = IPModels.getSupplier(ModelPumpjack.ID);

    @Override
    public int getViewDistance() {
        return 100;
    }

    @Override
    public void render(@NotNull IMultiblockContext<PumpjackLogic.State> ctx, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        PumpjackLogic.State state = ctx.getState();
        poseStack.pushPose();
        Direction rotation = ctx.getLevel().getOrientation().front();
        switch (rotation) {
            case NORTH -> {
                poseStack.mulPose(new Quaternionf().rotateY(Mth.HALF_PI));
                poseStack.translate(-6, 0, -1);
            }
            case EAST -> poseStack.translate(-5, 0, -1);
            case SOUTH -> {
                poseStack.mulPose(new Quaternionf().rotateY(3 * Mth.HALF_PI));
                poseStack.translate(-5, 0, -2);
            }
            case WEST -> {
                poseStack.mulPose(new Quaternionf().rotateY(Mth.PI));
                poseStack.translate(-6, 0, -2);
            }
            default -> {
            }
        }

        ModelPumpjack model;
        if ((model = (ModelPumpjack) PUMPJACK_ARM.get()) != null) {
            float ticks = state.activeTicks + (state.wasActive ? partialTicks : 0);
            model.ticks = 1.5F * ticks;

            model.renderToBuffer(poseStack, bufferIn.getBuffer(model.renderType(ModelPumpjack.TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        poseStack.popPose();

    }
}
