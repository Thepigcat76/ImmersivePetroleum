package flaxbeard.immersivepetroleum.common.gui;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import flaxbeard.immersivepetroleum.common.util.AABBUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class IPContainerMultiblock<S extends IMultiblockState> extends IPContainer{
	
	protected static <S extends IMultiblockState> ContainerContext clientContext(MenuType<?> type, int id){
		return new ContainerContext(type, id, () -> {}, player -> true);
	}
	
	protected static <S extends IMultiblockState> ContainerContext serverContext(MenuType<?> type, int id, IMultiblockContext<S> mbContext){
		return new ContainerContext(type, id, mbContext::markMasterDirty, p -> mbContext.isValid().getAsBoolean());
	}
	
	@Nullable
	private AABB bounds;
	protected IPContainerMultiblock(ContainerContext containerContext, @Nullable MultiblockRegistration<S> mb, @Nullable IMultiblockContext<S> mbContext){
		super(containerContext);
		
		if(mb != null && mbContext != null){
			Vec3i size = mb.size(mbContext.getLevel().getRawLevel());
			BlockPos min = mbContext.getLevel().toAbsolute(BlockPos.ZERO);
			BlockPos max = mbContext.getLevel().toAbsolute(new BlockPos(size));
			this.bounds = AABBUtils.create(min, max).inflate(getMaxDistance());
		}
	}
	
	@Deprecated
	protected IPContainerMultiblock(@Nonnull MultiblockRegistration<S> mb, @Nullable MenuType<?> type, int id, IMultiblockContext<S> context){
		super(new ContainerContext(type, id, context::markMasterDirty, (p) -> context.isValid().getAsBoolean()));
		
		Vec3i size = mb.size(context.getLevel().getRawLevel());
		
		BlockPos min = context.getLevel().toAbsolute(BlockPos.ZERO);
		BlockPos max = context.getLevel().toAbsolute(new BlockPos(size));
		this.bounds = AABBUtils.create(min, max).inflate(getMaxDistance());
	}
	
	/**
	 * Returns the maximum distance in blocks to the multiblock before the GUI gets closed automatically
	 */
	public int getMaxDistance(){
		return 5;
	}
	
	@Override
	public boolean stillValid(@Nonnull Player player){
		if(this.isValid.test(player)){
			return this.bounds == null || this.bounds.intersects(player.getBoundingBox());
		}
		
		return false;
	}
}
