package com.example.gloovitomanager.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gloovitomanager.R;
import com.example.gloovitomanager.activities.ListaProductosActivity;
import com.example.gloovitomanager.activities.modificar_borrar.FichaLineaActivity;
import com.example.gloovitomanager.activities.modificar_borrar.FichaLocalActivity;
import com.example.gloovitomanager.modelo.Local;

import java.util.List;

public class LocalesAdapter extends RecyclerView.Adapter<LocalesAdapter.ViewHolder> {

    private final Context context;
    private final List<Local> listaLocales;

    public LocalesAdapter(Context context, List<Local> listaLocales) {
        this.context = context;
        this.listaLocales = listaLocales;
    }

    @NonNull
    @Override
    public LocalesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_local, parent, false);
        return new LocalesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LocalesAdapter.ViewHolder holder, int position) {
        final Local local = listaLocales.get(position);
        holder.nombreLocalTV.setText(local.getNombre());
        holder.direccionLocalTV.setText(local.getDireccion());

        //Si el local no tiene una imagen determinado, cargamos una por defecto
        if (local.getImagenURL().equals("default")) {
            Glide.with(context)
                    .load(R.drawable.ic_local)
                    .circleCrop()
                    .into(holder.localIV);
        } else {
            Glide.with(context)
                    .load(local.getImagenURL())
                    .circleCrop()
                    .into(holder.localIV);
        }

        //Listener para cuando seleccionemos un local del RecyclerView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ListaProductosActivity.class);
                i.putExtra("localid", local.getIdlocal());
                context.startActivity(i);
            }
        });

        //Listener para editar el local
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent i = new Intent(context, FichaLocalActivity.class);
                i.putExtra("localid", local.getIdlocal());
                context.startActivity(i);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaLocales.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreLocalTV, direccionLocalTV;
        public ImageView localIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nombreLocalTV = itemView.findViewById(R.id.nombreLocalItemTV);
            direccionLocalTV = itemView.findViewById(R.id.direccionTV);
            localIV = itemView.findViewById(R.id.localItemIV);
        }
    }
}