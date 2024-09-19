package flaxbeard.immersivepetroleum.common.gui.sync;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class SyncDataSerializers{
	public static final DataSerializer<Integer> INT = SerialRegister.register(FriendlyByteBuf::readVarInt, FriendlyByteBuf::writeVarInt);
	public static final DataSerializer<Float> FLOAT = SerialRegister.register(FriendlyByteBuf::readFloat, FriendlyByteBuf::writeFloat);
	public static final DataSerializer<Boolean> BOOL = SerialRegister.register(FriendlyByteBuf::readBoolean, FriendlyByteBuf::writeBoolean);
	public static final DataSerializer<ItemStack> ITEM_STACK = SerialRegister.register(FriendlyByteBuf::readItem, FriendlyByteBuf::writeItem, ItemStack::copy, ItemStack::matches);
	public static final DataSerializer<FluidStack> FLUID_STACK = SerialRegister.register(FriendlyByteBuf::readFluidStack, FriendlyByteBuf::writeFluidStack, FluidStack::copy, FluidStack::isFluidStackIdentical);
	
	protected static class SerialRegister{
		private static final List<DataSerializer<?>> SERIALIZERS = new ArrayList<>();
		
		protected static <T> DataSerializer<T> register(Function<FriendlyByteBuf, T> read, BiConsumer<FriendlyByteBuf, T> write){
			return register(read, write, t -> t, Object::equals);
		}
		
		protected static <T> DataSerializer<T> register(Function<FriendlyByteBuf, T> read, BiConsumer<FriendlyByteBuf, T> write, UnaryOperator<T> copy, BiPredicate<T, T> equals){
			DataSerializer<T> serializer = new DataSerializer<>(read, write, copy, equals, SERIALIZERS.size());
			SERIALIZERS.add(serializer);
			return serializer;
		}
	}
	
	public static SyncPair<?> read(FriendlyByteBuf in){
		DataSerializer<?> serializer = SerialRegister.SERIALIZERS.get(in.readVarInt());
		return serializer.read(in);
	}
	
	public record DataSerializer<T>(Function<FriendlyByteBuf, T> read, BiConsumer<FriendlyByteBuf, T> write, UnaryOperator<T> copy, BiPredicate<T, T> equals, int id){
		public SyncPair<T> read(FriendlyByteBuf in){
			return new SyncPair<>(this, read().apply(in));
		}
	}
	
	public record SyncPair<T>(DataSerializer<T> serializer, T data){
		public void write(FriendlyByteBuf out){
			out.writeVarInt(this.serializer.id());
			this.serializer.write().accept(out, this.data);
		}
	}
}
