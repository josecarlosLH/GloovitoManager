package com.example.gloovitomanager.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gloovitomanager.R;
import com.example.gloovitomanager.activities.modificar_borrar.FichaProductoActivity;
import com.example.gloovitomanager.modelo.Producto;

import java.util.List;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ViewHolder> {
    private final Context context;
    private final List<Producto> listaProductos;
    private final String localid;

    public ProductosAdapter(Context context, List<Producto> listaProductos, String localid) {
        this.context = context;
        this.listaProductos = listaProductos;
        this.localid = localid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
        return new ProductosAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Producto producto = listaProductos.get(position);
        holder.nombreProductoTV.setText(producto.getNombre());
        holder.stockTV.setText(String.valueOf(producto.getStock()));
        holder.precioTV.setText(String.valueOf(producto.getPrecio()));

        //Si el local no tiene una imagen determinado, cargamos una por defecto
        if (producto.getImagenURL().equals("default")) {
            Glide.with(context)
                    .load(R.drawable.ic_product)
                    .circleCrop()
                    .into(holder.productoItemIV);
        } else {
            Glide.with(context)
                    .load(producto.getImagenURL())
                    .circleCrop()
                    .into(holder.productoItemIV);
        }

        //Listener para cuando seleccionemos un producto del RecyclerView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, FichaProductoActivity.class);
                i.putExtra("productoid", producto);
                i.putExtra("localprodid", localid);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreProductoTV,
                        precioTV,
                        stockTV;

        public ImageView productoItemIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nombreProductoTV = itemView.findViewById(R.id.nombreProductoItemTV);
            precioTV = itemView.findViewById(R.id.precioProductoTV);
            stockTV = itemView.findViewById(R.id.stockProductoTV);
            productoItemIV = itemView.findViewById(R.id.productoItemIV);
        }
    }
}