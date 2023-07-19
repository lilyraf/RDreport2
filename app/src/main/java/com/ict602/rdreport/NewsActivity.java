package com.ict602.rdreport;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {

    private List<NewsItem> newsList;
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;

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
}
