package com.test.volley;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListModel implements Parcelable {

    private String image;
    private String description;
    private String price;

    public ListModel(String image, String description, String price) {
        this.image = image;
        this.description = description;
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public ListModel(Parcel in) {
        image = in.readString();
        description = in.readString();
        price = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeString(description);
        dest.writeString(price);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ListModel createFromParcel(Parcel in) {
            return new ListModel(in);
        }

        public ListModel[] newArray(int size) {
            return new ListModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public static ListModel cast(JSONObject object) throws JSONException {
        return new ListModel(object.optString("url"), object.optString("title"), object.optString("price"));
    }

    public static ArrayList<ListModel> cast(JSONArray array) throws JSONException {
        int size = array.length();
        ArrayList<ListModel> models = new ArrayList<ListModel>();
        for (int i = 0; i < size; i++) {
            models.add(cast(array.getJSONObject(i)));
        }
        return models;
    }
}
