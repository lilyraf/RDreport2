package com.ict602.rdreport;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

public class AboutActivity extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView headerProfileImageView;
    private TextView headerNameTextView; // Declare TextView to show the user's name in the header
    private TextView headerEmailTextView; // Declare TextView to show the user's name in the header

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        String name = getIntent().getStringExtra("Name");
        String email = getIntent().getStringExtra("Email");
        String profileImageURL = getIntent().getStringExtra("ProfileIMG");

        // Find the NavigationView
        navigationView = findViewById(R.id.navigation_view);

        // Find the headerNameTextView and headerEmailTextView in the NavigationView header
        View headerView = navigationView.getHeaderView(0);
        headerProfileImageView = headerView.findViewById(R.id.headerProfileImageView);
        headerNameTextView = headerView.findViewById(R.id.headerNameTextView);
        headerEmailTextView = headerView.findViewById(R.id.headerEmailTextView);

        // Set the profile image using Glide
        Glide.with(this)
                .load(profileImageURL)
                .placeholder(R.drawable.ic_profile_placeholder) // Placeholder image while loading
                .error(R.drawable.ic_profile_error) // Error image if loading fails
                .circleCrop()
                .into(headerProfileImageView);

        // Set the user's name and email to the respective TextViews in the header
        headerNameTextView.setText(name);
        headerEmailTextView.setText(email);

        // Initialize the drawer menu
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // Enable the drawer icon in the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set a click listener for the navigation items
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation item clicks
                if (item.getItemId() == R.id.menu_news) {
                    Intent intent = new Intent(AboutActivity.this, NewsActivity.class);
                    intent.putExtra("Name", name);
                    intent.putExtra("Email", email);
                    intent.putExtra("ProfileIMG", profileImageURL);
                    AboutActivity.this.startActivity(intent);
                } else if (item.getItemId() == R.id.menu_comment) {
                    Intent intent = new Intent(AboutActivity.this, CommentActivity.class);
                    intent.putExtra("Name", name);
                    intent.putExtra("Email", email);
                    intent.putExtra("ProfileIMG", profileImageURL);
                    AboutActivity.this.startActivity(intent);
                } else if (item.getItemId() == R.id.menu_about) {
                    Intent intent = new Intent(AboutActivity.this, AboutActivity.class);
                    intent.putExtra("Name", name);
                    intent.putExtra("Email", email);
                    intent.putExtra("ProfileIMG", profileImageURL);
                    AboutActivity.this.startActivity(intent);
                } else if (item.getItemId() == R.id.menu_sign_out) {
                    signOut();
                    Intent intent = new Intent(AboutActivity.this, MainActivity.class);
                    startActivity(intent);
                }


                // Close the drawer when an item is clicked
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Initialize Google Sign-In options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    public void openUrl(View view) {
        String url = "https://github.com/lilyraf/RDreport2"; // GitHub URL

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle the drawer menu toggle
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Clear user data and navigate back to MainActivity
                        Toast.makeText(getApplicationContext(), "Signed out successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AboutActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
    }

}

