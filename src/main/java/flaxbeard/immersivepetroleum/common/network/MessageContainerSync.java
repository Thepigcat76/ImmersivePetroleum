package flaxbeard.immersivepetroleum.common.network;

import com.mojang.datafixers.util.Pair;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.gui.IPContainer;
import flaxbeard.immersivepetroleum.common.gui.sync.SyncDataSerializers;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.List;

public class MessageContainerSync implements INetMessage{
	public static final ResourceLocation ID = ResourceUtils.ip("container_sync");
	
	private final List<Pair<Integer, SyncDataSerializers.SyncPair<?>>> synced;
	
	public MessageContainerSync(FriendlyByteBuf buf){
		this(NetworkUtils.readList(buf, b -> Pair.of(b.readVarInt(), SyncDataSerializers.read(b))));
	}
	
	public MessageContainerSync(List<Pair<Integer, SyncDataSerializers.SyncPair<?>>> sync){
		this.synced = sync;
	}
	
	@Override
	public void write(FriendlyByteBuf buf){
		NetworkUtils.writeList(buf, this.synced, (p, b) -> {
			b.writeVarInt(p.getFirst());
			p.getSecond().write(b);
		});
	}
	
	@Override
	public void process(PlayPayloadContext context){
		context.workHandler().execute(() -> {
			if(ImmersivePetroleum.proxy.getClientPlayer().containerMenu instanceof IPContainer container)
				container.receiveSync(this.synced);
		});
	}
	
	@Override
	public ResourceLocation id(){
		return ID;
	}
}
