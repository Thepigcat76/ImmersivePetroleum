package flaxbeard.immersivepetroleum.common.gui;

public class CokerUnitContainer /*extends MultiblockAwareGuiContainer<CokerUnitTileEntity>*/{
	/*
	public CokerUnitContainer(MenuType<?> type, int id, Inventory playerInventory, final CokerUnitTileEntity tile){
		super(type, tile, id, CokerUnitMultiblock.INSTANCE);
		
		addSlot(new IPSlot.CokerInput(this, getInv(), CokerUnitTileEntity.Inventory.INPUT.id(), 20, 71));
		addSlot(new IPSlot(getInv(), CokerUnitTileEntity.Inventory.INPUT_FILLED.id(), 9, 14, stack -> {
			return FluidUtil.getFluidHandler(stack).map(h -> {
				if(h.getTanks() <= 0 || h.getFluidInTank(0).isEmpty()){
					return false;
				}
				
				FluidStack fs = h.getFluidInTank(0);
				if(fs.isEmpty() || (tile.bufferTanks[TANK_INPUT].getFluidAmount() > 0 && !fs.isFluidEqual(tile.bufferTanks[TANK_INPUT].getFluid()))){
					return false;
				}
				
				return CokerUnitRecipe.hasRecipeWithInput(fs, true);
			}).orElse(false);
		}));
		addSlot(new IPSlot.ItemOutput(getInv(), CokerUnitTileEntity.Inventory.INPUT_EMPTY.id(), 9, 45));
		
		addSlot(new IPSlot.FluidContainer(getInv(), CokerUnitTileEntity.Inventory.OUTPUT_EMPTY.id(), 175, 14, FluidFilter.EMPTY));
		addSlot(new IPSlot.ItemOutput(getInv(), CokerUnitTileEntity.Inventory.OUTPUT_FILLED.id(), 175, 45));
		
		this.ownSlotCount = CokerUnitTileEntity.Inventory.values().length;
		
		addPlayerInventorySlots(playerInventory, 20, 105);
		addPlayerHotbarSlots(playerInventory, 20, 163);
	}
	*/
}
