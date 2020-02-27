package vazkii.patchouli.api.data;

import com.google.gson.JsonObject;

/**
 * @author Minecraftschurli
 * @version 2020-02-26
 */
public abstract class PageBuilder {
    private final EntryBuilder parent;
    private final String type;
    private String advancement;
    private String flag;
    private String anchor;

    public PageBuilder(String type, EntryBuilder parent) {
        this.parent = parent;
        this.type = type;
    }

    JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", type);
        json.addProperty("advancement", advancement);
        json.addProperty("flag", flag);
        json.addProperty("anchor", anchor);
        this.serialize(json);
        return json;
    }

    protected abstract void serialize(JsonObject json);

    public EntryBuilder build() {
        return parent;
    }

    public void setAdvancement(String advancement) {
        this.advancement = advancement;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }
}
