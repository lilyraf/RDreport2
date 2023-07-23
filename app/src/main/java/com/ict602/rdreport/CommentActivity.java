package com.ict602.rdreport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {

    GoogleSignInClient mGoogleSignInClient;
    String name, email, profileImageURL;
    EditText etName, etEmail, etComments;

    RequestQueue queue;
    final String URL = "http://169.254.2.69/comments/api.php";

    // Navigation Drawer
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private ImageView headerProfileImageView;
    private TextView headerNameTextView; // Declare TextView to show the user's name in the header
    private TextView headerEmailTextView; // Declare TextView to show the user's name in the header

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        queue = Volley.newRequestQueue(getApplicationContext());

        name = getIntent().getStringExtra("Name");
        email = getIntent().getStringExtra("Email");
        profileImageURL = getIntent().getStringExtra("ProfileIMG");

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etComments = findViewById(R.id.etComments);
        Button button = findViewById(R.id.btnSubmit);
        etName.setText(name);
        etName.setEnabled(false);
        etEmail.setText(email);
        etEmail.setEnabled(false);

        button.setOnClickListener(this);


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

        initDrawerMenu(); // Initialize the drawer menu
        initGoogleSignIn(); // Initialize Google Sign-In options
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        return true;
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

    // Implement the onClick method from View.OnClickListener interface
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSubmit) {
            makeRequest();
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), email + " signed out.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void makeRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
            }
        }, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", etName.getText().toString());
                params.put("email", etEmail.getText().toString());
                params.put("comments", etComments.getText().toString());
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Method to initialize the Navigation Drawer
    private void initDrawerMenu() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // Set up the ActionBarDrawerToggle to open/close the drawer
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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
                    Intent intent = new Intent(CommentActivity.this, NewsActivity.class);
                    intent.putExtra("Name", name);
                    intent.putExtra("Email", email);
                    intent.putExtra("ProfileIMG", profileImageURL);
                    CommentActivity.this.startActivity(intent);
             } else if (item.getItemId() == R.id.menu_comment) {
                    Intent intent = new Intent(CommentActivity.this, CommentActivity.class);
                    intent.putExtra("Name", name);
                    intent.putExtra("Email", email);
                    intent.putExtra("ProfileIMG", profileImageURL);
                    CommentActivity.this.startActivity(intent);

                } else if (item.getItemId() == R.id.menu_about) {
                    Intent intent = new Intent(CommentActivity.this, AboutActivity.class);
                    intent.putExtra("Name", name);
                    intent.putExtra("Email", email);
                    intent.putExtra("ProfileIMG", profileImageURL);
                    CommentActivity.this.startActivity(intent);
                } else if (item.getItemId() == R.id.menu_sign_out) {
                    signOut();
                    Intent intent = new Intent(CommentActivity.this, MainActivity.class);
                    startActivity(intent);
                }


                // Close the drawer when an item is clicked
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }

        });
    }

    // Method to initialize Google Sign-In options
    private void initGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
}
