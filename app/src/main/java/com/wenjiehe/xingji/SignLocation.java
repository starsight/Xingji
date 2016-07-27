package com.wenjiehe.xingji;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by wenjie on 16/06/13.
 */
public class SignLocation {

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
}
