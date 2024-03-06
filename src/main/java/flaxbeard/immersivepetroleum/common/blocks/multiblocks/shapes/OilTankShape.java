package flaxbeard.immersivepetroleum.common.blocks.multiblocks.shapes;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import flaxbeard.immersivepetroleum.common.util.AABBUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public class OilTankShape extends GenericShape{
	public static final OilTankShape GETTER = new OilTankShape();
	
	private OilTankShape(){
	}
	
	@Nonnull
	protected List<AABB> getShape(BlockPos posInMultiblock){
		int x = posInMultiblock.getX();
		int y = posInMultiblock.getY();
		int z = posInMultiblock.getZ();
		
		List<AABB> main = new ArrayList<>();
		
		// Corner Supports
		if(y == 0){
			// Corner Supports
			if(x == 0 && z == 1){
				AABBUtils.box16(main, 0, 0, 0, 4, 16, 4);
				AABBUtils.box16(main, 8, 0, 8, 16, 8, 16);
			}
			if(x == 4 && z == 1){
				AABBUtils.box16(main, 12, 0, 0, 16, 16, 4);
				AABBUtils.box16(main, 0, 0, 8, 8, 8, 16);
			}
			if(x == 0 && z == 5){
				AABBUtils.box16(main, 0, 0, 12, 4, 16, 16);
				AABBUtils.box16(main, 8, 0, 0, 16, 8, 8);
			}
			if(x == 4 && z == 5){
				AABBUtils.box16(main, 12, 0, 12, 16, 16, 16);
				AABBUtils.box16(main, 0, 0, 0, 8, 8, 8);
			}
			
			// Edge and Corner Angles
			
			if(!(x >= 1 && z >= 2 && x <= 3 && z <= 4))
				AABBUtils.box16(main, 0, 8, 0, 16, 16, 16);
			
			if(z >= 2 && z <= 4){
				if(x == 0) AABBUtils.box16(main, 8, 0, 0, 16, 8, 16);
				if(x == 4) AABBUtils.box16(main, 0, 0, 0, 8, 8, 16);
			}
			if(x >= 1 && x <= 3){
				if(z == 1) AABBUtils.box16(main, 0, 0, 8, 16, 8, 16);
				if(z == 5) AABBUtils.box16(main, 0, 0, 0, 16, 8, 8);
			}
		}
		
		// Easy Access Ladders™
		if(x == 3 && z == 0){
			if(y == 1 || y == 2){
				AABBUtils.box16(main, 2, 0, 15, 14, 16, 16);
			}
		}
		
		// Easy Access Slabs™
		if(y == 2){
			if(z == 0 && (x == 2 || x == 4)){
				AABBUtils.box16(main, 0, 8, 0, 16, 16, 16);
			}
		}
		
		// Railings
		if(y == 3){
			if(z >= 1 && z <= 5){
				if(x == 0){
					AABBUtils.box16(main, 0, 0, 0, 1, 16, 16);
				}else if(x == 4){
					AABBUtils.box16(main, 15, 0, 0, 16, 16, 16);
				}
			}
			if(x >= 0 && x <= 4){
				if(z == 5){
					AABBUtils.box16(main, 0, 0, 15, 16, 16, 16);
				}else if(z == 1 && x != 4){
					AABBUtils.box16(main, 0, 0, 0, 16, 16, 1);
				}
			}
		}
		
		// Use default cube shape if nessesary
		if(main.isEmpty()){
			main.add(AABBUtils.FULL);
		}
		return main;
	}
}
