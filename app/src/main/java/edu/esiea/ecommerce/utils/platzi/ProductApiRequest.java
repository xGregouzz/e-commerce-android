package edu.esiea.ecommerce.utils.platzi;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import edu.esiea.ecommerce.models.Product;

public class ProductApiRequest {

    private static final String TAG = "ProductsAPI";
    private final RequestQueue requestQueue;

    public ProductApiRequest(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public interface ProductApiCallback {
        void onSuccess(Product product);
        void onError(String errorMessage);
    }

    public void addProduct(JSONObject jsonObject) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                "https://api.escuelajs.co/api/v1/products/",
                jsonObject,
                response -> {
                    Log.i(TAG, "Produit Ajouté");
                },
                error -> {
                    if (error.getMessage() != null) {
                        Log.e(TAG, error.getMessage());
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    public void putProduct(Integer productId, JSONObject jsonObject, ProductApiCallback callback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                "https://api.escuelajs.co/api/v1/products/" + productId,
                jsonObject,
                response -> {
                    Log.i(TAG, "Produit Modifié");
                    Product product = parseJson(jsonObject);
                    callback.onSuccess(product);
                },
                error -> {
                    if (error.getMessage() != null) {
                        Log.e(TAG, error.getMessage());
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    public void deleteProduct(Integer productId) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                "https://api.escuelajs.co/api/v1/products/" + productId,
                null,
                response -> {
                    Log.i(TAG, "Produit Supprimé");
                },
                error -> {
                    if (error.getMessage() != null) {
                        Log.e(TAG, error.getMessage());
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private Product parseJson(JSONObject response) {
        return new Gson().fromJson(response.toString(), Product.class);
    }
}

