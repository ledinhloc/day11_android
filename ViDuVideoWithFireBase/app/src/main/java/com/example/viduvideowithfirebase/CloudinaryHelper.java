package com.example.viduvideowithfirebase;

import android.content.Context;

import com.cloudinary.android.MediaManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CloudinaryHelper {
    public static void initCloudinary(Context context) {
        try {
            Properties props = new Properties();
            InputStream inputStream = context.getAssets().open("cloudinary_config.properties");
            props.load(inputStream);

            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", props.getProperty("cloud_name"));
            config.put("api_key", props.getProperty("api_key"));
            config.put("api_secret", props.getProperty("api_secret"));

            MediaManager.init(context, config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
