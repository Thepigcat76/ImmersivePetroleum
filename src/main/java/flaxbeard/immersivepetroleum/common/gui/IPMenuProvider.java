package flaxbeard.immersivepetroleum.common.gui;

import flaxbeard.immersivepetroleum.common.blocks.interfaces.IHasGUIInteraction;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IPMenuProvider<T extends BlockEntity & IPMenuProvider<T>> extends IHasGUIInteraction<T>{
	/*
	record BEContainerIP<T extends BlockEntity, C extends IEContainerMenu> (DeferredHolder<MenuType<?>, MenuType<C>> type, IEMenuTypes.BEContainerConstructor<T, C> factory){
		public C create(int windowId, Inventory playerInv, T tile){
			return factory.construct(getType(), windowId, playerInv, tile);
		}
		
		public MenuType<C> getType(){
			return type.get();
		}
	}
	*/
}
