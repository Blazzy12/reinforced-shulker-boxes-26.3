package atonkish.reinfshulker.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

public class ReinforcedShulkerBoxCraftingRecipe extends ShapedRecipe {
  public static final MapCodec<ReinforcedShulkerBoxCraftingRecipe> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      Recipe.CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
                      CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(
                          recipe -> recipe.bookInfo),
                      ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.raw),
                      ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.result))
                  .apply(instance, ReinforcedShulkerBoxCraftingRecipe::new));
  public static final StreamCodec<RegistryFriendlyByteBuf, ReinforcedShulkerBoxCraftingRecipe>
      PACKET_CODEC =
          StreamCodec.composite(
              Recipe.CommonInfo.STREAM_CODEC,
              recipe -> recipe.commonInfo,
              CraftingRecipe.CraftingBookInfo.STREAM_CODEC,
              recipe -> recipe.bookInfo,
              ShapedRecipePattern.STREAM_CODEC,
              recipe -> recipe.raw,
              ItemStackTemplate.STREAM_CODEC,
              recipe -> recipe.result,
              ReinforcedShulkerBoxCraftingRecipe::new);
  public static final RecipeSerializer<ReinforcedShulkerBoxCraftingRecipe> SERIALIZER =
      new RecipeSerializer<>(CODEC, PACKET_CODEC);

  final ShapedRecipePattern raw;
  final ItemStackTemplate result;

  public ReinforcedShulkerBoxCraftingRecipe(
      Recipe.CommonInfo commonInfo,
      CraftingRecipe.CraftingBookInfo bookInfo,
      ShapedRecipePattern raw,
      ItemStackTemplate result) {
    super(commonInfo, bookInfo, raw, result);
    this.raw = raw;
    this.result = result;
  }

  @Override
  @SuppressWarnings("unchecked")
  public RecipeSerializer<ShapedRecipe> getSerializer() {
    return (RecipeSerializer<ShapedRecipe>) (Object) ModRecipeSerializer.REINFORCED_SHULKER_BOX;
  }

  @Override
  public ItemStack assemble(CraftingInput craftingRecipeInput) {
    // Keep the contents (and other components) of the shulker box in the center slot
    Item item = this.result.item().value();
    ItemStack itemStack = craftingRecipeInput.getItem(4);
    return itemStack.transmuteCopy(item, this.result.count());
  }
}
