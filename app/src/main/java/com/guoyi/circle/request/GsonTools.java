package com.guoyi.circle.request;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.guoyi.circle.been.ReturnMsg;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * GSON转换工具
 */
public class GsonTools {

    private static Gson gson = null;

    private GsonTools() {
        // TODO Auto-generated constructor stub
    }

    public synchronized static Gson getGsonInstance() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public static <T extends ReturnMsg> Observable<T> getBeanObservable(ReturnMsg o, Class<T> cls) {
        String json = createGsonString(o.getData());
        T t = GsonTools.changeGsonToBean(json, cls);
        t.setReturnMsg(o);
        return Observable.just(t);

    }


    public static String createGsonString(Object object) {
        Gson gson = getGsonInstance();
        String gsonString = gson.toJson(object);
        return gsonString;
    }

    public static <T> T changeGsonToBean(Object o, Class<T> cls) {
        Gson gson = getGsonInstance();
        String string = createGsonString(o);
        T t = gson.fromJson(string, cls);
        return t;
    }

    public static <T> T changeGsonToBean(String gsonString, Class<T> cls) {
        Gson gson = getGsonInstance();
        T t = gson.fromJson(gsonString, cls);
        return t;
    }

    public static <T> List<T> changeGsonToList(String gsonString, Class<T> cls) {
        Gson gson = getGsonInstance();
        List<T> list = gson.fromJson(gsonString, new TypeToken<List<T>>() {
        }.getType());
        return list;
    }

    public static <T> List<T> changeGsonToSafeList(String gsonString, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        try {
            Gson gson = getGsonInstance();
            JsonArray arry = new JsonParser().parse(gsonString).getAsJsonArray();
            for (JsonElement jsonElement : arry) {
                list.add(gson.fromJson(jsonElement, cls));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static <T> List<Map<String, T>> changeGsonToListMaps(
            String gsonString) {
        List<Map<String, T>> list = null;
        Gson gson = getGsonInstance();
        list = gson.fromJson(gsonString, new TypeToken<List<Map<String, T>>>() {
        }.getType());
        return list;
    }

    public static <T> Map<String, T> changeGsonToMaps(String gsonString) {
        Map<String, T> map = null;
        Gson gson = getGsonInstance();
        map = gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
        }.getType());
        return map;
    }

    @SuppressLint("LongLogTag")
    public static String getJsonValueByKey(String json, String key) {
        try {
            String str = new JSONObject(json).getString(key);
            return str;
        } catch (JSONException localJSONException) {
            Log.e("com.imcore.common.util.JsonError", localJSONException.getLocalizedMessage());
        }
        return "";
    }

}
