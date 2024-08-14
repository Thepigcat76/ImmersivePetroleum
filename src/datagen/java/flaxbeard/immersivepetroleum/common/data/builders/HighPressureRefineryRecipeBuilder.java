package flaxbeard.immersivepetroleum.common.data.builders;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import flaxbeard.immersivepetroleum.api.crafting.HighPressureRefineryRecipe;
import flaxbeard.immersivepetroleum.common.util.ChancedItemStack;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class HighPressureRefineryRecipeBuilder extends IPRecipeBuilder<HighPressureRefineryRecipeBuilder>{
	
	public static HighPressureRefineryRecipeBuilder builder(FluidStack output){
		HighPressureRefineryRecipeBuilder b = new HighPressureRefineryRecipeBuilder();
		b.fluidOutput = output;
		return b;
	}
	
	private FluidTagInput fluidInput;
	private FluidTagInput fluidInput2;
	private ChancedItemStack itemOutput = new ChancedItemStack(ItemStack.EMPTY, 0);
	private FluidStack fluidOutput;
	int energy;
	int time;
	
	private HighPressureRefineryRecipeBuilder(){
	}
	
	public HighPressureRefineryRecipeBuilder itemOutput(ChancedItemStack output){
		this.itemOutput = output;
		return this;
	}
	
	public HighPressureRefineryRecipeBuilder fluidInput(FluidTagInput input){
		if(this.fluidInput == null)
			this.fluidInput = input;
		else
			this.fluidInput2 = input;
		return this;
	}
	
	public HighPressureRefineryRecipeBuilder setTimeAndEnergy(int time, int energy){
		this.time = time;
		this.energy = energy;
		return this;
	}
	
	public void build(RecipeOutput out, ResourceLocation name){
		HighPressureRefineryRecipe recipe = new HighPressureRefineryRecipe(this.fluidOutput, this.itemOutput, this.fluidInput, this.fluidInput2, this.energy, this.time);
		out.accept(name, recipe, null, getConditions());
	}
}
