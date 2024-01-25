package edu.esiea.ecommerce.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.esiea.ecommerce.R;
import edu.esiea.ecommerce.adapters.ProductAdapter;
import edu.esiea.ecommerce.models.Product;
import edu.esiea.ecommerce.utils.ProductListUrlBuilder;
import edu.esiea.ecommerce.utils.platzi.ProductsApiRequest;
import edu.esiea.ecommerce.utils.platzi.UserApiRequest;

public class HomeActivity extends AppCompatActivity {
    private ProductListUrlBuilder productListUrlBuilder;
    private ProductsApiRequest productsApiRequest;
    private ProductAdapter productAdapter;
    private Boolean isLoading = false;
    private String search = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.productListUrlBuilder = new ProductListUrlBuilder();
        setContentView(R.layout.activity_home);
        productsApiRequest = new ProductsApiRequest(this);

        Intent intent = new Intent();
        ImageView userDetailsButton = findViewById(R.id.homeUserDetailsButton);
        userDetailsButton.setOnClickListener(v -> {
            intent.setClass(HomeActivity.this, UserDetailsActivity.class);
            startActivity(intent);
        });

        ConstraintLayout containerLayout = findViewById(R.id.homeContainerSearchView);
        LinearLayout containerSearchBar = findViewById(R.id.homeLinearLayoutSearchBar);
        SearchView searchView = findViewById(R.id.homeSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                containerLayout.setVisibility(View.GONE);
                containerSearchBar.setTranslationY(0);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                containerLayout.setVisibility(View.VISIBLE);
                containerSearchBar.setTranslationY(-50);
                search = newText;
                getProducts();
                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                containerLayout.setVisibility(View.VISIBLE);
                containerSearchBar.setTranslationY(-50);
            } else {
                containerLayout.setVisibility(View.GONE);
                containerSearchBar.setTranslationY(0);
            }
        });

        productAdapter = new ProductAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.homeProductsRecyclerView);
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

        ImageView viewProductsButton = findViewById(R.id.homeViewProductsButton);
        viewProductsButton.setOnClickListener(view -> {
            intent.setClass(HomeActivity.this, ProductsPageActivity.class);
            startActivity(intent);
        });
    }

    public void updateProducts() {
        productsApiRequest.getProductsList(productListUrlBuilder.title(search).offset(productAdapter.getItemCount()).limit(2), new ProductsApiRequest.ProductsApiCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                productAdapter.addProducts(products);
                isLoading = false;
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("MainActivity", errorMessage);
            }
        });
    }

    public void getProducts() {
        productsApiRequest.getProductsList(this.productListUrlBuilder.title(search).offset(0).limit(2), new ProductsApiRequest.ProductsApiCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                productAdapter.setProducts(products);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("MainActivity", errorMessage);
            }
        });
    }
}
