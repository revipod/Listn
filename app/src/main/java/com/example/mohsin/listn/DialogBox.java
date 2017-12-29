package com.example.mohsin.listn;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by mabbasi on 8/3/2017.
 */

public class DialogBox {

    Context context;
    Drawable d;
    public Dialog loadingScreen;

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
        d = context.getResources().getDrawable(R.mipmap.blackwrench);
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
                return;
            }
        });
        builder.setMessage(Message);
        builder.setCancelable(false);
        builder.show();

    }

    public void createDialog(String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Missing Field");
        builder.setIcon(R.mipmap.sadface);
        builder.setMessage(Message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog dialog = builder.show();
        TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER_VERTICAL);
        dialog.show();
    }



}
