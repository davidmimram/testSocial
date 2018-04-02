package ind.david.finalproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private EditText userName, FullName,CountryName;
    private Button SaveinformationBotton;
    private CircleImageView profileImage;
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;
    private StorageReference UserProfileImageRef;
    private ProgressDialog loadingBar;
    //--------------------------------------
    String currentUserID;
    final static int Gallery_Pick = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_write_info);


        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        userName = (EditText) findViewById(R.id.setup_userName);
        FullName = (EditText) findViewById(R.id.setup_fullname);
        CountryName = (EditText) findViewById(R.id.setup_country);
        SaveinformationBotton = (Button) findViewById(R.id.setup_infromation_button);
        profileImage = (CircleImageView) findViewById(R.id.setup_profile_Image);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        loadingBar = new ProgressDialog(this);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~






        //--------------------------------------

        SaveinformationBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SaveAccountSetupInfo();
            }
        });
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        //this

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {
                    String image = dataSnapshot.child("profileImage").getValue().toString();

                    Picasso.with(SetupActivity.this).load(image).placeholder(R.drawable.profile).into(profileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // בחירת תמונה והקוד שיושב במניספיפס ובגרדלמודולאפ - בחירת גודל והבאת הספריה
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Gallery_Pick && requestCode==RESULT_OK && data!=null);
        {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {

                //------------------------------------
                // תיבה מה קורה בזמן שנוצר החשבון
                loadingBar.setTitle("תמונת פרופיל");
                loadingBar.setMessage("אנא המתן בזמן העלאת תמונת הפרופיל");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);
                //------------------------------------


                Uri resultUri = result.getUri();
                StorageReference filePath= UserProfileImageRef.child(currentUserID + ".jpg");

                //new Metohd inside
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful())
                        {

                            Toast.makeText(SetupActivity.this, "תמונתך נשמרה", Toast.LENGTH_SHORT).show();
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            UserRef.child("profileImage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            Intent selfIntent = new Intent(SetupActivity.this,SetupActivity.class);
                                            startActivity(selfIntent);

                                            if (task.isSuccessful()){
                                                Toast.makeText(SetupActivity.this, "תמונתך נשמרה.", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();

                                            }
                                            else
                                            {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(SetupActivity.this, "התמונה אינה נשמרה במערכת!" + message, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });

                        }

                    }
                });

            }
            else
            {
                Toast.makeText(this, "בעיית העלאה - נסה שנית.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();

            }
        }
    }


    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private void SaveAccountSetupInfo()
    {

        String username = userName.getText().toString();
        String fullname = FullName.getText().toString();
        String country = CountryName.getText().toString();

        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this, "אנא מלא שם המשתמש", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(fullname)){
            Toast.makeText(this, "אנא הזן שם מלא", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(country)){
            Toast.makeText(this, "אנא הזן מדינה", Toast.LENGTH_SHORT).show();
        }
        else {

            //------------------------------------
            // תיבה מה קורה בזמן שנוצר החשבון
            loadingBar.setTitle("שומר את המידע...");
            loadingBar.setMessage("אנא המתן חשבונך מעודכן...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            //------------------------------------

            // מאחסן בדה בייס - פיירבייס
            HashMap userMap = new HashMap();
            userMap.put("user name",username);
            userMap.put("full_name",fullname);
            userMap.put("country",country);
            // כאן נוכל לקחת עוד מידע מהשתמש כמו למשל ביוגרפיה או כל דבר אחר
            userMap.put("status","this is post msg");
            userMap.put("gender","none");
            userMap.put("bod","none");
            userMap.put("relationshipstatus","none");
            UserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (task.isSuccessful()){
                        SendUserToMainActivity();
                        Toast.makeText(SetupActivity.this, "חשבונך נוצר בהצלחה!", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this,"בעיה ביצירת החשבון" + message,Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });

        }
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void SendUserToMainActivity() {
        Intent setupIntent = new Intent(SetupActivity.this,MainActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();



    }
}