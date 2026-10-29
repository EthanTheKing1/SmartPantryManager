package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter
        extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    public interface OnIngredientActionListener {
        void onEdit(Ingredient ingredient);
        void onDelete(Ingredient ingredient);
    }

    private final List<Ingredient> ingredients = new ArrayList<>();
    private final OnIngredientActionListener listener;

    public IngredientAdapter(OnIngredientActionListener listener) {
        this.listener = listener;
    }

    public void setIngredients(List<Ingredient> newIngredients) {
        ingredients.clear();
        ingredients.addAll(newIngredients);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);

        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull IngredientViewHolder holder, int position) {

        Ingredient ingredient = ingredients.get(position);
        ButtonStyler.secondary(
                holder.itemView.findViewById(R.id.btnEditIngredient));

        ButtonStyler.danger(
                holder.itemView.findViewById(R.id.btnDeleteIngredient));

        holder.tvName.setText(ingredient.getName());

        String imageName = ingredient.getName()
                .trim()
                .toLowerCase(java.util.Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+|_+$", "");

        int imageResource = holder.itemView.getContext()
                .getResources()
                .getIdentifier(
                        imageName,
                        "drawable",
                        holder.itemView.getContext().getPackageName()
                );

        if (imageResource != 0) {
            holder.imgIngredient.setImageResource(imageResource);
        } else {
            holder.imgIngredient.setImageDrawable(null);
        }

        holder.tvQuantity.setText(
                "Quantity: " + ingredient.getQuantity()
                        + " " + ingredient.getUnit());

        String expiryDate = ingredient.getExpiryDate();

        if (expiryDate != null && !expiryDate.isEmpty()) {
            holder.tvExpiry.setText("Expires: " + expiryDate);
            holder.tvExpiry.setVisibility(View.VISIBLE);
        } else {
            holder.tvExpiry.setText("");
            holder.tvExpiry.setVisibility(View.GONE);
        }

        holder.btnEdit.setOnClickListener(
                v -> listener.onEdit(ingredient));

        holder.btnDelete.setOnClickListener(
                v -> listener.onDelete(ingredient));
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvQuantity;
        TextView tvExpiry;
        Button btnEdit;
        Button btnDelete;
        ImageView imgIngredient;

        IngredientViewHolder(@NonNull View itemView) {
            super(itemView);


            tvName = itemView.findViewById(R.id.tvIngredientName);
            tvQuantity = itemView.findViewById(R.id.tvIngredientQuantity);
            tvExpiry = itemView.findViewById(R.id.tvIngredientExpiry);
            btnEdit = itemView.findViewById(R.id.btnEditIngredient);
            btnDelete = itemView.findViewById(R.id.btnDeleteIngredient);
            imgIngredient = itemView.findViewById(R.id.imgIngredient);

        }
    }
}
