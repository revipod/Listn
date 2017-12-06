package com.example.mohsin.listn;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements MainActivityInterface {


    private static final String TAG = "MainActivity";
    Button loginBTN;
    Button registerBTN;
    EditText usernameET;
    EditText passwordET;

    MainAsyncRequests requests;
    DialogBox dialogBox;

    View centerView;
    Dialog newUserDialog;
    Dialog additionalregistermenuDialog;
    MainActivityInterface mainActivityInterface;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginBTN = (Button) findViewById(R.id.loginBTN);
        registerBTN = (Button) findViewById(R.id.registerBTN);
        usernameET = (EditText) findViewById(R.id.usernameET);
        passwordET = (EditText) findViewById(R.id.passwordET);
        centerView = (View) findViewById(R.id.centerdot);
        mainActivityInterface = new MainActivityInterface() {
            @Override
            public void loginUserI(JSONObject result) throws JSONException {
                loginUser(result);
            }

            @Override
            public void getNameI(Boolean found, String username) {
                getName(found,username);
            }

            @Override
            public void setNewUserI(JSONObject result) throws JSONException {
                setNewUser(result);
            }
        };
        requests = new MainAsyncRequests(mainActivityInterface);
        dialogBox = new DialogBox(this);

        callClickListeners();




    }

    private void callClickListeners()
    {
        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    checkUser(usernameET.getText().toString(), passwordET.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayUserNameMenu();
            }
        });
    }

    private void displayUserNameMenu() {
        newUserDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        newUserDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        newUserDialog.setCancelable(true);
        newUserDialog.setContentView(R.layout.username_menu);
        newUserDialog.show();
        Button continueBTN = (Button) newUserDialog.findViewById(R.id.continueBTN);
        final EditText usernameET = (EditText) newUserDialog.findViewById(R.id.usernameET);
        continueBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(usernameET.length() < 1)
                {
                    dialogBox.createDialog("Empty Username","Please insert a username","bad");
                }
                else
                {
                    JSONObject usernameParams = new JSONObject();
                    try {
                        usernameParams.put("username",usernameET.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        requests.checkUsername(usernameParams);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    public void getName(Boolean found, final String username){
        if(found)
        {
            dialogBox.createDialog("Username exists","Sorry but a user with this username already exists. Please try again.","bad");
        }
        else
        {
            additionalregistermenuDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            additionalregistermenuDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            additionalregistermenuDialog.setCancelable(true);
            additionalregistermenuDialog.setContentView(R.layout.additionalregistermenu);
            additionalregistermenuDialog.show();
            dialogBox.createDialog("Sucess!","Welcome to Listn " + username + "! Let's get you started!","good");
            newUserDialog.dismiss();

            Button nextBTN = (Button) additionalregistermenuDialog.findViewById(R.id.nextBTN);
            final EditText emailET = (EditText) additionalregistermenuDialog.findViewById(R.id.emailET);
            final EditText fullnameET = (EditText) additionalregistermenuDialog.findViewById(R.id.fullnameET);
            final EditText passwordET = (EditText) additionalregistermenuDialog.findViewById(R.id.passwordET);

            nextBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(emailET.length() < 5)
                    {
                        dialogBox.createDialog("Email", "Please insert a valid email address", "bad");
                    }
                    else {
                        if (fullnameET.length() < 3) {
                            dialogBox.createDialog("Empty Username", "Please insert your Full Name", "bad");
                        } else {
                            if (passwordET.length() < 5) {
                                dialogBox.createDialog("Password", "Please make sure your password is at least 5 characters.", "bad");
                                return;
                            } else {
                                JSONObject nameandemailParams = new JSONObject();
                                try {
                                    nameandemailParams.put("username",username);
                                    nameandemailParams.put("fullname", fullnameET.getText().toString().trim());
                                    nameandemailParams.put("password", passwordET.getText().toString().trim());
                                    nameandemailParams.put("email", emailET.getText().toString().trim());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    requests.checkEmail(nameandemailParams);
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            });
        }


    }

    public void setNewUser(final JSONObject result) throws JSONException {
        if (result.getBoolean("found"))
        {
            dialogBox.createDialog("Email exists","Sorry but a user with this email already exists. Please try again or reset your password.","bad");
        }
        else
        {
            JSONObject newUser;
            newUser = result.getJSONObject("user");
            additionalregistermenuDialog.dismiss();
            Intent intent = new Intent(getApplicationContext(), TabActivity.class);
            intent.putExtra("User",newUser.toString());
            intent.putExtra("loginorRegister","register");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void checkUser(String username, String password) throws JSONException {
        JSONObject loginParams = new JSONObject();
        loginParams.put("username",username);
        loginParams.put("password",password);
        requests.login(loginParams);
    }

    public void loginUser(JSONObject result) throws JSONException {
        if(result.getBoolean("loggedin")){
            JSONObject newUser;
            JSONObject postData = null;
            newUser = result.getJSONObject("user");
            if(result.has("posts")) postData = result.getJSONObject("posts");
            Intent intent = new Intent(getApplicationContext(), TabActivity.class);
            intent.putExtra("User",newUser.toString());
            if(postData!=null) intent.putExtra("postData",postData.toString());
            intent.putExtra("loginorRegister","login");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        else{
            dialogBox.createDialog("Not found","Either the username of password you inserted is in correct. Please try again.","bad");
        }


    }

    public void loginUserI(JSONObject result) throws JSONException {

    }

    public void getNameI(Boolean found, String username) {

    }

    public void setNewUserI(JSONObject result) throws JSONException {

    }

}
