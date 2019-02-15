package com.example.fallennymous.prekjutelulas.Common;

import com.example.fallennymous.prekjutelulas.Model.User;
import com.example.fallennymous.prekjutelulas.Remote.APIService;
import com.example.fallennymous.prekjutelulas.Remote.RetrofitClient;

/**
 * Created by fallennymous on 06/06/2018.
 */

public class Common {
    public static User currenUser;

    private static final String BASE_URL = "https://fcm.googleapis.com/";
    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/";

    public static APIService getFCMService()
    {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

  //  public static IGoogleService getGoogleMapApi() {
  //      return IGeoRetrofit.getGoogleClient(GOOGLE_API_URL).create(IGoogleService.class);
 //   }

    public static String convertCodeToStatus(String status) {
        if (status.equals("0"))
            return "Pesanan diproses";
        else if (status.equals("1"))
            return "Pesanan dalam perjalanan ";
        else
            return "pesanan diterima";
    }
}
