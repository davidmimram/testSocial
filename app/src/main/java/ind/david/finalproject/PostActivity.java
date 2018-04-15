package ind.david.finalproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class PostActivity extends AppCompatActivity implements View.OnClickListener{

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //defult
    private Toolbar mUpBar;
    private ImageView returnIcon;
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // new for this screen:
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private ImageButton SelectPostImage;
    private Button UpdatePostButton;
    private EditText postDescripition;
    //private Uri ImageUri;
    private String Descripition;
    private StorageReference PostImagesRefernces;
    private String saveCurrentDate, saveCurrentTime, timeOfImage, downloadUrl, current_user_id;
    private DatabaseReference UserRef, PostsRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;


    private HashMap<String,Boolean> iconStore;
    private int counter;



    private ImageView castro,mango,zara,factory,breshka,pullbear;











    //test
    SharedPreferences sharedpreference;



    String currentUserID;
    private CircleImageView upProfileImage;

    Uri ImageUri;
    private static final int Gallery_Pick = 1;



    //OnnnnnnnCreated!!!!!!!!
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploade_posttwo);

        returnIcon = (ImageView) findViewById(R.id.back);
        SelectPostImage = (ImageButton) findViewById(R.id.selectPostImage);
        UpdatePostButton = (Button) findViewById(R.id.postButton2);
        postDescripition = (EditText) findViewById(R.id.PostText);


    //~~~~dvir 10.4.2018~~~~/
        iconStore = new HashMap<> ();
        counter = 0;
        iconStore.put ("paulbear",false);
        iconStore.put ("breshka",false);
        iconStore.put ("castro",false);
        iconStore.put ("zara",false);
        iconStore.put ("factory",false);
        iconStore.put ("mango",false);





        //------- todo NOT WORING ! ------///
        castro = (ImageView) findViewById (R.id.castro);
        zara = (ImageView) findViewById (R.id.zara);
        factory = (ImageView) findViewById (R.id.factory);
        pullbear = (ImageView) findViewById (R.id.pullbear);
        mango = (ImageView) findViewById (R.id.mango);
        breshka = (ImageView) findViewById (R.id.breshka);


        castro.setOnClickListener (this);
        zara.setOnClickListener (this);
        factory.setOnClickListener (this);
        pullbear.setOnClickListener (this);
        mango.setOnClickListener (this);
        breshka.setOnClickListener (this);





        //------- todo NOT WORING ! ------///





        ///----------///

//        currentUserID = mAuth.getCurrentUser().getUid();

        upProfileImage = (CircleImageView) findViewById(R.id.asProfile2);


        ///---//////

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        PostImagesRefernces = FirebaseStorage.getInstance().getReference();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        loadingBar = new ProgressDialog(this);





        Compressor compressor = new Compressor(this);

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



            UserRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists ()) {


                        String image = dataSnapshot.child ("profileImage").getValue ().toString ();
                        Picasso.with (PostActivity.this).load (image).placeholder (R.drawable.hanni).into (upProfileImage);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText (PostActivity.this, databaseError.getMessage (), Toast.LENGTH_SHORT).show ();
                }
            });


    }

    private void ValiDatePostInfo() {
        Descripition = postDescripition.getText().toString();
        if (!didPickImage) {
            Toast.makeText(this, "אנא בחר תמונה", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(Descripition)) {
            Toast.makeText(this, "הינך חייב למלאות כ10 תווים לפחות", Toast.LENGTH_SHORT).show();

        } else {
            for (int i = 0; i < iconStore.size () ; i++) {
                iconStore.values ().remove (false);

            }


//            Toast.makeText (this, iconStore.size (), Toast.LENGTH_SHORT).show ();
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


    @Override
    public void onClick (View v) {
        switch (v.getId ()) {
            case R.id.zara:
                //dvir king of the world! thank u !

                    if (iconStore.get ("zara").equals (true)){
                        zara.setImageResource (R.drawable.zara);
                        iconStore.put ("zara",false);
                        counter--;
                    } else if (counter < 3 ) {
                        iconStore.put ("zara",true);
                        zara.setImageResource (R.drawable.zarach);

                        counter++;
                    }
                break;

            case R.id.castro:
//
                //dvir king of the world! thank u !

                if (iconStore.get ("castro").equals (true)){
                    castro.setImageResource (R.drawable.castro);
                    iconStore.put ("castro",false);
                    counter--;
                } else if (counter < 3 ) {
                    iconStore.put ("castro",true);
                    castro.setImageResource (R.drawable.castroch);

                    counter++;
                }
                break;

            case R.id.pullbear:

                //dvir king of the world! thank u !
                if (iconStore.get ("paulbear").equals (true)){
                    pullbear.setImageResource (R.drawable.paulbear);
                    iconStore.put ("paulbear",false);
                    counter--;
                } else if (counter < 3 ) {
                    iconStore.put ("paulbear",true);
                    pullbear.setImageResource (R.drawable.pullbearch);

                    counter++;
                }

                break;


            case R.id.factory:
                //dvir king of the world! thank u !
                if (iconStore.get ("factory").equals (true)){
                    factory.setImageResource (R.drawable.factory);
                    iconStore.put ("factory",false);
                    counter--;
                } else if (counter < 3 ) {
                    iconStore.put ("factory",true);
                    factory.setImageResource (R.drawable.factorych);

                    counter++;
                }

                break;


            case R.id.breshka:
                if (iconStore.get ("breshka").equals (true)){
                    breshka.setImageResource (R.drawable.breshka);
                    iconStore.put ("breshka",false);
                    counter--;
                } else if (counter < 3 )
                {
                    iconStore.put ("breshka",true);
                    breshka.setImageResource (R.drawable.breskach);

                    counter++;
                }

                break;



            case R.id.mango:
                //dvir king of the world! thank u !
                if (iconStore.get ("mango").equals (true)){

                    mango.setImageResource (R.drawable.mango);
                    iconStore.put ("mango",false);
                    counter--;
                } else if (counter < 3 ){

                    iconStore.put ("mango",true);
                    mango.setImageResource (R.drawable.mangoch);

                    counter++;

                }
                break;


        }

    }

    private void StorinImageToFireBaseStorge() {

        Calendar callforDate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MM-yyyy");
        saveCurrentDate = currentdate.format(callforDate.getTime());

        //~~~~~~~~~~~~~~~CallFortime~~~~~~~~~~~~~
        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH-mm");
        saveCurrentTime = currentTime.format(callforDate.getTime());


        timeOfImage = saveCurrentDate + saveCurrentTime;


        StorageReference filePath = PostImagesRefernces.child("Posts").child("Post_" + timeOfImage + ".jpg");



        filePath.putFile(ImageUri).addOnSuccessListener (new OnSuccessListener<UploadTask.TaskSnapshot> () {
            @Override
            public void onSuccess (UploadTask.TaskSnapshot taskSnapshot) {

                downloadUrl = taskSnapshot.getDownloadUrl ().toString ();

                Toast.makeText (PostActivity.this, "התמונה עלתה בהצלחה!", Toast.LENGTH_SHORT).show ();


                SaveingPostInfomationToDataBase ();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.getMessage();
                Toast.makeText(PostActivity.this, "שגיאה בהעלאת התמונה" + message, Toast.LENGTH_SHORT).show();
            }




        });
    }






    ///------
    private void SaveingPostInfomationToDataBase() {

        UserRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    String userFullName = dataSnapshot.child("full_name").getValue().toString();
                    String profileImage = dataSnapshot.child("profileImage").getValue().toString();

                    HashMap postMap = new HashMap();
                    postMap.put("uid", current_user_id);
                    postMap.put("date", saveCurrentDate);
                    postMap.put("time", saveCurrentTime);
                    postMap.put("description", Descripition);
                    postMap.put("postimage", downloadUrl);
                    postMap.put("profileImage", profileImage);
                    postMap.put("fullName", userFullName);
                    postMap.put("iconStore",iconStore);
                    postMap.put ("Likecount",0);
                    PostsRef.child(current_user_id + timeOfImage).updateChildren(postMap).addOnSuccessListener (new OnSuccessListener () {
                        @Override
                        public void onSuccess (Object o) {

                            Intent intent = new Intent (PostActivity.this, MainActivity.class);
                            startActivity (intent);

                            loadingBar.dismiss ();
                            Toast.makeText (PostActivity.this, "הפוסט עודכן בהצלחה", Toast.LENGTH_SHORT).show ();

                        }
                    }).addOnFailureListener (new OnFailureListener () {
                        @Override
                        public void onFailure (@NonNull Exception e) {
                            Toast.makeText (PostActivity.this, e.toString (), Toast.LENGTH_SHORT).show ();
                        }
                    });




                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText (PostActivity.this, databaseError.getMessage (), Toast.LENGTH_SHORT).show ();
            }
        });
    }



    private void OpenGallaery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);

    }

    boolean didPickImage = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {

             ImageUri = data.getData();

            didPickImage = true;
            Picasso.with(this).load(data.getData()).resize(500 , 385).into(SelectPostImage);
            //  SelectPostImage.setImageURI(ImageUri);
        }

    }

    public void SendToMainActivity(View view) {
        Intent intent = new Intent(PostActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }


    private void SendUserToActivity() {
        Intent setupIntent = new Intent(PostActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
        // most important in the app!!!!
        // סתומרת שאם אין מידע הוא ישלח אותנו לפה - רק אחרי שנמלא את כל המידע נגיע לדף הראשי
        // כאן היוזר בונה את הדאטה של עצמו

    }



    // כפתורים




    //ICONS Post:




    // TODO: 4/6/18 בקובץ זה יש הכל כדי להכניס תמונה למעלה אך הבעיה היא שצריך פלייסהולדר זה יקח הרבה זמן אבל שווה לעשות ! ואז התמונת פרופיל תוצג בכל הסרגלים :)


}
