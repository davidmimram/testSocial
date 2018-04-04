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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

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

    //-------comments-------//



    //^^^^^^^^^^^^^^^^^^^^^^^^^//



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");



        //------new 3.4.2018------//
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseLike.keepSynced(true);


        //--------comments---------//


        //^^^^^^^^^^^^^^^^^^^^^^^^^//


        try {
            currentUserID = mAuth.getCurrentUser().getUid();
        } catch (Exception e) {
            e.printStackTrace();
        }
        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        bottomtab = (RelativeLayout) findViewById(R.id.mainbootom_app_bar);
        AddNewPostButton = (Button) findViewById(R.id.new_post_uploade);
        upProfileImage = (CircleImageView) findViewById(R.id.asProfile);


        //~~~~~~~~~~~~~~~~~~~~~~new~~~~~~~~29.3.2018~~~~~~~~~~~~~~

        try {
            UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        try {
                            String image = dataSnapshot.child("profileImage").getValue().toString();
                            Picasso.with(MainActivity.this).load(image).placeholder(R.drawable.profile_ovel_two).into(upProfileImage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        //~~~~~~~~~~~~~~~~~~~~~~new~~~~~~~~~~~~~~~~~~~~~~


        //todo: david changed a few things!

        postList = (RecyclerView) findViewById(R.id.all_user_post_list);
        postList.setHasFixedSize(true);


        /*
         * todo oz:
         * fix 1: enable these methods to show the posts. make sure your DisplayallUserPost();
         * running at the end of them
         */

        DisplayallUserPost();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);
        postList.setAdapter(firebaseRecycleAdepter);


        /*
        mToolBar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Home")*/
        ;



    }


    // בדיקת כפתורים


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

        try {
            firebaseRecycleAdepter =
                    new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                            Post.class,
                            R.layout.posttrytwo,
                            PostViewHolder.class,
                            PostRef) {
                        @Override
                        protected void populateViewHolder(final PostViewHolder viewHolder, Post model, int position) {

                            //------new 3.4.2018------//
                            final String post_key = getRef(position).getKey ();

                            //^^^^^^^^^^^^^^^^^^^^^^^^^//

                            viewHolder.setFullname(model.getFullname());
                           /* viewHolder.setTime(model.getTime());
                            viewHolder.setDate(model.getDate());*/
                            viewHolder.setDescription(model.getDescription());
                            viewHolder.setProfileImage(getApplicationContext(), model.getProfileImage());
                            viewHolder.setPostimage(getApplicationContext(), model.getPostimage());


                            //------new 3.4.2018------//
                            viewHolder.setmLikeBtn (post_key);

                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

/*
                                    Toast.makeText(MainActivity.this, post_key, Toast.LENGTH_SHORT).show();
*/
                                }
                            });

                            viewHolder.mLikeBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v){

                                    mProcessClickLike = true;

                                    mDatabaseLike.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (mProcessClickLike) {

                                            if (dataSnapshot.child (post_key).hasChild (mAuth.getCurrentUser ().getUid ())) {

                                                mDatabaseLike.child (post_key).child (mAuth.getCurrentUser ().getUid ()).removeValue ();
                                                mProcessClickLike = false;


                                            } else {
                                                mDatabaseLike.child (post_key).child (mAuth.getCurrentUser ().getUid ()).setValue ("RandomValue");
                                                mProcessClickLike = false;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });



                                }
                            });


                            //^^^^^^^^^^^^^^^^^^^^^^^^^//

                        }



                    };




            postList.setAdapter(firebaseRecycleAdepter);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public static class PostViewHolder extends RecyclerView.ViewHolder {

        View mView;

        //------new 3.4.2018------//

        ImageView mLikeBtn;
        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuth;

        //^^^^^^^^^^^^^^^^^^^^^^^^^//


        public PostViewHolder(View itemView) {

            super(itemView);
            mView = itemView;

            //------new 3.4.2018------//
            mLikeBtn = (ImageView) mView.findViewById(R.id.likeIcon);
            mDatabaseLike = FirebaseDatabase.getInstance ().getReference ().child ("Likes");
            mAuth = FirebaseAuth.getInstance ();

            mDatabaseLike.keepSynced (true);
        }
        public void setmLikeBtn(final String post_key){




        mDatabaseLike.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange (DataSnapshot dataSnapshot) {

                if(dataSnapshot.child (post_key).hasChild (mAuth.getCurrentUser ().getUid ())){


                    mLikeBtn.setImageResource (R.drawable.btn_like_suggestion);
                    // כאן הקוד של האנימציה




                } else {
                    mLikeBtn.setImageResource (R.drawable.btn_like_suggestiongrey);


                }
            }

            @Override
            public void onCancelled (DatabaseError databaseError) {

            }
        });


        }
        //^^^^^^^^^^^^^^^^^^^^^^^^^//



        public void setFullname(String fullname) {

            TextView username = (TextView) mView.findViewById(R.id.mainUserText);
            username.setText(fullname);
        }


        public void setProfileImage(Context ctx, String profileImage) {

            ImageView imageView = mView.findViewById(R.id.mainUserImage);
            Picasso.with(ctx).load(profileImage).into(imageView);


        }

        /*        public void setTime(String time)
                {

                    TextView PostTime = (TextView) mView.findViewById(R.id.post_time);
                    PostTime.setText("   " + time);

                }

                public void setDate(String date)
                {
                    TextView PostDate = (TextView) mView.findViewById(R.id.post_date);
                    PostDate.setText("   " +date);

                }*/
        public void setDescription(String description) {
            TextView Postdescription = (TextView) mView.findViewById(R.id.mainCommentText);
            Postdescription.setText(description);

        }

        public void setPostimage(Context ctx, String postimages) {
            ImageView Postimage = (ImageView) mView.findViewById(R.id.mainPostImage);
            Picasso.with(ctx).load(postimages).into(Postimage);


//            Postimage.getLayoutParams().height = 321; // OR
//            Postimage.getLayoutParams().width = 361;
        }
    }

    //--------firday----///
    private void SendUserToPostActivity() {
        Intent addNewPostIntent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(addNewPostIntent);
    }
    //--------firday----///

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



/*    Animator spruceAnimator = new Spruce
            .SpruceBuilder(postList)
            .sortWith(new DefaultSort(*//*interObjectDelay=*//*50L))
            .animateWith(new Animator[]{DefaultAnimations.shrinkAnimator(postList, *//*duration=*//*800)})
            .start();*/

}







