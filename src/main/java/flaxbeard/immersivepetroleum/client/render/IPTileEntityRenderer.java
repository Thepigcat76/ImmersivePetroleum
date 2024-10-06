package flaxbeard.immersivepetroleum.client.render;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class IPTileEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    public IPTileEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    }
}
