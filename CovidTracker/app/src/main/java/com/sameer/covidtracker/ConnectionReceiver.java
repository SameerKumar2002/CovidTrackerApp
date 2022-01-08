package com.sameer.covidtracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ConnectionReceiver extends BroadcastReceiver {

    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;

        if(isConnected(context)){
            Toast.makeText(context,"Connected",Toast.LENGTH_LONG).show();
        }else {

            showDialog(true);

        }

    }

    public boolean isConnected(Context context){

        try {

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();

            return (info != null && info.isConnected());

        }catch (NullPointerException exception){

            exception.printStackTrace();
            return false;

        }catch (Exception exception){

            exception.printStackTrace();
            return false;

        }
    }

    public void showDialog(boolean isEnable){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.internet_dialog,null);
        Button button = view.findViewById(R.id.id_retry);
        builder.setView(view);

        final Dialog dialog = builder.create();
        dialog.setCancelable(false);
          dialog.show();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected(mContext)){
                    dialog.dismiss();
                }else {
                    dialog.show();
                }
            }
        });

    }

}
