package flaxbeard.immersivepetroleum.common.blocks.multiblocks;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import blusunrize.immersiveengineering.common.util.Utils;
import flaxbeard.immersivepetroleum.client.utils.MCUtil;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;

public class IPClientMultiblockProperties implements ClientMultiblocks.MultiblockManualData{
	private final IPTemplateMultiblock multiblock;
	@Nullable
	private NonNullList<ItemStack> materials;
	private final ItemStack renderStack;
	@Nullable
	private final Vec3 renderOffset;
	
	public IPClientMultiblockProperties(IPTemplateMultiblock multiblock){
		this(multiblock, null);
	}
	
	public IPClientMultiblockProperties(IPTemplateMultiblock multiblock, double offX, double offY, double offZ){
		this(multiblock, new Vec3(offX, offY, offZ));
	}
	
	private IPClientMultiblockProperties(IPTemplateMultiblock multiblock, @Nullable Vec3 renderOffset){
		this.multiblock = multiblock;
		this.renderStack = new ItemStack(multiblock.getBlock());
		this.renderOffset = renderOffset;
	}
	
	/** Skipping normal rendering behaviour */
	protected boolean usingCustomRendering(){
		return false;
	}
	
	@Override
	public NonNullList<ItemStack> getTotalMaterials(){
		if(this.materials == null){
			List<StructureTemplate.StructureBlockInfo> structure = this.multiblock.getStructure(null);
			this.materials = NonNullList.create();
			for(StructureTemplate.StructureBlockInfo info:structure){
				// Skip dummy blocks in total
				if(info.state().hasProperty(IEProperties.MULTIBLOCKSLAVE) && info.state().getValue(IEProperties.MULTIBLOCKSLAVE))
					continue;
				
				ItemStack picked = Utils.getPickBlock(info.state());
				boolean added = false;
				for(ItemStack existing:this.materials)
					if(ItemStack.isSameItem(existing, picked)){
						existing.grow(1);
						added = true;
						break;
					}
				if(!added)
					this.materials.add(picked.copy());
			}
		}
		return this.materials;
	}
	
	@Override
	public boolean canRenderFormedStructure(){
		return this.renderOffset != null;
	}
	
	/** Allowing custom accessories to be rendered. Unused if {@link #usingCustomRendering()} returns true */
	public void renderExtras(PoseStack matrix, MultiBufferSource buffer){
	}
	
	/** Only used when {@link #usingCustomRendering()} returns true */
	public void renderCustomFormedStructure(PoseStack matrix, MultiBufferSource buffer){
	}
	
	@Override
	public final void renderFormedStructure(PoseStack matrix, MultiBufferSource buffer){
		Objects.requireNonNull(this.renderOffset);
		
		if(usingCustomRendering()){
			renderCustomFormedStructure(matrix, buffer);
			return;
		}
		
		matrix.translate(this.renderOffset.x, this.renderOffset.y, this.renderOffset.z);
		MCUtil.getItemRenderer().renderStatic(renderStack, ItemDisplayContext.NONE, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, matrix, buffer, null, 0);
		matrix.pushPose();
		{
			renderExtras(matrix, buffer);
		}
		matrix.popPose();
	}
}
