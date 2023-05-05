package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.app.Retrofit.PreferenceHandler;

import org.mindrot.jbcrypt.BCrypt;

public class UpdateUserActivity extends AppCompatActivity {

    PreferenceHandler prefHandler;
    DatabaseHandler dbHandler;
    String salt = BCrypt.gensalt();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        prefHandler = new PreferenceHandler(this);
        dbHandler = new DatabaseHandler(this);

        Button updateButton = findViewById(R.id.update_button);

        updateButton.setOnClickListener(view -> {

            // Get the password and confirm password from the text fields
            String pw = ((EditText) findViewById(R.id.updatepw_in)).getText().toString();
            String pwConfirm = ((EditText) findViewById(R.id.updatepwConfirm_in)).getText().toString();

            // Check if the passwords match
            if (!checkPW(pw, pwConfirm))
                return;

            // Passwords match so update the user details
            updateUser(pw);

        });

    }

    private boolean checkPW(String pw, String pwConfirm) {
        if (pw.length() > 20 || pw.length() < 4) {
            Toast.makeText(this, "Password must be between 4 and 20 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!pw.equals(pwConfirm)) {
            Toast.makeText(this, "Passwords don't match!", Toast.LENGTH_SHORT).show();
            return false;
        }
        User user = dbHandler.getUser(prefHandler.getEmail());
        if (BCrypt.checkpw(pw, user.getHashedPW())) {
            Toast.makeText(this, "New password cannot be the same as the old password", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void updateUser(String pw) {
        User user = dbHandler.getUser(prefHandler.getEmail());

        // Encrypt the pw before updating it
        pw = BCrypt.hashpw(pw, salt);

        // Update the user's password
        user.setHashedPW(pw);
        dbHandler.updateUser(user);

        Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();

        // Sends you to the MovieList page
        Intent intent = new Intent(UpdateUserActivity.this, MovieListActivity.class);
        startActivity(intent);
    }
}