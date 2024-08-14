package flaxbeard.immersivepetroleum.common.util.compat.jei;

import java.util.Arrays;
import java.util.Collections;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import flaxbeard.immersivepetroleum.api.crafting.CokerUnitRecipe;
import flaxbeard.immersivepetroleum.client.utils.MCUtil;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import flaxbeard.immersivepetroleum.common.util.Utils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

public class CokerUnitRecipeCategory extends IPRecipeCategory<CokerUnitRecipe>{
	public static final ResourceLocation ID = ResourceUtils.ip("cokerunit");
	
	private final IDrawableStatic tankOverlay;
	public CokerUnitRecipeCategory(IGuiHelper guiHelper){
		super(CokerUnitRecipe.class, guiHelper, ID, "block.immersivepetroleum.coker_unit");
		ResourceLocation background = ResourceUtils.ip("textures/gui/jei/coker.png");
		ResourceLocation coker = ResourceUtils.ip("textures/gui/coker.png");
		
		setBackground(guiHelper.createDrawable(background, 0, 0, 150, 77));
		setIcon(IPContent.Multiblock.COKERUNIT.iconStack());
		
		this.tankOverlay = guiHelper.createDrawable(coker, 200, 0, 20, 51);
	}
	
	@Override
	public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, CokerUnitRecipe recipe, @Nonnull IFocusGroup focuses){
		int inputAmount = recipe.inputFluid.getAmount();
		int outputAmount = recipe.outputFluid.getAmount();
		int guiTankSize = Math.max(inputAmount, outputAmount);
		
		builder.addSlot(RecipeIngredientRole.INPUT, 2, 2)
			.setFluidRenderer(guiTankSize, false, 20, 51)
			.setOverlay(this.tankOverlay, 0, 0)
			.addIngredients(NeoForgeTypes.FLUID_STACK, recipe.inputFluid.getMatchingFluidStacks());
		
		builder.addSlot(RecipeIngredientRole.OUTPUT, 50, 2)
			.setFluidRenderer(guiTankSize, false, 20, 51)
			.setOverlay(this.tankOverlay, 0, 0)
			.addIngredient(NeoForgeTypes.FLUID_STACK, recipe.outputFluid);
		
		builder.addSlot(RecipeIngredientRole.INPUT, 4, 58)
			.addIngredients(VanillaTypes.ITEM_STACK, Arrays.asList(recipe.inputItem.getMatchingStacks()));
		
		builder.addSlot(RecipeIngredientRole.OUTPUT, 52, 58)
			.addIngredients(VanillaTypes.ITEM_STACK, Collections.singletonList(recipe.outputItem));
	}
	
	@Override
	public void draw(CokerUnitRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY){
		PoseStack matrix = guiGraphics.pose();
		
		IDrawable background = getBackground();
		int bWidth = background.getWidth();
		int bHeight = background.getHeight();
		Font font = MCUtil.getFont();
		
		int time = (recipe.getTotalProcessTime() + 2 + 5) * recipe.inputItem.getCount();
		int energy = recipe.getTotalProcessEnergy() / recipe.getTotalProcessTime();
		
		matrix.pushPose();
		{
			String text0 = I18n.get("desc.immersiveengineering.info.ift", Utils.fDecimal(energy));
			guiGraphics.drawString(font, text0, bWidth - 5 - font.width(text0), (bHeight / 3) + font.lineHeight, 0, false);
			
			String text1 = I18n.get("desc.immersiveengineering.info.seconds", Utils.fDecimal(time / 20D));
			guiGraphics.drawString(font, text1, bWidth - 10 - font.width(text1), (bHeight / 3) + (font.lineHeight * 2), 0, false);
		}
		matrix.popPose();
	}
}
