package flaxbeard.immersivepetroleum.common.gui;

public class DistillationTowerContainer /*extends MultiblockAwareGuiContainer<DistillationTowerTileEntity>*/{
	/*
	public DistillationTowerContainer(MenuType<?> type, int id, Inventory playerInventory, final DistillationTowerTileEntity tile){
		super(type, tile, id, DistillationTowerMultiblock.INSTANCE);
		
		addSlot(new IPSlot(getInv(), INV_0, 12, 17){
			@Override
			public boolean mayPlace(@Nonnull ItemStack stack){
				return FluidUtil.getFluidHandler(stack).map(h -> {
					if(h.getTanks() <= 0){
						return false;
					}
					
					FluidStack fs = h.getFluidInTank(0);
					if(fs.isEmpty() || (tile.tanks[TANK_INPUT].getFluidAmount() > 0 && !fs.isFluidEqual(tile.tanks[TANK_INPUT].getFluid()))){
						return false;
					}
					
					DistillationTowerRecipe recipe = DistillationTowerRecipe.findRecipe(fs);
					return recipe != null;
				}).orElse(false);
			}
		});
		addSlot(new IPSlot.ItemOutput(getInv(), INV_1, 12, 53));
		
		addSlot(new IPSlot.FluidContainer(getInv(), INV_2, 134, 17, FluidFilter.EMPTY));
		addSlot(new IPSlot.ItemOutput(getInv(), INV_3, 134, 53));
		
		this.ownSlotCount = 4;
		
		addPlayerInventorySlots(playerInventory, 8, 85);
		addPlayerHotbarSlots(playerInventory, 8, 143);
	}
	*/
}
