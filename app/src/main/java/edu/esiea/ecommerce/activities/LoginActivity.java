package edu.esiea.ecommerce.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import edu.esiea.ecommerce.R;
import edu.esiea.ecommerce.utils.platzi.UserApiRequest;

public class LoginActivity extends AppCompatActivity {
    private UserApiRequest userApiRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userApiRequest = new UserApiRequest(this);

        EditText loginUsernameEditText = findViewById(R.id.loginUsernameEditText);
        EditText loginPasswordEditText = findViewById(R.id.loginPasswordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(view -> {
            String username = loginUsernameEditText.getText().toString();
            String password = loginPasswordEditText.getText().toString();

            if (!username.isEmpty() && !password.isEmpty()) {
                userApiRequest.loginUser(username, password, new UserApiRequest.AuthCallback() {
                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        showToast("Nom d'utilisateur ou mot de passe incorrect");
                    }
                });
            } else {
                showToast("Veuillez remplir tous les champs");
            }
        });

        TextView loginSignUpTextView = findViewById(R.id.loginSignUpTextView);
        ImageView loginImageViewLogo = findViewById(R.id.loginImageViewLogo);
        loginSignUpTextView.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        loginImageViewLogo.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
