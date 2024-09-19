package flaxbeard.immersivepetroleum.common.gui.sync;

import blusunrize.immersiveengineering.api.energy.IMutableEnergyStorage;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ContainerSyncData<T>{
	private final SyncDataSerializers.DataSerializer<T> serializer;
	private final Supplier<T> getter;
	private final Consumer<T> setter;
	private T current;
	
	public ContainerSyncData(SyncDataSerializers.DataSerializer<T> serializer, Supplier<T> getter, Consumer<T> setter){
		this.serializer = serializer;
		this.getter = getter;
		this.setter = setter;
	}
	
	public static ContainerSyncData<?> fluid(FluidTank tank){
		return new ContainerSyncData<>(SyncDataSerializers.FLUID_STACK, tank::getFluid, tank::setFluid);
	}
	
	public static ContainerSyncData<?> energy(IMutableEnergyStorage storage){
		return integer(storage::getEnergyStored, storage::setStoredEnergy);
	}
	
	public static ContainerSyncData<?> integer(Supplier<Integer> get, Consumer<Integer> set){
		return new ContainerSyncData<>(SyncDataSerializers.INT, get, set);
	}
	
	public boolean needsUpdate(){
		T n = this.getter.get();
		if(n == null && this.current == null)
			return false;
		
		if(this.current != null && n != null && this.serializer.equals().test(this.current, n))
			return false;
		
		this.current = this.serializer.copy().apply(n);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public void processSync(Object data){
		this.current = (T) data;
		this.setter.accept(this.serializer.copy().apply(this.current));
	}
	
	public SyncDataSerializers.SyncPair<T> getSyncPair(){
		return new SyncDataSerializers.SyncPair<>(this.serializer, this.current);
	}
}
