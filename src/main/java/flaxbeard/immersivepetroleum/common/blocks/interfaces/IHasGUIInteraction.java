package flaxbeard.immersivepetroleum.common.blocks.interfaces;

import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IHasGUIInteraction<TE extends BlockEntity & IHasGUIInteraction<TE>> extends MenuProvider{
	
	/*
	@Nullable
	TE getGuiMaster();
	
	BEContainerIP<? super TE, ?> getContainerType();
	
	boolean canUseGui(Player player);
	
	default boolean isValid(){
		return getGuiMaster() != null;
	}
	
	@Nonnull
	@Override
	default AbstractContainerMenu createMenu(int id, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity){
		TE master = getGuiMaster();
		Preconditions.checkNotNull(master);
		BEContainerIP<? super TE, ?> type = getContainerType();
		return type.create(id, playerInventory, master);
	}
	
	public static final Component NO_DISPLAY_NAME = Component.literal("");
	@Override
	default Component getDisplayName(){
		return NO_DISPLAY_NAME;
	}
	*/
}
