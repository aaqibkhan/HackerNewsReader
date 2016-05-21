package aaqib.taskaaqib.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * HackerNews Item which can be a Story, Comment, Poll, Job or Pollopt
 */
public class Item implements Parcelable {

    private long id;
    private boolean deleted;
    private String type;
    private String by;
    private long time;
    private String text;
    private boolean dead;
    private long parent;
    private List<Long> kids;
    private String url;
    private int score;
    private String title;
    private int descendants;

    public Item(JSONObject jsonObject) {
        try {
            this.id = jsonObject.getLong("id");
            this.deleted = jsonObject.has("deleted") && jsonObject.getBoolean("deleted");
            this.type = jsonObject.has("type") ? jsonObject.getString("type") : "";
            this.by = jsonObject.has("by") ? jsonObject.getString("by") : "";
            this.time = jsonObject.has("time") ? jsonObject.getLong("time") : 0;
            this.text = jsonObject.has("text") ? jsonObject.getString("text") : "";
            this.dead = jsonObject.has("dead") && jsonObject.getBoolean("dead");
            this.parent = jsonObject.has("parent") ? jsonObject.getLong("parent") : 0;
            JSONArray jsonArray = jsonObject.has("kids") ? jsonObject.getJSONArray("kids") : null;
            if (jsonArray != null) {
                kids = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    kids.add(jsonArray.getLong(i));
                }
            }
            this.url = jsonObject.has("url") ? jsonObject.getString("url") : "";
            this.score = jsonObject.has("score") ? jsonObject.getInt("score") : 0;
            this.title = jsonObject.has("title") ? jsonObject.getString("title") : "";
            this.descendants = jsonObject.has("descendants") ? jsonObject.getInt("descendants") : 0;
        } catch (JSONException jsonE) {
            jsonE.printStackTrace();
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public String getType() {
        return type;
    }

    public String getBy() {
        return by;
    }

    public long getTime() {
        return time;
    }

    public String getText() {
        return text;
    }

    public boolean isDead() {
        return dead;
    }

    public long getParent() {
        return parent;
    }

    public List<Long> getKids() {
        return kids;
    }

    public String getUrl() {
        return url;
    }

    public int getScore() {
        return score;
    }

    public String getTitle() {
        return title;
    }

    public int getDescendants() {
        return descendants;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeByte(deleted ? (byte) 1 : (byte) 0);
        dest.writeString(this.type);
        dest.writeString(this.by);
        dest.writeLong(this.time);
        dest.writeString(this.text);
        dest.writeByte(dead ? (byte) 1 : (byte) 0);
        dest.writeLong(this.parent);
        dest.writeList(this.kids);
        dest.writeString(this.url);
        dest.writeInt(this.score);
        dest.writeString(this.title);
        dest.writeInt(this.descendants);
    }

    public Item() {
    }

    protected Item(Parcel in) {
        this.id = in.readLong();
        this.deleted = in.readByte() != 0;
        this.type = in.readString();
        this.by = in.readString();
        this.time = in.readLong();
        this.text = in.readString();
        this.dead = in.readByte() != 0;
        this.parent = in.readLong();
        this.kids = new ArrayList<>();
        in.readList(this.kids, Long.class.getClassLoader());
        this.url = in.readString();
        this.score = in.readInt();
        this.title = in.readString();
        this.descendants = in.readInt();
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
