package flaxbeard.immersivepetroleum.common.blocks.multiblocks;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityDummy;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import com.google.common.base.Preconditions;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

import javax.annotation.Nonnull;

public abstract class IPTemplateMultiblock extends TemplateMultiblock{
	private final MultiblockRegistration<?> logic;
	
	public IPTemplateMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size, MultiblockRegistration<?> logic){
		super(loc, masterFromOrigin, triggerFromOrigin, size);
		this.logic = logic;
	}
	
	@Override
	public Vec3i getSize(@Nonnull Level world){
		return this.size;
	}
	
	public ResourceLocation getBlockName(){
		return this.logic.id();
	}
	
	@Override
	public Component getDisplayName(){
		return this.logic.block().get().getName();
	}
	
	@Override
	public Block getBlock(){
		return this.logic.block().get();
	}
	
	@Nonnull
	@Override
	public TemplateData getTemplate(@Nonnull Level level){
		// Straight up just copied all of this from IETemplateMultiblock
		
		TemplateData result = super.getTemplate(level);
		final Vec3i resultSize = result.template().getSize();
		Preconditions.checkState(resultSize.equals(size), "Wrong template size for multiblock %s, template size: %s", getTemplateLocation(), resultSize);
		return result;
	}
	
	@Override
	protected void prepareBlockForDisassembly(Level world, BlockPos pos){
		BlockEntity be = world.getBlockEntity(pos);
		if(be instanceof IMultiblockBE<?> multiblockBE){
			multiblockBE.getHelper().markDisassembling();
		}else if(be != null){
			ImmersivePetroleum.log.error("Expected multiblock TE at {}, got {}", pos, be);
		}
	}
	
	/** @deprecated Replaced by {@link #getBlock()} */
	public Block getBaseBlock(){
		return getBlock();
	}
	
	@Override
	protected void replaceStructureBlock(StructureBlockInfo info, Level world, BlockPos actualPos, boolean mirrored, Direction clickDirection, Vec3i offsetFromMaster){
		// Straight up just copied all of this from IETemplateMultiblock
		
		BlockState newState = logic.block().get().defaultBlockState();
		newState = newState.setValue(IEProperties.MULTIBLOCKSLAVE, !offsetFromMaster.equals(Vec3i.ZERO));
		
		if(newState.hasProperty(IEProperties.ACTIVE))
			newState = newState.setValue(IEProperties.ACTIVE, false);
		
		if(newState.hasProperty(IEProperties.MIRRORED))
			newState = newState.setValue(IEProperties.MIRRORED, mirrored);
		
		if(newState.hasProperty(IEProperties.FACING_HORIZONTAL))
			newState = newState.setValue(IEProperties.FACING_HORIZONTAL, clickDirection.getOpposite());
		
		final BlockState oldState = world.getBlockState(actualPos);
		world.setBlock(actualPos, newState, 0);
		BlockEntity curr = world.getBlockEntity(actualPos);
		
		if(curr instanceof MultiblockBlockEntityDummy<?> dummy){
			dummy.getHelper().setPositionInMB(info.pos());
		}else if(!(curr instanceof MultiblockBlockEntityMaster<?>)){
			ImmersivePetroleum.log.error("Expected MB TE at {} during placement", actualPos); // This is the only difference
		}
		
		final LevelChunk chunk = world.getChunkAt(actualPos);
		world.markAndNotifyBlock(actualPos, chunk, oldState, newState, Block.UPDATE_ALL, 512);
	}
}
