package ind.david.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // יצירה
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView postList;
    private RelativeLayout bottomtab;
    // new one//
    private Button AddNewPostButton;
    //-----------------------------------
    //circle userprofile:
    String currentUserID;
    private CircleImageView upProfileImage;

    //-----------------------------------
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef, PostRef, UserUpProfile;
    private FirebaseRecyclerAdapter<Post, PostViewHolder> firebaseRecycleAdepter;

    //------new 3.4.2018------//
    private DatabaseReference mDatabaseLike;


    private boolean mProcessClickLike = false;

    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator ();
    private  static final AccelerateDecelerateInterpolator ACCELERATE_DECELERATE_INTERPOLATOR = new AccelerateDecelerateInterpolator ();


    private HashMap<String,Boolean> iconStore2;
    private ArrayList<ImageView> iconImgArray;
    private ArrayList<String> iconUid;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");





        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseLike.keepSynced(true);





        iconStore2 = new HashMap<> ();
        iconUid = new ArrayList<> ();
        iconImgArray = new ArrayList<> ();




        currentUserID = mAuth.getCurrentUser().getUid();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        bottomtab = (RelativeLayout) findViewById(R.id.mainbootom_app_bar);
        AddNewPostButton = (Button) findViewById(R.id.new_post_uploade);
        upProfileImage = (CircleImageView) findViewById(R.id.asProfile);


            UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                            String image = dataSnapshot.child("profileImage").getValue().toString();
                            Picasso.with(MainActivity.this).load(image).placeholder(R.drawable.profile_ovel_two).into(upProfileImage);
                        }
                    }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        postList = (RecyclerView) findViewById(R.id.all_user_post_list);
        postList.setHasFixedSize(true);


        DisplayallUserPost();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);
        postList.setAdapter(firebaseRecycleAdepter);

    }



    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // בודק אם היוזר מחובר לפיירבייס או לחשבון - זה יעלהאת העמוד לוגין כראשון כמסך התחברות !!!
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {

            SendUserToLogInActivity();
        } else {

            CheckUserExistence();
        }

    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // מה קורה אם היוזר לא קיים בדטה בייס מה נעשה
    private void CheckUserExistence() {
        final String current_user_id = mAuth.getCurrentUser().getUid();
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(current_user_id)) {
                    SendUserToActivity();

                    //--------firday----///
                    AddNewPostButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SendUserToPostActivity();
                        }
                    });

                } else {
                    DisplayallUserPost();
                }

            }

            //--------firday----///
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }








    private void DisplayallUserPost() {

        firebaseRecycleAdepter =
                new FirebaseRecyclerAdapter<Post, PostViewHolder> (
                        Post.class,
                        R.layout.posttrytwo,
                        PostViewHolder.class,
                        PostRef) {
                    @Override
                    protected void populateViewHolder (final PostViewHolder viewHolder, final Post model, int position) {

                        //------new 3.4.2018------//
                        final String post_key = getRef (position).getKey ();

                        //^^^^^^^^^^^^^^^^^^^^^^^^^//

                        viewHolder.setFullname (model.getFullName ());
                           /* viewHolder.setTime(model.getTime());
                            viewHolder.setDate(model.getDate());*/
                        viewHolder.setDescription (model.getDescription ());
                        viewHolder.setProfileImage (getApplicationContext (), model.getProfileImage ());
                        viewHolder.setPostimage (getApplicationContext (), model.getPostimage ());
                        viewHolder.likesCounts.setText (String.valueOf (model.getLikecount ()));





                        //Icon Secssion:
                        //^^^^^^^^^^^^^^^^^^
                        iconStore2 = model.getIconStore ();





                        iconImgArray.add (viewHolder.shop1);
                        iconImgArray.add (viewHolder.shop2);
                        iconImgArray.add (viewHolder.shop3);

                        if (!iconStore2.keySet ().isEmpty ()) {

                            for (String s : iconStore2.keySet ()) {

                                iconUid.add (s);
                            }


                            for (int i = 0; i < iconUid.size (); i++) {
                                String s = iconUid.get (i);

                                int identifier = getApplicationContext ().getResources ().getIdentifier (s, "drawable", getApplicationContext ().getPackageName ());

                                if (iconImgArray.get (i) != null) {

                                    iconImgArray.get (i).setImageResource (identifier);
                                    iconImgArray.get (i).setVisibility (View.VISIBLE);

                                }
                            }
                        }


                        viewHolder.mLikeBtn.setOnClickListener (new View.OnClickListener () {
                            @Override
                            public void onClick (View v) {
                                if (!mProcessClickLike) {
                                    PostRef.child (post_key).child ("likecount").setValue (model.getLikecount () + 1);
                                            Picasso.with (getApplicationContext ()).load (R.drawable.btn_like_suggestiongrey).into (viewHolder.likeBtn);
                                            mProcessClickLike = true;


                                } else  {

                                    PostRef.child (post_key).child ("likecount").setValue (model.getLikecount () - 1);
                                            Picasso.with (getApplicationContext ()).load (R.drawable.btn_like_suggestion).into (viewHolder.likeBtn);
                                            mProcessClickLike = false;
                                        }
                            }
                        });


                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                updateNumOfLikes(model.getUid ());

                                }
                            });



                            //^^^^^^^^^^^^^^^^^^^^^^^^^//

                        }



                    };




            postList.setAdapter(firebaseRecycleAdepter);



    }

    public void todo1 (View view) {
        Toast.makeText (this, "כאן יהיה דיאלוג בוקס ", Toast.LENGTH_SHORT).show ();
    }














     // קלאס זה מציג את המידע של הפוסט כולו.
    public static class PostViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageView mLikeBtn;
        TextView likesCounts;
        TextView username;
        ImageView imageView;
        TextView Postdescription;
        ImageView Postimage;
        ImageView likeBtn;
        ImageView shop1;
        ImageView shop2;
        ImageView shop3;


        public PostViewHolder(View itemView) {

            super(itemView);
            mView = itemView;

            mLikeBtn = (ImageView) mView.findViewById(R.id.likeIcon);
            likesCounts = (TextView) mView.findViewById (R.id.tvlikenumbers);
            username = (TextView) mView.findViewById(R.id.mainUserText);
            imageView = mView.findViewById(R.id.mainUserImage);
            Postdescription = (TextView) mView.findViewById(R.id.mainCommentText);
            Postimage = (ImageView) mView.findViewById(R.id.mainPostImage);
            likeBtn = mView.findViewById (R.id.likeIcon);
            shop1 = mView.findViewById (R.id.shop1);
            shop2 = mView.findViewById (R.id.shop2);
            shop3 = mView.findViewById (R.id.shop3);




//            mAuth = FirebaseAuth.getInstance ();


        }




        public void setFullname(String fullname) {

            username.setText(fullname);
        }


        public void setProfileImage(Context ctx, String profileImage) {

            Picasso.with(ctx).load(profileImage).into(imageView);


        }


        public void setDescription(String description) {

            Postdescription.setText(description);

        }

        public void setPostimage(Context ctx, String postimages) {

            Picasso.with(ctx).load(postimages).into(Postimage);

            // זום אין לתמונת פוסט zoom
//            PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher (Postimage);
//            photoViewAttachegitr.update ();
//            photoViewAttacher.getScale ();


         /*   Postimage.getLayoutParams().height = 321; // OR
            Postimage.getLayoutParams().width = 361;*/
        }

    }

    private void SendUserToPostActivity() {
        Intent addNewPostIntent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(addNewPostIntent);
    }


    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void SendUserToActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
        // most important in the app!!!!
        // סתומרת שאם אין מידע הוא ישלח אותנו לפה - רק אחרי שנמלא את כל המידע נגיע לדף הראשי
        // כאן היוזר בונה את הדאטה של עצמו

    }


    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private void SendUserToLogInActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //~~~~~~Bottom Knobs~~~~~~~~//

    public void home(View view) {

        Button button = (Button) findViewById(R.id.icon_home);
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

        button.startAnimation(myAnim);


//                Toast.makeText(this, "home", Toast.LENGTH_SHORT).show();
    }

    public void search(View view) {


        Button button = (Button) findViewById(R.id.icon_search);
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

        button.startAnimation(myAnim);


//        Toast.makeText(this, "search", Toast.LENGTH_SHORT).show();
    }

    public void center(View view) {


        // Use bounce interpolator with amplitude 0.2 and frequency 20
//        Handler handler=new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {

        Button button = (Button) findViewById(R.id.icon_center);
        final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);

        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

        button.startAnimation(myAnim);
        Intent intent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(intent);


//        },2000);





       /* Button button = (Button)findViewById(R.id.icon_center);
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        button.startAnimation(myAnim);*/


        /// צריך פה אנדלר כדי לעקב שהאייקון יספיק לעשות את האפקט שלו


//        Toast.makeText(this, "center", Toast.LENGTH_SHORT).show();
    }

    public void like(View view) {


        Button button = (Button) findViewById(R.id.icon_like);
        Button button2 = (Button) findViewById(R.id.imageView2);
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

        button.startAnimation(myAnim);


//        Toast.makeText(this, "like", Toast.LENGTH_SHORT).show();
    }

    public void profile(View view) {


        Button button = (Button) findViewById(R.id.icon_profile);
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

        button.startAnimation(myAnim);


//        Toast.makeText(this, "profile", Toast.LENGTH_SHORT).show();
    }


    //------------- מעביר לדף פוסט לייטאוט----------

    public void new_post_uploade(View view) {

        Button button = (Button) findViewById(R.id.new_post_uploade);
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

        button.startAnimation(myAnim);


        Intent intent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(intent);
    }














}







