package flaxbeard.immersivepetroleum.common;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.DerrickLogic;
import flaxbeard.immersivepetroleum.common.gui.DerrickContainer;
import flaxbeard.immersivepetroleum.common.gui.IPContainer;
import flaxbeard.immersivepetroleum.common.gui.IPContainerMultiblock;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class IPMenuTypes{
	public static void forceClassLoad(){}
	
	public static final IPContainerMultiblock<DerrickLogic.State> DERRICK = register("derrick", DerrickContainer::client, DerrickContainer::server);
	
	private static <S extends IMultiblockState, C extends IPContainer> IPContainerMultiblock<S> register(String name, ClientConstructor<C> client, ServerConstructor<S, C> server){
		return null;
	}
	
	public interface ServerConstructor<B, C extends IPContainer>{
		C constructor(MenuType<C> type, int id, Inventory playerInv, IMultiblockContext<B> be);
	}
	
	public interface ClientConstructor<C extends IPContainer>{
		C constructor(MenuType<C> type, int id, Inventory playerInv);
	}
	
	/*
	public static final BEContainerIP<DistillationTowerTileEntity, DistillationTowerContainer> DISTILLATION_TOWER =
			register("distillation_tower", DistillationTowerContainer::new);
	public static final BEContainerIP<DerrickTileEntity, DerrickContainer> DERRICK =
			register("derrick", DerrickContainer::new);
	public static final BEContainerIP<CokerUnitTileEntity, CokerUnitContainer> COKER =
			register("coker", CokerUnitContainer::new);
	public static final BEContainerIP<HydrotreaterTileEntity, HydrotreaterContainer> HYDROTREATER =
			register("hydrotreater", HydrotreaterContainer::new);
	
	@SuppressWarnings("unchecked")
	public static <T extends BlockEntity, C extends IEContainerMenu> BEContainerIP<T, C> register(String name, IEMenuTypes.BEContainerConstructor<T, C> container){
		DeferredHolder<MenuType<?>, MenuType<C>> typeRef = IPRegisters.registerMenu(name, () -> {
			Mutable<MenuType<C>> typeBox = new MutableObject<>();
			MenuType<C> type = new MenuType<>((IContainerFactory<C>) (windowId, inv, data) -> {
				Level world = ImmersivePetroleum.proxy.getClientWorld();
				BlockPos pos = data.readBlockPos();
				BlockEntity te = world.getBlockEntity(pos);
				return container.construct(typeBox.getValue(), windowId, inv, (T) te);
			});
			typeBox.setValue(type);
			return type;
		});
		return new BEContainerIP<>(typeRef, container);
	}
	*/
}
