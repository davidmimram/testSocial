package ind.david.finalproject;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // יצירה
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView postList;
    private RelativeLayout bottomtab;
    // new one//
    private ImageView AddNewPostButton;
    //-----------------------------------
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef,PostRef;
    private FirebaseRecyclerAdapter<Post, PostViewHolder> firebaseRecycleAdepter;

    //    private Toolbar mToolBar;
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~


//אביב ילד זין

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        bottomtab = (RelativeLayout) findViewById(R.id.mainbootom_app_bar);
        AddNewPostButton = (ImageView) findViewById(R.id.new_post_uploade);


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

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // בודק אם היוזר מחובר לפיירבייס או לחשבון - זה יעלהאת העמוד לוגין כראשון כמסך התחברות !!!
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currectUser = mAuth.getCurrentUser();
        if (currectUser == null)
        {

            SendUserToLogInActivity();
        }
        else {

            ChekUserExistence();
        }

    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // מה קורה אם היוזר לא קיים בדטה בייס מה נעשה
    private void ChekUserExistence()
    {
    final String current_user_id = mAuth.getCurrentUser().getUid();
    UserRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (!dataSnapshot.hasChild(current_user_id))  {
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
        public void onCancelled(DatabaseError databaseError)
        {

        }
    });
    }

    private void DisplayallUserPost() {

        try {
            firebaseRecycleAdepter =
                    new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                            Post.class,
                            R.layout.all_the_post_layout,
                            PostViewHolder.class,
                            PostRef) {
                        @Override
                        protected void populateViewHolder(PostViewHolder viewHolder, Post model, int position) {
                            viewHolder.setFullname(model.getFullname());
                            viewHolder.setTime(model.getTime());
                            viewHolder.setDate(model.getDate());
                            viewHolder.setDescription(model.getDescription());
                            viewHolder.setProfileImage(getApplicationContext(), model.getProfileImage());
                            viewHolder.setPostimage(getApplicationContext(), model.getPostimage());


                        }
                    };
            postList.setAdapter(firebaseRecycleAdepter);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static class PostViewHolder extends RecyclerView.ViewHolder
    {

        View mView;

        public PostViewHolder(View itemView) {

            super(itemView);
            mView = itemView;
        }
        public void setFullname(String fullname)
        {

            TextView username = (TextView) mView.findViewById(R.id.post_user_name);
            username.setText(fullname);
        }

        public void setProfileImage(Context ctx ,String profileImage)
        {

            CircleImageView imageView = (CircleImageView) mView.findViewById(R.id.post_profile_image);
            Picasso.with(ctx).load(profileImage).into(imageView);

        }

        public void setTime(String time)
        {

            TextView PostTime = (TextView) mView.findViewById(R.id.post_time);
            PostTime.setText("   " + time);

        }

        public void setDate(String date)
        {
            TextView PostDate = (TextView) mView.findViewById(R.id.post_date);
            PostDate.setText("   " +date);

        }
        public void setDescription(String description)
        {
            TextView Postdescription = (TextView) mView.findViewById(R.id.post_descripition);
            Postdescription.setText(description);

        }

        public void setPostimage(Context ctx, String postimages)
        {
            ImageView Postimage = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(postimages).into(Postimage);
//            Postimage.getLayoutParams().height = 321; // OR
//            Postimage.getLayoutParams().width = 361;
        }
    }

    //--------firday----///
    private void SendUserToPostActivity() {
        Intent addNewPostIntent = new Intent(MainActivity.this,PostActivity.class);
        startActivity(addNewPostIntent);
    }
    //--------firday----///

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void SendUserToActivity()
    {
        Intent setupIntent = new Intent(MainActivity.this,SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
        // most important in the app!!!!
        // סתומרת שאם אין מידע הוא ישלח אותנו לפה - רק אחרי שנמלא את כל המידע נגיע לדף הראשי
        // כאן היוזר בונה את הדאטה של עצמו

    }


    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private void SendUserToLogInActivity() {
        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }




    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~










    //~~~~~~Bottom Knobs~~~~~~~~//

    public void home(View view) {
                Toast.makeText(this, "home", Toast.LENGTH_SHORT).show();
    }
    public void search(View view) {
        Toast.makeText(this, "search", Toast.LENGTH_SHORT).show();
    }
    public void center(View view) {
        Intent intent = new Intent(MainActivity.this,PostActivity.class);
        startActivity(intent);
       // Toast.makeText(this, "center", Toast.LENGTH_SHORT).show();
    }
    public void like(View view) {
        Toast.makeText(this, "like", Toast.LENGTH_SHORT).show();
    }
    public void profile(View view) {
        Toast.makeText(this, "profile", Toast.LENGTH_SHORT).show();
    }




    //------------- מעביר לדף פוסט לייטאוט----------
    public void new_post_uploade(View view) {
        Intent intent = new Intent(MainActivity.this,PostActivity.class);
        startActivity(intent);
    }
}

///~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//








////Shutdown Codes:
//______________________

        //~~~ Header~~~~//
//     /*navigationView = (NavigationView) findViewById(R.id.navigation_view);*/

        /*// האדר שלנ של התפריט שלנוו*/
/*        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                userMenuSelector(item);
                return false;
            }
        });

    }*/

 /*   private void userMenuSelector(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.nav_profile:
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                break;



        case R.id.nav_home:
        Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
        break;

        case R.id.nav_find_friends:
                Toast.makeText(this, "Find Friends", Toast.LENGTH_SHORT).show();
                break;

        case R.id.nav_friends:
                Toast.makeText(this, "Friends", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_message:
                Toast.makeText(this, "Messages", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_Logout:
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                break;


        }
    }*/