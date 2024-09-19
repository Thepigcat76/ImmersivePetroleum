package flaxbeard.immersivepetroleum.common.gui;

import com.mojang.datafixers.util.Pair;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.gui.sync.ContainerSyncData;
import flaxbeard.immersivepetroleum.common.gui.sync.SyncDataSerializers;
import flaxbeard.immersivepetroleum.common.network.MessageContainerSync;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@EventBusSubscriber(modid = ImmersivePetroleum.MODID, bus = Bus.FORGE)
public abstract class IPContainer extends AbstractContainerMenu{
	
	private final List<ServerPlayer> usingPlayer = new ArrayList<>();
	private final List<ContainerSyncData<?>> syncData = new ArrayList<>();
	
	protected final Runnable setChanged;
	protected final Predicate<Player> isValid;
	protected IPContainer(ContainerContext containerContext){
		super(containerContext.type, containerContext.id);
		this.setChanged = containerContext.setChanged;
		this.isValid = containerContext.isValid;
	}
	
	protected void addSyncData(ContainerSyncData<?> syncData){
		this.syncData.add(syncData);
	}
	
	@Override
	public void broadcastChanges(){
		super.broadcastChanges();
		
		List<Pair<Integer, SyncDataSerializers.SyncPair<?>>> sync = new ArrayList<>();
		for(int i = 0;i < this.syncData.size();i++){
			ContainerSyncData<?> syncData = this.syncData.get(i);
			if(syncData.needsUpdate())
				sync.add(Pair.of(i, syncData.getSyncPair()));
		}
		if(!sync.isEmpty()){
			this.usingPlayer.forEach(p -> p.connection.send(new MessageContainerSync(sync)));
		}
	}
	
	public void receiveSync(List<Pair<Integer, SyncDataSerializers.SyncPair<?>>> syncList){
		for(Pair<Integer, SyncDataSerializers.SyncPair<?>> e: syncList)
			this.syncData.get(e.getFirst()).processSync(e.getSecond().data());
	}
	
	@Nonnull
	@Override
	public ItemStack quickMoveStack(@Nonnull Player player, int slot){
		return ItemStack.EMPTY; // TODO Is this TO, FROM or BOTH?!
	}
	
	@Override
	public boolean stillValid(@Nonnull Player player){
		return this.isValid.test(player);
	}
	
	@Override
	public void removed(@Nonnull Player player){
		super.removed(player);
		setChanged.run();
	}
	
	@SubscribeEvent
	public static void containerOpened(PlayerContainerEvent.Open e){
		if(e.getContainer() instanceof IPContainer container && e.getEntity() instanceof ServerPlayer player){
			container.usingPlayer.add(player);
			List<Pair<Integer, SyncDataSerializers.SyncPair<?>>> list = new ArrayList<>();
			for(int i = 0;i < container.syncData.size();i++){
				list.add(Pair.of(i, container.syncData.get(i).getSyncPair()));
			}
			player.connection.send(new MessageContainerSync(list));
		}
	}
	
	@SubscribeEvent
	public static void containerClosed(PlayerContainerEvent.Close e){
		if(e.getContainer() instanceof IPContainer container && e.getEntity() instanceof ServerPlayer player)
			container.usingPlayer.remove(player);
	}
	
	protected final void addPlayerInventorySlots(Inventory playerInv, int x, int y){
		for(int i = 0;i < 3;i++){
			for(int j = 0;j < 9;j++){
				addSlot(new Slot(playerInv, j + i * 9 + 9, x + j * 18, y + i * 18));
			}
		}
	}
	
	protected final void addPlayerHotbarSlots(Inventory playerInv, int x, int y){
		for(int i = 0;i < 9;i++){
			addSlot(new Slot(playerInv, i, x + i * 18, y));
		}
	}
	
	protected record ContainerContext(@Nullable MenuType<?> type, int id, @Nonnull Runnable setChanged, @Nonnull Predicate<Player> isValid){
	}
}
