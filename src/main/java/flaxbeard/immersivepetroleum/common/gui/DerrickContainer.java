package flaxbeard.immersivepetroleum.common.gui;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.DerrickLogic.State;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import javax.annotation.Nullable;

public class DerrickContainer extends IPContainerMultiblock<State>{
	
	public static DerrickContainer client(MenuType<?> type, int id, Inventory playerInv){
		return new DerrickContainer(clientContext(type, id), playerInv, null, null);
	}
	
	public static DerrickContainer server(MenuType<?> type, int id, Inventory playerInv, IMultiblockContext<State> mbContext){
		return new DerrickContainer(serverContext(type, id, mbContext), playerInv, IPContent.Multiblock.DERRICK, mbContext);
	}
	
	protected DerrickContainer(ContainerContext cContext, Inventory playerInv, @Nullable MultiblockRegistration<State> mb, @Nullable IMultiblockContext<State> mbContext){
		super(cContext, mb, mbContext);
	}
	
	/*
	protected DerrickContainer(@Nullable MenuType<?> type, int id, Inventory playerInv, IMultiblockContext<DerrickLogic.State> mbContext){
		super(IPContent.Multiblock.DERRICK, type, id, mbContext);
		
		this.addSlot(new Slot(null, 0, 92, 55){
			@Override
			public boolean mayPlace(@Nonnull ItemStack stack){
				return ExternalModContent.isIEItem_Pipe(stack);
			}
		});
		
		//this.ownSlotCount = 1;
		
		addPlayerInventorySlots(playerInv, 20, 82);
		addPlayerHotbarSlots(playerInv, 20, 140);
	}
	*/
	
	/*
	public DerrickContainer(MenuType<?> type, int id, Inventory playerInventory, DerrickTileEntity tile){
		super(type, tile, id, DerrickMultiblock.INSTANCE);
		
		this.addSlot(new Slot(getInv(), 0, 92, 55){
			@Override
			public boolean mayPlace(@Nonnull ItemStack stack){
				return ExternalModContent.isIEItem_Pipe(stack);
			}
		});
		
		this.ownSlotCount = 1;
		
		addPlayerInventorySlots(playerInventory, 20, 82);
		addPlayerHotbarSlots(playerInventory, 20, 140);
	}
	*/
}
