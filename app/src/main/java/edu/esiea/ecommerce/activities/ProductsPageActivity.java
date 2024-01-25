package edu.esiea.ecommerce.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import edu.esiea.ecommerce.R;
import edu.esiea.ecommerce.adapters.CategoriesAdapter;
import edu.esiea.ecommerce.utils.platzi.CategoryApiRequest;
import edu.esiea.ecommerce.utils.platzi.ProductApiRequest;
import edu.esiea.ecommerce.utils.ProductListUrlBuilder;
import edu.esiea.ecommerce.adapters.ProductAdapter;
import edu.esiea.ecommerce.utils.platzi.ProductsApiRequest;
import edu.esiea.ecommerce.models.Product;

public class ProductsPageActivity extends AppCompatActivity {
    private ProductListUrlBuilder url;
    private ProductsApiRequest productsApiRequest;
    private ProductApiRequest productApiRequest;
    private ProductAdapter productAdapter;
    private Boolean isLoading = false;
    public static Integer categoryId = null;
    private String search = null;
    private Double minPrice = null;
    private Double maxPrice = null;

    @Override
    protected void onResume() {
        super.onResume();
        productAdapter.clearProducts();
        getProducts();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_page);
        this.url = new ProductListUrlBuilder();
        productsApiRequest = new ProductsApiRequest(this);
        productApiRequest = new ProductApiRequest(this);

        ImageView buttonUserDetails = findViewById(R.id.homeUserDetailsButton);
        buttonUserDetails.setOnClickListener(v -> {
            Intent intent = new Intent(ProductsPageActivity.this, UserDetailsActivity.class);
            startActivity(intent);
        });

        ImageView buttonViewHomePage = findViewById(R.id.buttonHomePage);
        buttonViewHomePage.setOnClickListener(v -> {
            Intent intent = new Intent(ProductsPageActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        ConstraintLayout containerLayout = findViewById(R.id.container_search_view);
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                containerLayout.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                containerLayout.setVisibility(View.VISIBLE);
                search = newText;
                getProducts();
                return false;
            }
        });

        Button btnFilter = findViewById(R.id.buttonFilter);
        btnFilter.setOnClickListener(view -> showFilterPopup());

        searchView.setOnQueryTextFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                containerLayout.setVisibility(View.VISIBLE);
            } else {
                containerLayout.setVisibility(View.GONE);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        ImageButton buttonAddProduct = findViewById(R.id.buttonAddProduct);
        buttonAddProduct.setOnClickListener(view -> showAddProductPopup());

        productAdapter = new ProductAdapter(this);

        RecyclerView recyclerView = findViewById(R.id.productsRecyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        isLoading = true;
                        updateProducts();
                    }
                }
            }
        });
        getProducts();
        recyclerView.setAdapter(productAdapter);
    }

    public void updateProducts() {
        productsApiRequest.getProductsList(url.title(search).offset(productAdapter.getItemCount()).limit(2).priceMin(minPrice).priceMax(maxPrice).categoryId(categoryId), new ProductsApiRequest.ProductsApiCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                productAdapter.addProducts(products);
                isLoading = false;
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("ProductsPageActivity", errorMessage);
            }
        });
    }

    public void getProducts() {
        productsApiRequest.getProductsList(this.url.title(search).offset(0).limit(2).priceMin(minPrice).priceMax(maxPrice).categoryId(categoryId), new ProductsApiRequest.ProductsApiCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                productAdapter.setProducts(products);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("ProductsPageActivity", errorMessage);
            }
        });
    }

    private void showAddProductPopup() {
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_add_product, null);

        final EditText editTextProductName = popupView.findViewById(R.id.editTextProductName);
        final EditText editTextProductDescription = popupView.findViewById(R.id.editTextProductDescription);
        final EditText editTextProductCategory = popupView.findViewById(R.id.editTextProductCategory);
        final EditText editTextProductImageUrl = popupView.findViewById(R.id.editTextProductImageUrl);
        final EditText editTextProductPrice = popupView.findViewById(R.id.editTextProductPrice);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView)
                .setTitle("Ajouter un produit")
                .setPositiveButton("Ajouter", (dialogInterface, i) -> {
                    String productName = editTextProductName.getText().toString();
                    String productDescription = editTextProductDescription.getText().toString();
                    int productCategory = Integer.parseInt(editTextProductCategory.getText().toString());
                    String productImageUrl = editTextProductImageUrl.getText().toString();
                    double productPrice = Double.parseDouble(editTextProductPrice.getText().toString());

                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonArray.put(0, productImageUrl);
                        jsonObject.put("title", productName);
                        jsonObject.put("price", productPrice);
                        jsonObject.put("description", productDescription);
                        jsonObject.put("categoryId", productCategory);
                        jsonObject.put("images", jsonArray);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    productApiRequest.addProduct(jsonObject);
                    Log.i("ajout", "hello");
                })
                .setNegativeButton("Annuler", null)
                .show();
    }


    private void showFilterPopup() {
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_filter, null);

        final CategoryApiRequest categoryApiRequest = new CategoryApiRequest(this);
        final CategoriesAdapter categoriesAdapter = new CategoriesAdapter(this, popupView);

        final EditText minPriceFilter = popupView.findViewById(R.id.minPriceFilter);
        final EditText maxPriceFilter = popupView.findViewById(R.id.maxPriceFilter);

        RecyclerView recyclerView = popupView.findViewById(R.id.filterRecyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(categoriesAdapter);

        categoryApiRequest.getAllCategories(new CategoryApiRequest.CategoryCallback() {
            @Override
            public void onSuccess(List<Product.Category> categoryList) {
                categoriesAdapter.clearCategories();
                categoriesAdapter.addCategories(categoryList);
            }

            @Override
            public void onError(String errorMessage) {
                showToast("Error: cannot fetch categories");
                Log.e("PRODUCTS_PAGE", errorMessage);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView)
                .setTitle("Filtre")
                .setPositiveButton("Filtrer", (dialogInterface, i) -> {
                    if (minPriceFilter.getText() != null && !minPriceFilter.getText().toString().isEmpty()) {
                        minPrice = Double.parseDouble(minPriceFilter.getText().toString());
                    }

                    if (maxPriceFilter.getText() != null && !maxPriceFilter.getText().toString().isEmpty()) {
                        maxPrice = Double.parseDouble(maxPriceFilter.getText().toString());
                    }
                    productAdapter.clearProducts();
                    getProducts();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}


