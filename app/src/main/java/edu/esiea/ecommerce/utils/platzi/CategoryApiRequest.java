package edu.esiea.ecommerce.utils.platzi;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.List;

import edu.esiea.ecommerce.models.Product;

public class CategoryApiRequest {

    private static final String BASE_URL = "https://api.escuelajs.co/api/v1/categories";
    private static final String TAG = "CategoryAPI";

    private final RequestQueue requestQueue;
    private final Context context;

    public CategoryApiRequest(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        this.context = context;
    }

    public interface CategoryCallback {
        void onSuccess(List<Product.Category> categoryList);
        void onError(String errorMessage);
    }

    public void getAllCategories(final CategoryCallback callback) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                BASE_URL,
                null,
                response -> {
                    List<Product.Category> categoryList = parseCategoryList(response);
                    callback.onSuccess(categoryList);
                },
                error -> callback.onError(error.getMessage())
        );

        Volley.newRequestQueue(context).add(jsonArrayRequest);
    }

    private List<Product.Category> parseCategoryList(JSONArray response) {
        return new Gson().fromJson(response.toString(), new TypeToken<List<Product.Category>>() {}.getType());
    }
}
