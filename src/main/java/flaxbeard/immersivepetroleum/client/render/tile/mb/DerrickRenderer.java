package flaxbeard.immersivepetroleum.client.render.tile.mb;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import flaxbeard.immersivepetroleum.client.render.IPTileEntityRenderer;
import flaxbeard.immersivepetroleum.client.utils.MCUtil;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.DerrickLogic;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.List;

// TODO: look into using IEMultiblockRenderer
public class DerrickRenderer implements MultiblockRenderer<DerrickLogic.State>{
	static final Vector3f Y_AXIS = new Vector3f(0.0F, 1.0F, 0.0F);
	
	public static final ResourceLocation DRILL = ResourceUtils.ip("multiblock/dyn/derrick_drill");
	public static final ResourceLocation PIPE_SEGMENT = ResourceUtils.ip("multiblock/dyn/derrick_pipe_segment");
	public static final ResourceLocation PIPE_TOP = ResourceUtils.ip("multiblock/dyn/derrick_pipe_top");
	
	@Override
	public void render(@Nonnull IMultiblockContext<DerrickLogic.State> ctx, float partialTicks, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn){
		DerrickLogic.State state = ctx.getState();
		Level rawLevel = ctx.getLevel().getRawLevel();
		if(!ctx.isValid().getAsBoolean() || rawLevel == null /*|| !rawLevel.getChunkSource().hasChunk(ctx.getLevel().getBlockPos())*/){
			return;
		}

		matrixStack.pushPose();
		{
			float rot = state.rotation + (state.drilling? 10 * partialTicks : 0);

			matrixStack.translate(0.5, 1.0, 0.5);
			matrixStack.mulPose(new Quaternionf().rotateY(rot* Mth.DEG_TO_RAD));
			renderObj(DRILL, bufferIn, matrixStack, combinedLightIn, combinedOverlayIn);

			float pipeHeight = -(rot / 360F);

			for(int i = 0;i < 6;i++){
				float y = pipeHeight + i;
				if(y > -1.0){
					matrixStack.pushPose();
					{
						matrixStack.translate(0, y + 0.75, 0);
						renderObj(i < 5 ? PIPE_SEGMENT : PIPE_TOP, bufferIn, matrixStack, combinedLightIn, combinedOverlayIn);
					}
					matrixStack.popPose();
				}
			}

		}
		matrixStack.popPose();
	}

	@Override
	public boolean shouldRenderOffScreen(MultiblockBlockEntityMaster<DerrickLogic.State> p_112306_) {
		return true;
	}
	
	private void renderObj(ResourceLocation modelRL, @Nonnull MultiBufferSource bufferIn, @Nonnull PoseStack matrix, int light, int overlay){
		List<BakedQuad> quads = MCUtil.getModel(modelRL).getQuads(null, null, ApiUtils.RANDOM_SOURCE, ModelData.EMPTY, null);
		PoseStack.Pose last = matrix.last();
		VertexConsumer solid = bufferIn.getBuffer(RenderType.solid());
		for(BakedQuad quad:quads){
			solid.putBulkData(last, quad, 1.0F, 1.0F, 1.0F, light, overlay);
		}
	}
}
