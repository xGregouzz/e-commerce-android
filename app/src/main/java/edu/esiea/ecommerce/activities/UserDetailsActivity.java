package edu.esiea.ecommerce.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import org.json.JSONException;
import org.json.JSONObject;

import edu.esiea.ecommerce.R;
import edu.esiea.ecommerce.utils.platzi.UserApiRequest;
import edu.esiea.ecommerce.models.User;

public class UserDetailsActivity extends AppCompatActivity {
    private ImageView buttonViewProducts;
    private ImageView buttonViewHomePage;
    private UserApiRequest userApiRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        TextView userName = findViewById(R.id.userName);
        ImageView userPicture = findViewById(R.id.userPicture);

        RequestManager with = Glide.with(this);
        userApiRequest = new UserApiRequest(this);
        userApiRequest.getUserInfo(new UserApiRequest.UserInfoCallback() {
            @Override
            public void onSuccess(int id, String email, String name, String role, String avatar) {
                Log.i("TE", id + " " + email + " " + name + " ");
                intent.putExtra("userId", String.valueOf(id));
                String goodName = Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
                userName.setText(goodName);
                with.load(avatar).into(userPicture);
            }

            @Override
            public void onError(String errorMessage) {
                Log.i("TE", errorMessage);
            }
        });

        buttonViewProducts = findViewById(R.id.homeViewProductsButton);
        buttonViewProducts.setOnClickListener(view -> {
            intent.setClass(UserDetailsActivity.this, ProductsPageActivity.class);
            startActivity(intent);
        });

        buttonViewHomePage = findViewById(R.id.buttonHomePage);
        buttonViewHomePage.setOnClickListener(view -> {
            intent.setClass(UserDetailsActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        Button modifierButton = findViewById(R.id.buttonUpdateProfil);
        // Ajouter des écouteurs onClick aux boutons
        modifierButton.setOnClickListener(view -> {
            // Afficher la boîte de dialogue pour la modification des paramètres
            showEditUserDialog(intent, userName);
        });
    }

    private void showEditUserDialog(Intent intent, TextView userName) {
        // Créer un LayoutInflater pour inflater la vue personnalisée
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_edit_user, null);

        // Trouver les champs de texte dans la vue personnalisée
        final EditText updatedEmail = dialogView.findViewById(R.id.newEmailEditText);
        final EditText updatedName = dialogView.findViewById(R.id.newNameUserEditText);

        // Créer la boîte de dialogue avec la vue personnalisée
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setTitle("Modifier vos informations personnelles");

        // Ajouter des boutons à la boîte de dialogue (OK et Annuler)
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Récupérer les valeurs saisies par l'utilisateur
            String newEmail = updatedEmail.getText().toString();
            String newName = updatedName.getText().toString();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", newEmail);
                jsonObject.put("name", newName);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            putUser(Integer.valueOf(intent.getStringExtra("userId")), jsonObject, new UserApiRequest.UserApiCallback() {
                @Override
                public void onSuccess(User user) {
                    userName.setText(user.getName());
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e("test", errorMessage);
                }
            });

            // Afficher un message ou effectuer d'autres actions si nécessaire
            Toast.makeText(UserDetailsActivity.this, "Utilisateur modifié", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Annuler", (dialog, which) -> {
            // L'utilisateur a annulé, rien à faire ici
        });

        // Afficher la boîte de dialogue
        builder.show();
    }

    public void putUser(Integer userId, JSONObject jsonObject, UserApiRequest.UserApiCallback callback) {
        userApiRequest.putUser(userId, jsonObject, callback);
    }
}