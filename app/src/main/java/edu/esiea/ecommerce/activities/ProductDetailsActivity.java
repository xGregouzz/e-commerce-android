package edu.esiea.ecommerce.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import edu.esiea.ecommerce.R;
import edu.esiea.ecommerce.utils.platzi.ProductApiRequest;
import edu.esiea.ecommerce.models.Product;

public class ProductDetailsActivity extends AppCompatActivity {
    private ProductApiRequest productApiRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_details);
        productApiRequest = new ProductApiRequest(this);

        Intent intent = getIntent();
        TextView productTitleTextView = findViewById(R.id.productTitleTextView);
        TextView productDescriptionTextView = findViewById(R.id.productDescriptionTextView);
        TextView productPriceTextView = findViewById(R.id.productPriceTextView);
        ImageView productImageView = findViewById(R.id.productImageView);
        if (intent != null && intent.hasExtra("productTitle") && intent.hasExtra("productDescription") && intent.hasExtra("productImageUrl") && intent.hasExtra("productPrice")) {
            String productTitle = intent.getStringExtra("productTitle");
            String productPrice = intent.getStringExtra("productPrice");
            String productDescription = intent.getStringExtra("productDescription");
            String imageUrl = intent.getStringExtra("productImageUrl");
            Glide.with(this)
                    .load(imageUrl)
                    .into(productImageView);
            productTitleTextView.setText(productTitle);
            productDescriptionTextView.setText(productDescription);
            productPriceTextView.setText(productPrice + "$");
        } else {
            showToast("Erreur lors de la récupération des détails du produit");
            finish();
        }

        ImageView userDetailsButton = findViewById(R.id.homeUserDetailsButton);
        userDetailsButton.setOnClickListener(v -> {
            intent .setClass(ProductDetailsActivity.this, UserDetailsActivity.class);
            startActivity(intent);
        });

        ImageView homeViewProductsButton = findViewById(R.id.homeViewProductsButton);
        homeViewProductsButton.setOnClickListener(view -> {
            intent.setClass(ProductDetailsActivity.this, ProductsPageActivity.class);
            startActivity(intent);
        });

        ImageView homeHomebutton = findViewById(R.id.homeHomebutton);
        homeHomebutton.setOnClickListener(v -> {
            intent.setClass(ProductDetailsActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        LinearLayout modalSettings = findViewById(R.id.modalSettings);
        ImageView buttonSettings = findViewById(R.id.buttonSettings);
        buttonSettings.setOnClickListener(v -> {
            if (modalSettings.getVisibility() == View.VISIBLE) {
                modalSettings.setVisibility(View.GONE);
            } else {
                modalSettings.setVisibility(View.VISIBLE);
            }
        });

        View modifierButton = findViewById(R.id.modifierButton);
        modifierButton.setOnClickListener(view -> {
            showEditPopUp(intent, productTitleTextView, productDescriptionTextView, productPriceTextView);
        });

        View supprimerButton = findViewById(R.id.supprimerButton);
        supprimerButton.setOnClickListener(view -> {
            showDeletePopUp(intent);
        });
    }

    private void showDeletePopUp(Intent intent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Supprimer le produit");
        builder.setMessage("Êtes-vous sûr de vouloir supprimer ce produit?");
        builder.setPositiveButton("Oui", (dialog, which) -> {
            productApiRequest.deleteProduct(Integer.valueOf(intent.getStringExtra("productId")));
            Toast.makeText(ProductDetailsActivity.this, "Produit supprimé", Toast.LENGTH_SHORT).show();
            finish();
        });
        builder.setNegativeButton("Non", (dialog, which) -> {
        });

        builder.show();
    }

    private void showEditPopUp(Intent intent, TextView productTitleTextView, TextView productDescriptionTextView, TextView productPriceTextView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_edit_product, null);
        final EditText updatedEditText = dialogView.findViewById(R.id.updatedEditText);
        final EditText updatedDescriptionEditText = dialogView.findViewById(R.id.updatedDescriptionEditText);
        final EditText updatedPriceEditText = dialogView.findViewById(R.id.updatedPriceEditText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setTitle("Modifier le produit");
        builder.setPositiveButton("OK", (dialog, which) -> {
            String updatedName = updatedEditText.getText().toString();
            String updatedDescription = updatedDescriptionEditText.getText().toString();
            String updatedPrice = updatedPriceEditText.getText().toString();

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("title", updatedName);
                jsonObject.put("description", updatedDescription);
                jsonObject.put("price", Double.parseDouble(updatedPrice));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            putProduct(Integer.valueOf(intent.getStringExtra("productId")), jsonObject, new ProductApiRequest.ProductApiCallback() {
                @Override
                public void onSuccess(Product product) {
                    Log.i("test", "Hello");
                    productTitleTextView.setText(product.getTitle());
                    productDescriptionTextView.setText(product.getDescription());
                    productPriceTextView.setText(product.getPrice() + "$");
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e("test", errorMessage);
                }
            });
            Toast.makeText(ProductDetailsActivity.this, "Produit modifié", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> {
        });

        builder.show();
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void putProduct(Integer productId, JSONObject jsonObject, ProductApiRequest.ProductApiCallback callback) {
        productApiRequest.putProduct(productId, jsonObject, callback);
    }
}

