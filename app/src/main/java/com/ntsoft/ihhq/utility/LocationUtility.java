package com.ntsoft.ihhq.utility;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 3/16/2016.
 */
public class LocationUtility {
    public static String getCountryName(Context context, LatLng latLng) {
        String countryName = "";
        ////
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latLng.latitude, latLng.longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
//                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
//                            sb.append(address.getAddressLine(i)).append("\n");
//                        }
//                        sb.append(address.getLocality());
//                        sb.append(address.getPostalCode()).append("\n");
//                        sb.append(address.getCountryName());
//                        searchKey = sb.toString();
//                        searchKey = address.getAddressLine(address.getMaxAddressLineIndex() - 1);
                if (address.getAdminArea() != null) {
                    countryName = address.getCountryName();/////Address[addressLines=[0:"Allen, OK 74825",1:"USA"],feature=74825,admin=Oklahoma,sub-admin=null,locality=Allen,thoroughfare=null,postalCode=74825,countryCode=US,countryName=United States,hasLatitude=true,latitude=34.8031254,hasLongitude=true,longitude=-96.429036,phone=null,url=null,extras=null]
//                            searchKey = searchKey.replace(" ", "_");

                }

            }
        } catch (IOException e) {
//                    Log.e(TAG, "Unable connect to Geocoder", e);
            e.printStackTrace();
        }
        return countryName;
    }
}
