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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private EditText UserEmail,UserPassword,UserConmfirodPassword;
    private Button CreateAccountButton;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        mAuth = FirebaseAuth.getInstance(); // < FIREBASE
        //----------------------------------------
        UserEmail = (EditText) findViewById(R.id.register_email);
        UserPassword = (EditText) findViewById(R.id.register_password);
        UserConmfirodPassword = (EditText) findViewById(R.id.register_password_confiromd);
        CreateAccountButton = (Button) findViewById(R.id.register_create_account);
        loadingBar = new ProgressDialog(this);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~




        //~~~~~~~~~~~~~~~MetodCreated~~~~~~~~~~~~~~~~~~~~~~~
        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CreateNewAccount();
            }
        });
    }


//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // אם יש דאטה זה ידלג על השלב הזה
    @Override
    protected void onStart() {
        FirebaseUser currectUser = mAuth.getCurrentUser();
        if (currectUser == null)
        {

            SendUserToMainActivity();
        }
        super.onStart();
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);


    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~




    private void CreateNewAccount() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String confirmPassword = UserConmfirodPassword.getText().toString();


        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "הכנס אימייל", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "הכנס סיסמא", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(confirmPassword))
        {
            Toast.makeText(this, "אנא אשר את הסיסמא", Toast.LENGTH_SHORT).show();
        }
        else if(!password.equals(confirmPassword))
        {
            Toast.makeText(this, "סיסמא אינה תואמת לסיסמאת אימות", Toast.LENGTH_SHORT).show();

        }
        else
            {
                //------------------------------------
                // תיבה מה קורה בזמן שנוצר החשבון
                loadingBar.setTitle("יוצר חשבון חדש");
                loadingBar.setMessage("אנא המתן...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);
                //------------------------------------

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //~~~ אם הכניסה הצליחה ~~~~
                        SendUserToSetupActivity();

                        if(task.isSuccessful())
                        {
                            Toast.makeText(RegisterActivity.this, "התחברת בהצלחה", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                        else {
                            String message = task.getException().getMessage();
                            Toast.makeText(RegisterActivity.this,  "הכניסה נכשלה" +message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
        }

    }

    //~~~~~~~~~~~~~~~~~~~~מתודה שתעביר שהכניסה הצליחה~~~~~~~~~~~~
    private void SendUserToSetupActivity()
    {
        Intent setupIntent = new Intent(RegisterActivity.this,SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
}
