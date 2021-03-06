package com.example.farmfresh.model.data.data;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.farmfresh.model.data.Jsonable;
import com.example.farmfresh.model.data.enums.Category;
import com.example.farmfresh.model.data.enums.Key;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;
import java.util.HashMap;

import static com.example.farmfresh.model.data.enums.Key.Item.CATEGORY;
import static com.example.farmfresh.model.data.enums.Key.Item.IMAGE_BASE64;
import static java.util.Base64.getEncoder;


// image are encoded into Base64 String.
public class Item extends HashMap<Key.Item, Object> implements Jsonable<Item> {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        Base64.Encoder encoder = getEncoder();
        obj.put("sellerName", this.get(Key.Item.SELLER_NAME));
        obj.put("itemName", this.get(Key.Item.ITEM_NAME));
        obj.put("price", this.get(Key.Item.PRICE));
        obj.put("quantity", this.get(Key.Item.QUANTITY));
        obj.put("imageBase64", this.get(IMAGE_BASE64));
        obj.put("category", ((Category) this.get(CATEGORY)).name());

        return obj;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Item fromJson(JSONObject json) throws JSONException {
        this.put(Key.Item.SELLER_NAME, (String) json.get("sellerName"));
        this.put(Key.Item.ITEM_NAME, (String) json.get("itemName"));
        this.put(Key.Item.PRICE, (String) json.get("quantity"));
        this.put(Key.Item.QUANTITY, (String) json.get("price"));
        this.put(IMAGE_BASE64, (String) json.get("imageBase64"));
        this.put(CATEGORY, Category.parseString((String) json.get("category")));
        return this;
    }
}
