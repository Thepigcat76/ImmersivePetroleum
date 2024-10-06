package flaxbeard.immersivepetroleum.client.render.tile.mb;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockRenderer;
import blusunrize.immersiveengineering.client.render.tile.IEMultiblockRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import flaxbeard.immersivepetroleum.client.render.IPRenderTypes;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.DistillationTowerLogic;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class DistillationTowerRenderer implements MultiblockRenderer<DistillationTowerLogic.State>  /*implements BlockEntityRenderer<DistillationTowerTileEntity>*/{
    @Override
    public boolean shouldRenderOffScreen(MultiblockBlockEntityMaster<DistillationTowerLogic.State> p_112306_) {
        return true;
    }

    @Override
    public void render(@NotNull IMultiblockContext<DistillationTowerLogic.State> ctx, float partialTicks, @NotNull PoseStack matrixStack, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        DistillationTowerLogic.State state = ctx.getState();
        if(ctx.isValid().getAsBoolean()){
//            if(ctx.shouldRenderAsActive()){
                combinedOverlayIn = OverlayTexture.NO_OVERLAY;

                matrixStack.pushPose();
                {
                    Direction rotation = ctx.getLevel().getOrientation().front();
                    switch(rotation){
                        case NORTH -> {
                            // transform.rotate(new Quaternion(0, 0, 0, true));
                            matrixStack.translate(3, 0, 4);
                        }
                        case SOUTH -> {
                            matrixStack.mulPose(new Quaternionf().rotateY(Mth.PI));
                            matrixStack.translate(2, 0, 3);
                        }
                        case EAST -> {
                            matrixStack.mulPose(new Quaternionf().rotateY(3*Mth.HALF_PI));
                            matrixStack.translate(3, 0, 3);
                        }
                        case WEST -> {
                            matrixStack.mulPose(new Quaternionf().rotateY(Mth.HALF_PI));
                            matrixStack.translate(2, 0, 4);
                        }
                        default -> {
                        }
                    }

                    float br = 0.75F; // "Brightness"

                    // Is it the most efficient way of doing this? Probably not.
                    // Does it make me look smart af? hell yeah..
                    VertexConsumer buf = bufferIn.getBuffer(IPRenderTypes.DISTILLATION_TOWER_ACTIVE);
                    if(ctx.getLevel().getOrientation().mirrored()){
                        matrixStack.pushPose();
                        {
                            matrixStack.translate(-4.0, 0.0, -4.0);
                            Matrix4f mat = matrixStack.last().pose();

                            // Active Boiler Front
                            int ux = 96, vy = 134;
                            int w = 32, h = 24;
                            float uw = w / 256F, vh = h / 256F, u0 = ux / 256F, v0 = vy / 256F, u1 = u0 + uw, v1 = v0 + vh;

                            buf.vertex(mat, -0.0015F, 0.5F, w / 16F)			.color(br, br, br, 1.0F).uv(u1, v1).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(1, 1, 1).endVertex();
                            buf.vertex(mat, -0.0015F, 0.5F + h / 16F, w / 16F)	.color(br, br, br, 1.0F).uv(u1, v0).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(1, 1, 1).endVertex();
                            buf.vertex(mat, -0.0015F, 0.5F + h / 16F, 0.0F)		.color(br, br, br, 1.0F).uv(u0, v0).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(1, 1, 1).endVertex();
                            buf.vertex(mat, -0.0015F, 0.5F, 0.0F)				.color(br, br, br, 1.0F).uv(u0, v1).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(1, 1, 1).endVertex();

                            // Active Boiler Back
                            ux = 96; vy = 158;
                            w = 32; h = 24;
                            uw = w / 256F; vh = h / 256F; u0 = ux / 256F; v0 = vy / 256F; u1 = u0 + uw; v1 = v0 + vh;

                            buf.vertex(mat, 1.0015F, 0.5F + h / 16F, 0.0F)		.color(br, br, br, 1.0F).uv(u1, v0).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(1, 1, 1).endVertex();
                            buf.vertex(mat, 1.0015F, 0.5F + h / 16F, w / 16F)	.color(br, br, br, 1.0F).uv(u0, v0).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(1, 1, 1).endVertex();
                            buf.vertex(mat, 1.0015F, 0.5F, w / 16F)				.color(br, br, br, 1.0F).uv(u0, v1).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(1, 1, 1).endVertex();
                            buf.vertex(mat, 1.0015F, 0.5F, 0.0F)				.color(br, br, br, 1.0F).uv(u1, v1).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(1, 1, 1).endVertex();

                            // Active Boiler Side
                            ux = 80; vy = 134;
                            w = 16; h = 24;
                            uw = w / 256F; vh = h / 256F; u0 = ux / 256F; v0 = vy / 256F; u1 = u0 + uw; v1 = v0 + vh;

                            buf.vertex(mat, w / 16F, 0.5F, 2.0015F)				.color(br, br, br, 1.0F).uv(u1, v1).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(1, 1, 1).endVertex();
                            buf.vertex(mat, w / 16F, 0.5F + h / 16F, 2.0015F)	.color(br, br, br, 1.0F).uv(u1, v0).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(1, 1, 1).endVertex();
                            buf.vertex(mat, 0.0F, 0.5F + h / 16F, 2.0015F)		.color(br, br, br, 1.0F).uv(u0, v0).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(1, 1, 1).endVertex();
                            buf.vertex(mat, 0.0F, 0.5F, 2.0015F)				.color(br, br, br, 1.0F).uv(u0, v1).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(1, 1, 1).endVertex();
                        }
                        matrixStack.popPose();

                    }else{
                        matrixStack.pushPose();
                        {
                            matrixStack.translate(-2.0, 0.0, -4.0);
                            Matrix4f mat = matrixStack.last().pose();

                            // Active Boiler Back
                            int ux = 96, vy = 158;
                            int w = 32, h = 24;
                            float uw = w / 256F, vh = h / 256F, u0 = ux / 256F, v0 = vy / 256F, u1 = u0 + uw, v1 = v0 + vh;

                            buf.vertex(mat, -0.0015F, 0.5F, w / 16F)			.color(br, br, br, 1.0F).uv(u0, v1).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(1, 1, 1).endVertex();
                            buf.vertex(mat, -0.0015F, 0.5F + h / 16F, w / 16F)	.color(br, br, br, 1.0F).uv(u0, v0).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(1, 1, 1).endVertex();
                            buf.vertex(mat, -0.0015F, 0.5F + h / 16F, 0.0F)		.color(br, br, br, 1.0F).uv(u1, v0).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(1, 1, 1).endVertex();
                            buf.vertex(mat, -0.0015F, 0.5F, 0.0F)				.color(br, br, br, 1.0F).uv(u1, v1).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(1, 1, 1).endVertex();

                            // Active Boiler Front
                            ux = 96; vy = 134;
                            w = 32; h = 24;
                            uw = w / 256F; vh = h / 256F; u0 = ux / 256F; v0 = vy / 256F; u1 = u0 + uw; v1 = v0 + vh;

                            buf.vertex(mat, 1.0015F, 0.5F + h / 16F, 0.0F)		.color(br, br, br, 1.0F).uv(u0, v0).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(1, 1, 1).endVertex();
                            buf.vertex(mat, 1.0015F, 0.5F + h / 16F, w / 16F)	.color(br, br, br, 1.0F).uv(u1, v0).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(1, 1, 1).endVertex();
                            buf.vertex(mat, 1.0015F, 0.5F, w / 16F)				.color(br, br, br, 1.0F).uv(u1, v1).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(1, 1, 1).endVertex();
                            buf.vertex(mat, 1.0015F, 0.5F, 0.0F)				.color(br, br, br, 1.0F).uv(u0, v1).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(1, 1, 1).endVertex();

                            // Active Boiler Side
                            ux = 80; vy = 134;
                            w = 16; h = 24;
                            uw = w / 256F; vh = h / 256F; u0 = ux / 256F; v0 = vy / 256F; u1 = u0 + uw; v1 = v0 + vh;

                            buf.vertex(mat, w / 16F, 0.5F, 2.0015F)				.color(br, br, br, 1.0F).uv(u0, v1).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(1, 1, 1).endVertex();
                            buf.vertex(mat, w / 16F, 0.5F + h / 16F, 2.0015F)	.color(br, br, br, 1.0F).uv(u0, v0).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(1, 1, 1).endVertex();
                            buf.vertex(mat, 0.0F, 0.5F + h / 16F, 2.0015F)		.color(br, br, br, 1.0F).uv(u1, v0).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(1, 1, 1).endVertex();
                            buf.vertex(mat, 0.0F, 0.5F, 2.0015F)				.color(br, br, br, 1.0F).uv(u1, v1).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(1, 1, 1).endVertex();
                        }
                        matrixStack.popPose();
                    }
//                }
                    matrixStack.popPose();
            }
        }
    }
}
