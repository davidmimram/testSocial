package ind.david.finalproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
            AllowingUserToLogin(); //  מתודה קריאה למתודה שיצרנו
            }
        });
    }
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
        Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);


    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
}
