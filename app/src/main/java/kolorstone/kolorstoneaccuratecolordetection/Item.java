package kolorstone.kolorstoneaccuratecolordetection;

public class Item {
    private String id, user_id, rgb, hex, hsl, cmyk, timestamp, fav;

    public Item(String id, String user_id, String rgb, String hex, String hsl, String cmyk, String timestamp, String fav) {
        this.id = id;
        this.user_id = user_id;
        this.rgb = rgb;
        this.hex = hex;
        this.hsl = hsl;
        this.cmyk = cmyk;
        this.timestamp = timestamp;
        this.fav = fav;
    }

    public String getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getRgb() {
        return rgb;
    }

    public String getHex() {
        return hex;
    }

    public String getHsl() {
        return hsl;
    }

    public String getCmyk() {
        return hex;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getFav() {
        return fav;
    }
}
