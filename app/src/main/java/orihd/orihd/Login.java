package orihd.orihd;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;

import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.internal.ImageRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Login extends AppCompatActivity implements View.OnClickListener {
    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    String Email;
    String Name;
    Button login;
    Button friends;
    Button mypet;
    Button guidance;
    private IWXAPI api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.content_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        callbackManager = CallbackManager.Factory.create();
        setSupportActionBar(toolbar);
        api = WXAPIFactory.createWXAPI(this, WXEntryActivity.API_ID , false);
        // Layout setup

        AccessToken token;
        token = AccessToken.getCurrentAccessToken();

        Profile profile = Profile.getCurrentProfile().getCurrentProfile();





        setContentView(R.layout.content_login);

        loginButton = (LoginButton)findViewById(R.id.login_button);


        mypet = (Button)findViewById(R.id.button3);
        mypet.setOnClickListener(this);







        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent registerIntent2 = new Intent(Login.this, Home.class);
                Login.this.startActivity(registerIntent2);
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.button3:
                Intent registerIntent1234 = new Intent(Login.this, Homemenu.class);
                Login.this.startActivity(registerIntent1234);

                break;


            case R.id.button:
                AccessToken token;
                token = AccessToken.getCurrentAccessToken();

                if (token != null) {
                    //Means user is logged in
                    Intent registerIntent12 = new Intent(Login.this, Home.class);
                    Login.this.startActivity(registerIntent12);
                }

                else {
                    Toast.makeText(getApplicationContext(), "You must login first", Toast.LENGTH_LONG).show();
                    break;
                }
        }
    }
    private void setFacebookData(final LoginResult loginResult)
    {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        try {
                            Log.i("Response",response.toString());

                            String email = response.getJSONObject().getString("email");
                            String firstName = response.getJSONObject().getString("first_name");
                            String lastName = response.getJSONObject().getString("last_name");
                            String gender = response.getJSONObject().getString("gender");

                            Email = email;
                            Name = firstName + " " + lastName;


                            Profile profile = Profile.getCurrentProfile();
                            String id = profile.getId();
                            String link = profile.getLinkUri().toString();
                            Log.i("Link",link);
                            if (Profile.getCurrentProfile()!=null)
                            {
                                Log.i("Login", "ProfilePic" + Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                            }

                            Log.i("Login" + "Email", email);
                            Log.i("Login"+ "FirstName", firstName);
                            Log.i("Login" + "LastName", lastName);
                            Log.i("Login" + "Gender", gender);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public String getName(){
        return Name;
    }
    public void onResume(){
        super.onResume();
        if(WXEntryActivity.token != null){
            Toast.makeText(this, "Token: " + WXEntryActivity.token, Toast.LENGTH_LONG).show();
            WXEntryActivity.token = null;
        }
    }

    /**
     * Register first. This notifies WeChat about your application.
     * @param view
     */
    public void onClickRegisterButton(View view) {
        boolean success = api.registerApp(WXEntryActivity.API_ID);
        Toast.makeText(this, "Registration Success: " + success, Toast.LENGTH_LONG).show();
    }

    /**
     * Send an authorization request to WeChat. If successful, you will be moved to the WeChat
     * app and you should see the application details you registered through the portal. When
     * you grant access, WeChat should close and sent a response to WXEntryActivity.
     * @param view
     */
    public void onClickLoginButton(View view) {
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "none";
        api.sendReq(req);
    }

}
