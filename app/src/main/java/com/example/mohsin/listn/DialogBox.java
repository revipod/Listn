package com.example.mohsin.listn;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;

/**
 * Created by mabbasi on 8/3/2017.
 */

class DialogBox {

    private final Context context;


    public DialogBox(Context context) {
        this.context = context;

    }


    /*
  This function just creates a dialog box to display
  messages to inform the user whether their request was
  successful or not and displays error messages.
  */
    public void createDialog(String Title, String Message, String type) {
        final AlertDialog builder;
        Drawable d = context.getResources().getDrawable(R.mipmap.blackwrench);
        if (type.equals("good")) {
            d = context.getResources().getDrawable(R.mipmap.check);
        } else if (type.equals("bad")) {
            d = context.getResources().getDrawable(R.mipmap.sadface);
        }
        builder = new AlertDialog.Builder(context).create();
        builder.setTitle(Title);
        builder.setIcon(d);
        builder.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.dismiss();
            }
        });
        builder.setMessage(Message);
        builder.setCancelable(false);
        builder.show();

    }





}
