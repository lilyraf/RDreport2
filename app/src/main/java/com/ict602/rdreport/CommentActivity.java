package com.ict602.rdreport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {
    GoogleSignInClient mGoogleSignInClient;
    String name, email;
    EditText etName;
    EditText etEmail;
    EditText etComments;

    RequestQueue queue;
    final String URL = "http://169.254.2.69/comments/api.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        queue = Volley.newRequestQueue(getApplicationContext());


        name = getIntent().getStringExtra("Name");
        email = getIntent().getStringExtra("Email");
        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etComments = (EditText) findViewById(R.id.etComments);
        Button button = (Button) findViewById(R.id.btnSubmit);

        etName.setText(name);
        //tvName.setEnabled(false);
        etEmail.setText(email);
        //tvEmail.setEnabled(false);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //volley call

                makeRequest();

            }
        });
        Button signout = findViewById(R.id.signout);
        signout.setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }

    public void onClick(View v) {

        if (v.getId() == R.id.signout) {
            signOut();
        }
    }
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        Toast.makeText(getApplicationContext(),email + "signed out.",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
    public void makeRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();

            }
        }, errorListener){
            @Override
            protected Map<String,String> getParams(){
                Map <String,String> params = new HashMap<>();

                params.put("name",etName.getText().toString());
                params.put("email",etEmail.getText().toString());
                params.put("comments",etComments.getText().toString());

                return params;
            }
        };

        queue.add(stringRequest);
    }

    public Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };
}
