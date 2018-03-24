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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //defult
    private Toolbar mUpBar;
    private  ImageView returnIcon;
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // new for this screen:
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private ImageButton SelectPostImage;
    private Button UpdatePostButton;
    private EditText postDescripition;
    private Uri ImageUri;
    private  String Descripition;
    private StorageReference PostImagesRefernces;
    private String saveCurrentDate,saveCurrentTime,postRandomName,downloadUrl,current_user_id;
    private DatabaseReference  UserRef, PostsRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;



    private static final int Gallery_Pick = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        returnIcon = (ImageView) findViewById(R.id.back);
        SelectPostImage = (ImageButton) findViewById(R.id.selectPostImage);
        UpdatePostButton = (Button) findViewById(R.id.postButton);
        postDescripition = (EditText) findViewById(R.id.PostText);


        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        PostImagesRefernces = FirebaseStorage.getInstance().getReference();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        loadingBar = new ProgressDialog(this);



        SelectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallaery();
            }
        });

        UpdatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValiDatePostInfo();

            }
        });
    }

    private void ValiDatePostInfo() {
        Descripition = postDescripition.getText().toString();
        if(ImageUri == null)
        {
            Toast.makeText(this, "אנא בחר תמונה", Toast.LENGTH_SHORT).show();

         }
        else if(TextUtils.isEmpty(Descripition))
        {
            Toast.makeText(this, "הינך חייב למלאות כ10 תווים לפחות", Toast.LENGTH_SHORT).show();

        }
        else {
            StorinImageToFireBaseStorge();
            //------------------------------------
            // תיבה מה קורה בזמן שנוצר החשבון
            loadingBar.setTitle("מעלה פוסט חדש!");
            loadingBar.setMessage("אנא המתן בזמן העלאת הפוסט");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            //------------------------------------

        }

         }

    private void StorinImageToFireBaseStorge()
    {

        Calendar callforDate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentdate.format(callforDate.getTime());

        //~~~~~~~~~~~~~~~CallFortime~~~~~~~~~~~~~
        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm ");
        saveCurrentTime = currentTime.format(callforDate.getTime());


        postRandomName = saveCurrentDate + saveCurrentTime;


        StorageReference filePath = PostImagesRefernces.child("postimages").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");


        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful())
                {

                    downloadUrl = task.getResult().getDownloadUrl().toString();
                    Toast.makeText(PostActivity.this, "התמונה עלתה בהצלחה!", Toast.LENGTH_SHORT).show();

                    SaveingPostInfomationToDataBase();
                }
                else {
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, "שגיאה בהעלאת התמונה" + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SaveingPostInfomationToDataBase() {

        UserRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                if(dataSnapshot.exists())
                {

                    String userFullName = dataSnapshot.child("full name").getValue().toString();
                    String profileImage = dataSnapshot.child("profileImage").getValue().toString();

                    HashMap postMap = new HashMap();
                    postMap.put("uid",current_user_id);
                    postMap.put("date",saveCurrentDate);
                    postMap.put("time",saveCurrentTime);
                    postMap.put("description",Descripition);
                    postMap.put("postimage",downloadUrl);
                    postMap.put("profileImage",profileImage);
                    postMap.put("full_name",userFullName);
                    PostsRef.child(current_user_id + postRandomName).updateChildren(postMap).addOnCompleteListener
                            (new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful())
                            {


                             /*   SendUserToActivity();*/

                                loadingBar.dismiss();
                                Toast.makeText(PostActivity.this, "הפוסט עודכן בהצלחה", Toast.LENGTH_SHORT).show();
                            }
                            else 
                            {

                                loadingBar.dismiss();
                                Toast.makeText(PostActivity.this, "שגיאה בזמן עדכון הפוסט", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void OpenGallaery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode==RESULT_OK && data !=null)
        {

            ImageUri = data.getData();
            SelectPostImage.setImageURI(ImageUri);
        }

    }

    public void SendToMainActivity(View view)
    {
        Intent intent = new Intent(PostActivity.this,MainActivity.class);
        startActivity(intent);

    }

    private void SendUserToActivity()
    {
        Intent setupIntent = new Intent(PostActivity.this,SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
        // most important in the app!!!!
        // סתומרת שאם אין מידע הוא ישלח אותנו לפה - רק אחרי שנמלא את כל המידע נגיע לדף הראשי
        // כאן היוזר בונה את הדאטה של עצמו

    }


}
