package flaxbeard.immersivepetroleum.common.gui;

import net.minecraft.core.Vec3i;

/**
 * @author TwistedGate © 2021
 */
// TODO Replace IEBaseContainerOld as soon as possible
@Deprecated
public class MultiblockAwareGuiContainer/*<T extends MultiblockPartBlockEntity<T>> extends IEBaseContainerOld<T>*/{
	static final Vec3i ONE = new Vec3i(1, 1, 1);
	
	/*
	protected BlockPos templateSize;
	public MultiblockAwareGuiContainer(MenuType<?> type, T tile, int id, IETemplateMultiblock template){
		super(type, tile, id);
		
		this.templateSize = new BlockPos(template.getSize(getTile().getLevelNonnull())).subtract(ONE);
	}
	
	// TODO This is only Temporary until i've replaced IEBaseContainerOld
	/** Only exists to keep the Deprecation warning at bay and will then be removed/replace
	public Container getInv(){
		return this.inv;
	}
	
	// TODO This is only Temporary until i've replaced IEBaseContainerOld
	/** Only exists to keep the Deprecation warning at bay and will then be removed/replace
	public T getTile(){
		return this.tile;
	}
	
	/**
	 * Returns the maximum distance in blocks to the multiblock befor the GUI get's closed automaticly
	
	public int getMaxDistance(){
		return 5;
	}
	
	@Override
	public boolean stillValid(@Nonnull Player player){
		if(getInv() != null){
			BlockPos min = getTile().getBlockPosForPos(BlockPos.ZERO);
			BlockPos max = getTile().getBlockPosForPos(this.templateSize);
			
			AABB box = new AABB(min, max).inflate(getMaxDistance());
			
			return box.intersects(player.getBoundingBox());
		}
		
		return false;
	}
	
	protected final void addPlayerInventorySlots(Inventory playerInventory, int x, int y){
		for(int i = 0;i < 3;i++){
			for(int j = 0;j < 9;j++){
				addSlot(new Slot(playerInventory, j + i * 9 + 9, x + j * 18, y + i * 18));
			}
		}
	}
	
	protected final void addPlayerHotbarSlots(Inventory playerInventory, int x, int y){
		for(int i = 0;i < 9;i++){
			addSlot(new Slot(playerInventory, i, x + i * 18, y));
		}
	}
	*/
}
