package com.example.gloovitomanager.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gloovitomanager.R;
import com.example.gloovitomanager.activities.ListaProductosActivity;
import com.example.gloovitomanager.activities.modificar_borrar.FichaLineaActivity;
import com.example.gloovitomanager.modelo.Linea;
import com.example.gloovitomanager.modelo.Pedido;
import com.example.gloovitomanager.modelo.Usuario;

import java.util.List;

public class LineasAdapter extends RecyclerView.Adapter<LineasAdapter.ViewHolder> {

    private final List<Linea> listaLineas, listaStock, listaNoExiste;
    private final Context context;
    private Usuario usuario;
    private OnLineasClickListener listener;

    public LineasAdapter(List<Linea> listaLineas, Context context, List<Linea> listaStock, List<Linea> listaNoExiste, Usuario usuario, OnLineasClickListener listener) {
        this.listaLineas = listaLineas;
        this.context = context;
        this.listaStock = listaStock;
        this.listaNoExiste = listaNoExiste;
        this.usuario = usuario;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_linea, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        boolean existe = true;

        final Linea lineaActual = listaLineas.get(position);
        holder.lineaListener = lineaActual;

        holder.numeroLineaTV.setText(lineaActual.getNumlinea());
        holder.subtotalLineaTV.setText(String.valueOf(lineaActual.getSubtotal()));
        holder.nombreLocalLineaTV.setText(lineaActual.getLocal());
        holder.nombreProductoLineaTV.setText(lineaActual.getProducto());
        holder.cantidadLineaTV.setText(String.valueOf(lineaActual.getCantidad()));
        holder.precioLineaTV.setText(String.valueOf(lineaActual.getPrecio()));

        //Cambiamos el fondo de las líneas cuyo producto no figure en la BD
        for (Linea linea : listaNoExiste) {
            if (linea.getLocalid().equals(lineaActual.getLocalid())) {
                if (linea.getProductoid().equals(lineaActual.getProductoid())) {
                    existe = false;
                    holder.itemLY.setBackgroundResource(R.color.noExiste);
                }
            }
        }

        //Cambiamos el fondo de las líneas cuyo stock sea inferior al figura en la línea del pedido
        if (existe = true) {
            for (Linea linea : listaStock) {
                if (linea.getLocalid().equals(lineaActual.getLocalid())) {
                    if (linea.getProductoid().equals(lineaActual.getProductoid())) {
                        holder.itemLY.setBackgroundResource(R.color.stock);
                    }
                }
            }
        }

    }

    public interface OnLineasClickListener {
        void abrir(Linea l);
    }

    @Override
    public int getItemCount() {
        return listaLineas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView subtotalLineaTV, nombreLocalLineaTV, nombreProductoLineaTV, numeroLineaTV, cantidadLineaTV, precioLineaTV;

        public ConstraintLayout itemLY;

        public Linea lineaListener;

        public ViewHolder(View itemView) {
            super(itemView);

            itemLY = itemView.findViewById(R.id.itemLY);
            itemLY.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.abrir(lineaListener);
                }
            });

            subtotalLineaTV = itemView.findViewById(R.id.subtotalLineaTV);
            nombreLocalLineaTV = itemView.findViewById(R.id.nombreLocalLineaTV);
            nombreProductoLineaTV = itemView.findViewById(R.id.nombreProductoLineaTV);
            cantidadLineaTV = itemView.findViewById(R.id.cantidadLineaTV);
            precioLineaTV = itemView.findViewById(R.id.precioLineaTV);
            numeroLineaTV = itemView.findViewById(R.id.numeroLineaTV);

        }
    }
}