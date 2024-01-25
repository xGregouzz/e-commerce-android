package edu.esiea.ecommerce.utils.platzi;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.List;

import edu.esiea.ecommerce.models.Product;
import edu.esiea.ecommerce.utils.ProductListUrlBuilder;

public class ProductsApiRequest {

    private static final String TAG = "ProductsAPI";
    private final RequestQueue requestQueue;

    public ProductsApiRequest(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public interface ProductsApiCallback {
        void onSuccess(List<Product> product);

        void onError(String errorMessage);
    }

    public void getProductsList(ProductListUrlBuilder urlBuilder, ProductsApiCallback callback) {
        Log.i(TAG, "Calling with URL: " +  urlBuilder.getUrl());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                urlBuilder.build(),
                null,
                response -> {
                    Log.i(TAG, "response: " + response.toString());
                    List<Product> products = parseJson(response);
                    callback.onSuccess(products);
                },
                error -> {
                    if (error.getMessage() != null) {
                        Log.e(TAG, error.getMessage());
                    }

                    callback.onError("Error in request");
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    private List<Product> parseJson(JSONArray response) {
         return new Gson().fromJson(response.toString(), new TypeToken<List<Product>>() {}.getType());
    }
}

