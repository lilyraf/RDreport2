package com.ict602.rdreport;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {

    private List<NewsItem> newsList;
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    GoogleSignInClient mGoogleSignInClient;
    private DrawerLayout drawerLayout;

    private NavigationView navigationView;
    private ImageView headerProfileImageView;
    private TextView headerNameTextView; // Declare TextView to show the user's name in the header
    private TextView headerEmailTextView; // Declare TextView to show the user's name in the header

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Initialize the list to hold news items
        newsList = new ArrayList<>();

        // Initialize the RecyclerView and set its layout manager
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create the adapter and set it to the RecyclerView
        newsAdapter = new NewsAdapter(newsList);
        recyclerView.setAdapter(newsAdapter);

        // Call getNewsData() to fetch and display news
        getNewsData();

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
                    Intent intent = new Intent(NewsActivity.this, NewsActivity.class);
                    intent.putExtra("Name", name);
                    intent.putExtra("Email", email);
                    intent.putExtra("ProfileIMG", profileImageURL);
                    NewsActivity.this.startActivity(intent);
                } else if (item.getItemId() == R.id.menu_comment) {
                    Intent intent = new Intent(NewsActivity.this, CommentActivity.class);
                    intent.putExtra("Name", name);
                    intent.putExtra("Email", email);
                    intent.putExtra("ProfileIMG", profileImageURL);
                    NewsActivity.this.startActivity(intent);
                } else if (item.getItemId() == R.id.menu_about) {
                    Intent intent = new Intent(NewsActivity.this, AboutActivity.class);
                    intent.putExtra("Name", name);
                    intent.putExtra("Email", email);
                    intent.putExtra("ProfileIMG", profileImageURL);
                    NewsActivity.this.startActivity(intent);
                } else if (item.getItemId() == R.id.menu_sign_out) {
                    signOut();
                    Intent intent = new Intent(NewsActivity.this, MainActivity.class);
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

    private void getNewsData() {
        String newsURL = "http://169.254.2.69/comments/get_news.php";

        // Create a new RequestQueue using Volley
        RequestQueue queue = Volley.newRequestQueue(this);

        // Send a GET request to the server to fetch news data
        JsonArrayRequest newsRequest = new JsonArrayRequest(Request.Method.GET, newsURL, null,
                new Response.Listener<JSONArray>() {
                    @Override

                    public void onResponse(JSONArray response) {
                        try {
                            // Clear the newsList to avoid duplicates in case of multiple API calls
                            newsList.clear();

                            // Process each news item
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject newsObject = response.getJSONObject(i);

                                int id = newsObject.getInt("id");
                                String title = newsObject.getString("title");
                                String description = newsObject.getString("description");
                                String imageName = newsObject.getString("image_name");
                                String author = newsObject.getString("author");
                                String publishDate = newsObject.getString("publish_date");

                                // Create a NewsItem object and add it to the list
                                NewsItem newsItem = new NewsItem(id, title, description, imageName, author, publishDate);
                                newsList.add(newsItem);
                            }

                            // Notify the adapter that the data has changed
                            newsAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(NewsActivity.this, "Error parsing news data.", Toast.LENGTH_SHORT).show();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors that occurred during the request
                        Toast.makeText(NewsActivity.this, "Error fetching news: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });

        // Add the request to the RequestQueue to initiate the network request
        queue.add(newsRequest);
    }
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Clear user data and navigate back to MainActivity
                        Toast.makeText(getApplicationContext(), "Signed out successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(NewsActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
    }

}
