package edu.esiea.ecommerce.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import edu.esiea.ecommerce.R;
import edu.esiea.ecommerce.activities.ProductDetailsActivity;
import edu.esiea.ecommerce.models.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductsViewHolder> {

    private final Context context;
    private List<Product> products;

    public ProductAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_detail, parent, false);
        return new ProductsViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductsViewHolder productsViewHolder, int position) {
        Product product = products.get(position);

        productsViewHolder.productTitleTextView.setText(product.getTitle());
        productsViewHolder.productDescriptionTextView.setText(product.getDescription());
        productsViewHolder.productPriceTextView.setText("$" + product.getPrice());

        Glide.with(context)
                .load(product.getImages().get(0))
                .into(productsViewHolder.productImageView);

        productsViewHolder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra("productTitle", product.getTitle());
            intent.putExtra("productDescription", product.getDescription());
            intent.putExtra("productImageUrl", product.getImages().get(0));
            intent.putExtra("productPrice", String.valueOf(product.getPrice()));
            intent.putExtra("productId", String.valueOf((product.getId())));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return products == null ? 0 : products.size();
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    public void addProducts(List<Product> products) {
        this.products.addAll(products);
        notifyDataSetChanged();
    }

    public void clearProducts() {
        if (products == null) return;
        this.products.clear();
        notifyDataSetChanged();
    }

    static class ProductsViewHolder extends RecyclerView.ViewHolder {
        TextView productTitleTextView;
        TextView productDescriptionTextView;
        TextView productPriceTextView;
        ImageView productImageView;


        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            productTitleTextView = itemView.findViewById(R.id.titleTextView);
            productDescriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            productPriceTextView = itemView.findViewById(R.id.priceTextView);
            productImageView = itemView.findViewById(R.id.productImageView);
        }
    }
}
