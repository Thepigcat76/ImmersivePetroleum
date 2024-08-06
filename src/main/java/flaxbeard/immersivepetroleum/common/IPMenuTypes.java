package flaxbeard.immersivepetroleum.common;

public class IPMenuTypes{
	/*
	public static final BEContainerIP<DistillationTowerTileEntity, DistillationTowerContainer> DISTILLATION_TOWER =
			register("distillation_tower", DistillationTowerContainer::new);
	public static final BEContainerIP<DerrickTileEntity, DerrickContainer> DERRICK =
			register("derrick", DerrickContainer::new);
	public static final BEContainerIP<CokerUnitTileEntity, CokerUnitContainer> COKER =
			register("coker", CokerUnitContainer::new);
	public static final BEContainerIP<HydrotreaterTileEntity, HydrotreaterContainer> HYDROTREATER =
			register("hydrotreater", HydrotreaterContainer::new);
	
	public static void forceClassLoad(){}
	
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
