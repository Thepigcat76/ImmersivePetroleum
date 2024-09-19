package flaxbeard.immersivepetroleum.common.network;

import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class NetworkUtils{
	public static <T> List<T> readList(FriendlyByteBuf buf, Function<FriendlyByteBuf, T> reader){
		int size = buf.readVarInt();
		List<T> ret = new ArrayList<>(size);
		for(int i = 0;i < size;i++){
			ret.add(reader.apply(buf));
		}
		return ret;
	}
	
	public static <T> void writeList(FriendlyByteBuf buf, List<T> list, BiConsumer<T, FriendlyByteBuf> writer){
		buf.writeVarInt(list.size());
		list.forEach(e -> writer.accept(e, buf));
	}
}
