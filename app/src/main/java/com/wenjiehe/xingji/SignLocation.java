package com.wenjiehe.xingji;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by wenjie on 16/06/13.
 */
public class SignLocation implements Parcelable {

        public String street;
        public String city;
        public String province;
        public String locDescribe;

        public SignLocation(String province,String city,String street,String locDescribe){
            this.city=city;
            this.province = province;
            this.street = street;
            this.locDescribe = locDescribe;
    }

    protected SignLocation(Parcel in) {
        street = in.readString();
        city = in.readString();
        province = in.readString();
        locDescribe = in.readString();
    }

    public static final Creator<SignLocation> CREATOR = new Creator<SignLocation>() {
        @Override
        public SignLocation createFromParcel(Parcel in) {
            return new SignLocation(in);
        }

        @Override
        public SignLocation[] newArray(int size) {
            return new SignLocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(street);
        dest.writeString(city);
        dest.writeString(province);
        dest.writeString(locDescribe);
    }
}
