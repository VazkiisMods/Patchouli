package vazkii.patchouli.api.data;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import vazkii.patchouli.api.PatchouliAPI;

/**
 * @author Minecraftschurli
 * @version 2020-02-27
 */
public class SpotlightPageBuilder extends PageBuilder {
    private final String item;
    private String title;
    private Boolean linkRecipe;
    private String text;

    public SpotlightPageBuilder(ItemStack stack, EntryBuilder parent) {
        super("spotlight", parent);
        this.item = PatchouliAPI.instance.serializeItemStack(stack);
    }

    @Override
    protected void serialize(JsonObject json) {
        json.addProperty("item", item);
        if (title != null)
            json.addProperty("title", title);
        if (linkRecipe != null)
            json.addProperty("link_recipe", linkRecipe);
        if (text != null)
            json.addProperty("text", text);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLinkRecipe(Boolean linkRecipe) {
        this.linkRecipe = linkRecipe;
    }

    public void setText(String text) {
        this.text = text;
    }
}
