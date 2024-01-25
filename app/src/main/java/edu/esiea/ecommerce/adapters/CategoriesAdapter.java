package edu.esiea.ecommerce.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.esiea.ecommerce.R;
import edu.esiea.ecommerce.activities.ProductsPageActivity;
import edu.esiea.ecommerce.models.Product;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder> {

    private final Context context;
    private final View popupView;
    private List<Product.Category> categories;

    public CategoriesAdapter(Context context, View popupView) {
        this.context = context;
        this.popupView = popupView;
        this.categories = new ArrayList<>();
    }

    @NonNull
    @Override
    public CategoriesAdapter.CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_button, parent, false);
        return new CategoriesAdapter.CategoriesViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.CategoriesViewHolder holder, int position) {
        Product.Category category = categories.get(position);

        holder.button.setText(category.getName());

        holder.button.setOnClickListener(view -> {
            TextView textView = popupView.findViewById(R.id.selectedCategory);
            textView.setText("Selected Category: " + category.getName());
            ProductsPageActivity.categoryId = category.getId();
        });
    }

    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
    }

    public void setCategories(List<Product.Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    public void addCategories(List<Product.Category> categories) {
        this.categories.addAll(categories);
        notifyDataSetChanged();
    }

    public void clearCategories() {
        if (categories == null) return;
        this.categories.clear();
        notifyDataSetChanged();
    }

    static class CategoriesViewHolder extends RecyclerView.ViewHolder {
        Button button;


        public CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.categoryButton);
        }
    }
}
