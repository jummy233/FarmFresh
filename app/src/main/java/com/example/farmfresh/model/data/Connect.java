package com.example.farmfresh.model.data;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.arch.core.util.Function;

import com.example.farmfresh.R;
import com.example.farmfresh.model.data.data.Item;
import com.example.farmfresh.model.data.data.User;
import com.example.farmfresh.model.data.enums.Key;
import com.example.farmfresh.model.data.enums.UserType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.IConfigurationProvider;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.example.farmfresh.model.data.DataModel.getJsonArrayPropertyName;


/* Data connection APIs.
   All data will be accessed from here.
   This class read from data.json and parse it into `jsonData` : JSONObject.
   The JSONObject is in memory for each run, it get modified dumped into file
   with sync()
   For this app jsonData will only contains two `JSONArray`, namely "user" and "item"
 */
public class Connect {
    private static String path = "data.json";

    private DataModel dataModel;
    private Context context;

    @RequiresApi(api = Build.VERSION_CODES.O)
    // Pass an Application context to access the android resources.
    public Connect(Context context) throws JSONException {
        this.context = context;
        try {
            FileInputStream in = context.openFileInput(path);
            InputStreamReader inreader = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(inreader);

            String line;
            String raw = reader.readLine();  // local file will always be one line.

            System.out.println(raw);
            if (raw == null) {
                this.dataModel = new DataModel(defaultJsondata());
            } else {
                this.dataModel = new DataModel(new JSONObject((String)raw));
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
            this.dataModel = new DataModel(defaultJsondata());
            this.sync();
        }
        System.out.println(this.dataModel.getJsonData());
    }

    public JSONObject getJsonData() {
        return this.dataModel.getJsonData();
    }

    /* Deprecated. */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void close() {
        this.sync();
    }

    public <T extends HashMap & Jsonable> ArrayList<T> getList(Class<T> cls) throws JSONException {
        return dataModel.getList(cls);
    }

    public <T extends HashMap & Jsonable> ArrayList<T> filter(Enum key,
                                                              Function<Object, Boolean> predicate,
                                                              Class<T> cls)
            throws JSONException, InstantiationException, IllegalAccessException {
          /*
          search by key
           It will return a list of filter result. All result are Jsonable Haspmap defined in Item and User classes.
           Note, the callback can be replaced by a regular function object.
           usage:
                new Connect.filter("userName", (e) -> e.equals("Bob"), User.class).get(<idx>).get(<Key>);
                new Connect.filter("price", (e) -> e > 200 && e < 500, Item.class).get(<idx>).get(<Key>);;
            Note: The method level JSONException should never trigger because it handles the defaultJsonData(),
            which is always valid JSONObject.
         */
        ArrayList<T> res = new ArrayList<>();
        ArrayList<T> list = this.dataModel.getList(cls);
        boolean check;
        for (T ele : list) {
            check = predicate.apply(ele.get(key));
            if (check) res.add(ele);
        }
        return res;
    }

    public <T extends HashMap & Jsonable, U> void remove(Enum key, U val, Class<T> cls) throws JSONException {
        ArrayList<T> list = getList(cls);
        for (T ele : list) {
            if (ele != null && ele.get(key).equals(val)) {
                list.remove(ele);
                System.out.println("removed");
                return;
            }
        }
    }

    public ArrayList<Item> getSellerItems(final User user) throws IllegalAccessException, JSONException, InstantiationException {
        return filter(Key.Item.SELLER_NAME, new Function<Object, Boolean>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public Boolean apply(Object e) {
                return Objects.equals(user.get(Key.User.USER_NAME), e);
            }
        },
               Item.class);
    }


    public <T extends  Jsonable> void add(T ele, Class<T> cls) {
        /* Usage:
            new Connect.add(new User().fromJson("{...}"))
            invalid json data will throw a JSONException.
            Catch in where it is used and handle accordingly
        */
        String dataType = DataModel.getJsonArrayPropertyName(cls);
        System.out.println("Connect.add :: dataType: " + dataType);
        switch (dataType) {
            case "user":
                dataModel.getUsers().add((User) ele);
                break;
            case "item":
                dataModel.getItems().add((Item) ele);
                break;
        }
    }

    public ArrayList<User> getSellers() throws IllegalAccessException, JSONException, InstantiationException {
        return filter(Key.User.USER_TYPE, new Function<Object, Boolean>() {
            @Override
            public Boolean apply(Object e) {
                return e == UserType.SELLER;
            }
        }, User.class);
    }

    // write change into file.
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sync() {
        try {
            dataModel.sync(context, path);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private JSONObject fromJson(String path) throws JSONException, IOException {
        /* Json reader. there will be one json file as our database.
            Json Format: { "item": [Item.toJson()], "user": [User.toJson()] }
            Image is stored separately and will be accessed via the path.
        */
        String content = new String(Files.readAllBytes(Paths.get(path)));
        return new JSONObject(content);
    }

    private JSONObject defaultJsondata() throws JSONException{
        InputStream data = context.getResources().openRawResource(R.raw.data);
        JSONObject res = new JSONObject();
        res.put("user", new JSONArray());
        res.put("item", new JSONArray());
        try {
            String str = new BufferedReader(new InputStreamReader(data)).readLine();
            System.out.println(str);
            res = new JSONObject(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;

    }

}
