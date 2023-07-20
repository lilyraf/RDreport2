package com.ict602.rdreport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile() // Add this line to request profile information, including the profile image URL
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if(account == null){
            //null sign in
            Toast.makeText(this,"Please sign in", Toast.LENGTH_SHORT).show();

        } else {
            // user signed in already
            Toast.makeText(this,"User already signed in", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, CommentActivity.class);
            intent.putExtra("Name", account.getDisplayName());
            intent.putExtra("Email",account.getEmail());
            String profileImageURL = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "";
            intent.putExtra("ProfileIMG", profileImageURL);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_button) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 10);
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 10) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //updateUI(null);
            // Signed in successfully, show authenticated UI.
            // Open new Activity
            Toast.makeText(this,"User already signed in",Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, CommentActivity.class);
            intent.putExtra("Name", account.getDisplayName());
            intent.putExtra("Email",account.getEmail());
            String profileImageURL = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "";
            intent.putExtra("ProfileIMG", profileImageURL);
            startActivity(intent);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Bakpo Nih", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this,"tak jadi ni sign in", Toast.LENGTH_SHORT).show();
            //updateUI(null);
        }
    }

}