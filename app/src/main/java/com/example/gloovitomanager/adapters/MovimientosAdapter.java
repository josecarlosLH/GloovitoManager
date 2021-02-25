package com.example.gloovitomanager.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gloovitomanager.MainActivity;
import com.example.gloovitomanager.R;
import com.example.gloovitomanager.modelo.Movimiento;
import com.example.gloovitomanager.modelo.Usuario;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MovimientosAdapter extends RecyclerView.Adapter<MovimientosAdapter.ViewHolder> {
    private final Context context;
    private final List<Movimiento> listaMovimientos;
    private final List<Usuario> listaUsuarios;

    private final OnProductosClickListener listener;

    public MovimientosAdapter(Context context, List<Movimiento> listaMovimientos, List<Usuario> listaUsuarios, OnProductosClickListener listener) {
        this.context = context;
        this.listaMovimientos = listaMovimientos;
        this.listaUsuarios = listaUsuarios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovimientosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movimiento, parent, false);
        return new MovimientosAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MovimientosAdapter.ViewHolder holder, int position) {
        holder.idMovimientoItemTV.setText(listaMovimientos.get(position).getMovimientoId());
        holder.dineroTV.setText(String.valueOf(listaMovimientos.get(position).getDinero()));
        holder.estadoTV.setText(listaMovimientos.get(position).getEstado());

        //Dependiendo del estado del movimiento, deshabilitamos ciertos botones
        if (listaMovimientos.get(position).getEstado().equals("Completado")) {
            holder.cancelarBT.setEnabled(false);
            holder.aceptarBT.setEnabled(false);
        } else if (listaMovimientos.get(position).getEstado().equals("Cancelado")) {
            holder.cancelarBT.setEnabled(false);
        }

        //Obtener nombre usuario
        for (Usuario usuarioTmp : listaUsuarios) {
            if (usuarioTmp.getId().equals(listaMovimientos.get(position).getClienteId())) {
                holder.nombreClienteMovimientoItemTV.setText(usuarioTmp.getNombre());
            }
        }

        holder.movimiento = listaMovimientos.get(position);
    }

    public interface OnProductosClickListener {
        void completar(Movimiento movimiento);
        void cancelar(Movimiento movimiento);
    }

    @Override
    public int getItemCount() {
        return listaMovimientos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreClienteMovimientoItemTV,
                        idMovimientoItemTV,
                        dineroTV,
                        estadoTV;

        public Button aceptarBT, cancelarBT;

        public Movimiento movimiento;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nombreClienteMovimientoItemTV = itemView.findViewById(R.id.nombreClienteMovimientoItemTV);
            idMovimientoItemTV = itemView.findViewById(R.id.idMovimientoItemTV);
            dineroTV = itemView.findViewById(R.id.dineroTV);
            estadoTV = itemView.findViewById(R.id.estadoItemTV);
            aceptarBT = itemView.findViewById(R.id.aceptarBT);
            aceptarBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.completar(movimiento);
                }
            });
            cancelarBT = itemView.findViewById(R.id.cancelarBT);
            cancelarBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.cancelar(movimiento);
                }
            });
        }
    }
}