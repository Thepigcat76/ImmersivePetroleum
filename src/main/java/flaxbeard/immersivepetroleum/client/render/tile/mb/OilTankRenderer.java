package flaxbeard.immersivepetroleum.client.render.tile.mb;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockRenderer;
import blusunrize.immersiveengineering.client.utils.GuiHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import flaxbeard.immersivepetroleum.client.render.IPRenderTypes;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.OilTankLogic;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class OilTankRenderer implements MultiblockRenderer<OilTankLogic.State> {
    @Override
    public boolean shouldRenderOffScreen(MultiblockBlockEntityMaster<OilTankLogic.State> p_112306_) {
        return true;
    }

    @Override
    public void render(@NotNull IMultiblockContext<OilTankLogic.State> ctx, float partialTicks, @NotNull PoseStack matrixStack, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (!ctx.isValid().getAsBoolean()/*|| te.isDummy() || !te.getLevelNonnull().hasChunkAt(te.getBlockPos())*/)
            return;

        OilTankLogic.State state = ctx.getState();

        combinedOverlayIn = OverlayTexture.NO_OVERLAY;

        matrixStack.pushPose();
        {
            switch (ctx.getLevel().getOrientation().front()) {
                case EAST -> {
                    matrixStack.mulPose(new Quaternionf().rotateY(3 * Mth.HALF_PI));
                    matrixStack.translate(0, 0, -1);
                }
                case SOUTH -> {
                    matrixStack.mulPose(new Quaternionf().rotateY(Mth.PI));
                    matrixStack.translate(-1, 0, -1);
                }
                case WEST -> {
                    matrixStack.mulPose(new Quaternionf().rotateY(Mth.HALF_PI));
                    matrixStack.translate(-1, 0, 0);
                }
                default -> {
                }
            }

            // Tank Display
            matrixStack.pushPose();
            {
                matrixStack.translate(1, 2, 2.995F);

                // Background
                Matrix4f mat = matrixStack.last().pose();
                VertexConsumer builder = bufferIn.getBuffer(IPRenderTypes.TRANSLUCENT_POSITION_COLOR);
                builder.vertex(mat, 1.5F, -0.5F, 0.0F).color(34, 34, 34, 255).endVertex();
                builder.vertex(mat, 1.5F, 1F, 0.0F).color(34, 34, 34, 255).endVertex();
                builder.vertex(mat, 0F, 1F, 0.0F).color(34, 34, 34, 255).endVertex();
                builder.vertex(mat, 0F, -0.5F, 0.0F).color(34, 34, 34, 255).endVertex();

                FluidStack fs = state.tank.getFluid();
                if (!fs.isEmpty()) {
                    matrixStack.pushPose();
                    {
                        matrixStack.translate(0.25, 0.875, 0.0025F);
                        matrixStack.scale(0.0625F, -0.0625F, 0.0625F);

                        float h = fs.getAmount() / (float) state.tank.getCapacity();
                        GuiHelper.drawRepeatedFluidSprite(bufferIn.getBuffer(RenderType.solid()), matrixStack, fs, 0, 0 + (1 - h) * 16, 16, h * 16);
                    }
                    matrixStack.popPose();
                }
            }
            matrixStack.popPose();

            matrixStack.pushPose();
            {
                // Dynamic Fluid IO Ports
                if (ctx.getLevel().getOrientation().mirrored()) {
                    for (OilTankLogic.Port port : OilTankLogic.Port.DYNAMIC_PORTS) {
                        matrixStack.pushPose();
                        {
                            // TODO: Might not work as expected
                            BlockPos p = ctx.getLevel().getOrientation().getPosInMB(port.posInMultiblock);
//                            BlockPos p = port.posInMultiblock.subtract(te.posInMultiblock);
                            matrixStack.mulPose(new Quaternionf().rotateY(Mth.PI));
                            matrixStack.translate(p.getX() - 1, p.getY(), -p.getZ() - 1);
                            quad(matrixStack, bufferIn, state.portConfig.get(port), port.posInMultiblock.getX() == 4, combinedLightIn, combinedOverlayIn);
                        }
                        matrixStack.popPose();
                    }

                } else {
                    for (OilTankLogic.Port port : OilTankLogic.Port.DYNAMIC_PORTS) {
                        matrixStack.pushPose();
                        {
                            // TODO: Might not work as expected
                            BlockPos p = ctx.getLevel().getOrientation().getPosInMB(port.posInMultiblock);
//                            BlockPos p = port.posInMultiblock.subtract(te.posInMultiblock);
                            matrixStack.translate(p.getX(), p.getY(), p.getZ());
                            quad(matrixStack, bufferIn, state.portConfig.get(port), port.posInMultiblock.getX() == 4, combinedLightIn, combinedOverlayIn);
                        }
                        matrixStack.popPose();
                    }
                }
            }
            matrixStack.popPose();
        }
        matrixStack.popPose();
    }

    public void quad(PoseStack matrix, MultiBufferSource buffer, OilTankLogic.PortState portState, boolean flip, int combinedLight, int combinedOverlay) {
        Matrix4f mat = matrix.last().pose();
        VertexConsumer builder = buffer.getBuffer(IPRenderTypes.OIL_TANK);

        boolean input = portState == OilTankLogic.PortState.INPUT;
        float u0 = input ? 0.0F : 0.1F, v0 = 0.5F;
        float u1 = u0 + 0.1F, v1 = v0 + 0.1F;
        if (flip) {
            builder.vertex(mat, 1.001F, 0F, 0F).color(1F, 1F, 1F, 1F).uv(u1, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(1, 1, 1).endVertex();
            builder.vertex(mat, 1.001F, 1F, 0F).color(1F, 1F, 1F, 1F).uv(u1, v0).overlayCoords(combinedOverlay).uv2(combinedLight).normal(1, 1, 1).endVertex();
            builder.vertex(mat, 1.001F, 1F, 1F).color(1F, 1F, 1F, 1F).uv(u0, v0).overlayCoords(combinedOverlay).uv2(combinedLight).normal(1, 1, 1).endVertex();
            builder.vertex(mat, 1.001F, 0F, 1F).color(1F, 1F, 1F, 1F).uv(u0, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(1, 1, 1).endVertex();
        } else {
            builder.vertex(mat, -0.001F, 0F, 0F).color(1F, 1F, 1F, 1F).uv(u0, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(1, 1, 1).endVertex();
            builder.vertex(mat, -0.001F, 0F, 1F).color(1F, 1F, 1F, 1F).uv(u1, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(1, 1, 1).endVertex();
            builder.vertex(mat, -0.001F, 1F, 1F).color(1F, 1F, 1F, 1F).uv(u1, v0).overlayCoords(combinedOverlay).uv2(combinedLight).normal(1, 1, 1).endVertex();
            builder.vertex(mat, -0.001F, 1F, 0F).color(1F, 1F, 1F, 1F).uv(u0, v0).overlayCoords(combinedOverlay).uv2(combinedLight).normal(1, 1, 1).endVertex();
        }
    }
}
