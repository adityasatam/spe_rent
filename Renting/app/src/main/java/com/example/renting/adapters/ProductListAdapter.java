package com.example.renting.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.renting.R;
import com.example.renting.ViewProductAcitvity;
import com.example.renting.models.ProductModel;

import java.util.List;

public class ProductListAdapter  extends RecyclerView.Adapter<ProductListAdapter.MyViewHolder> {

    private List<ProductModel> productModel;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_product_name, tv_product_description, tv_owner_location;
        public TextView tv_product_price;
        public ImageView iv_product_image;

        public MyViewHolder(View view) {
            super(view);
            tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
            tv_product_description = (TextView) view.findViewById(R.id.tv_product_description);
            tv_owner_location = (TextView) view.findViewById(R.id.tv_owner_location);
            tv_product_price = (TextView) view.findViewById(R.id.tv_product_price);
            iv_product_image = (ImageView) view.findViewById(R.id.iv_product_image);
        }
    }

    public ProductListAdapter(List<ProductModel> productModel, Context context) {
        this.productModel = productModel;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ProductModel ProductModel = productModel.get(position);
        holder.tv_product_name.setText(ProductModel.product_name);
        holder.tv_product_description.setText(ProductModel.product_description);
        holder.tv_owner_location.setText(ProductModel.owner_location);
        holder.tv_product_price.setText(ProductModel.product_rent_price);
        holder.iv_product_image.setImageBitmap(StringToBitMap(ProductModel.product_image));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Recycle Click" + position, Toast.LENGTH_SHORT).show();
//                Toast.makeText(context, ProductModel.product_name, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ViewProductAcitvity.class);
                intent.putExtra("Product_Details", ProductModel);
                context.startActivity(intent);
            }
        });
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_product_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return productModel.size();
    }
}
