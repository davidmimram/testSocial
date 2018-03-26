package ind.david.finalproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    // יצירה
    private Button LoginButton;
    private EditText user_Email,user_Password;
    private TextView needNewAccountLink;
    private ProgressDialog loadingBar;
    //-------------------------------
    private FirebaseAuth mAuth;
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    private static final int RC_SIGN_IN = 1;

    private GoogleApiClient mGoogleSignInClient;
    private static final String TAG = "LoginActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
        needNewAccountLink = (TextView) findViewById(R.id.register_account_link);
        user_Email = (EditText) findViewById(R.id.login_email);
        user_Password = (EditText) findViewById(R.id.login_password);
        LoginButton = (Button) findViewById(R.id.login_button);
        loadingBar = new ProgressDialog(this);
        //-------------------------------
        mAuth = FirebaseAuth.getInstance();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//




        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
        needNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // פתיחת מתודה!
                SendUserToRegisterActivity();





            }

        });

        LoginButton .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try {
                    AllowingUserToLogin(); //  מתודה קריאה למתודה שיצרנו
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                Toast.makeText(LoginActivity.this, "החיבור לגוגל נכשל", Toast.LENGTH_SHORT).show();
                
            }
        })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();


    }




    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //-------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

        }
    }




    private void firebaseAuthWithGoogle(GoogleSignInOptions acct) {
        int getId = Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getServerClientId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getServerClientId(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");

                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });
    }


    //-------------------------


         //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
        // אם יש את כל המידע על היוזר הוא ישלח ישר לmainactivity
         @Override
         protected void onStart() {
             FirebaseUser currectUser = mAuth.getCurrentUser();
             if (currectUser != null)
             {

                 SendToMainActivity();
             }
          super.onStart();
    }


    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//



    private void AllowingUserToLogin()
    {
        String email = user_Email.getText().toString();
        String password = user_Password.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "אנא הכנס אימייל", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "אנא הכנס סיסמא", Toast.LENGTH_SHORT).show();
        }
        else
            //------------------------------------
            // תיבה מה קורה בזמן שנוצר החשבון
            loadingBar.setTitle("מתחבר לחשבונך...");
            loadingBar.setMessage("אנא המתן...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
        //------------------------------------
            {
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        SendToMainActivity();
                        Toast.makeText(LoginActivity.this, "הינך התחברת בהצלחה", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                    else
                        {
                       String message = task.getException().getMessage();
                            Toast.makeText(LoginActivity.this, "בעיית כניסה ! אנא נסה שנית" +message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();


                    }
                }
            });
            }
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    // מה קורה שהיוזר מתחבר בהצלחה
    private void SendToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//



    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    private void SendUserToRegisterActivity() {
        Intent registert = new Intent(LoginActivity.this, RegisterActivity.class);
        registert.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(registert);



    }



    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
}
