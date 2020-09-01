package com.towhid.imagecompressor;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    private SharedPreferences mySharedPref;

    public SharedPref(Context context) {
        mySharedPref = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }

    void setDeny(Boolean deny) {
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putBoolean("deny", deny);
        editor.apply();
    }

    Boolean getDeny() {
        return mySharedPref.getBoolean("deny", false);
    }

}
