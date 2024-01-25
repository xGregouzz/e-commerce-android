package edu.esiea.ecommerce.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.esiea.ecommerce.R;
import edu.esiea.ecommerce.utils.platzi.UserApiRequest;
import edu.esiea.ecommerce.models.User;

public class SignUpActivity extends AppCompatActivity {
    private UserApiRequest userApiRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userApiRequest = new UserApiRequest(this);
        setContentView(R.layout.activity_signup);
        EditText signUpUsernameEditText = findViewById(R.id.signUpUsernameEditText);
        EditText signUpEmailEditText = findViewById(R.id.signUpEmailEditText);
        EditText signUpPasswordEditText = findViewById(R.id.signUpPasswordEditText);
        Button signUpLoginButton = findViewById(R.id.signUpLoginButton);
        signUpLoginButton.setOnClickListener(view -> {
            String username = signUpUsernameEditText.getText().toString();
            String email = signUpEmailEditText.getText().toString();
            String password = signUpPasswordEditText.getText().toString();

            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                showToast("Veuillez remplir tous les champs");
                return;
            }

            if (username.length() < 3) {
                showToast("Votre nom d'utilisateur doit faire au moins 3 characters");
                return;
            }

            if (password.length() < 5) {
                showToast("Votre nom mot de passe doit faire au moins 5 characters");
                return;
            }

            userApiRequest.createUser(username, email, password, "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2c/Default_pfp.svg/2048px-Default_pfp.svg.png", new UserApiRequest.UserApiCallback() {
                @Override
                public void onSuccess(User user) {
                    Log.i("SIGN_UP_ACTIVITY", "new user: " + user.toString());
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    showToast("Échec de la création de compte");
                    Log.i("SIGN_UP_ACTIVITY", errorMessage);
                }
            });
        });

        TextView signUpLoginTextView = findViewById(R.id.signUpLoginTextView);
        signUpLoginTextView.setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        ImageView signUpImageViewLogo = findViewById(R.id.signUpImageViewLogo);
        signUpImageViewLogo.setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

