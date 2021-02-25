package com.example.gloovitomanager.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gloovitomanager.R;
import com.example.gloovitomanager.activities.modificar_borrar.FichaPedidoActivity;
import com.example.gloovitomanager.modelo.Pedido;
import com.example.gloovitomanager.modelo.Usuario;

import java.util.List;

//ESTÁ BIEN
public class PedidosAdapter extends RecyclerView.Adapter<PedidosAdapter.ViewHolder> {
    private final Context context;
    private final List<Pedido> listaPedidos;
    private final List<Usuario> listaUsuarios;

    public PedidosAdapter(Context context, List<Pedido> listaPedidos, List<Usuario> listaUsuarios) {
        this.context = context;
        this.listaPedidos = listaPedidos;
        this.listaUsuarios = listaUsuarios;
    }

    @NonNull
    @Override
    public PedidosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pedido, parent, false);
        return new PedidosAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidosAdapter.ViewHolder holder, int position) {
        final Pedido pedido = listaPedidos.get(position);

        //Obtenemos al cliente que ha solicitado el pedido
        for (Usuario usuarioTmp : listaUsuarios) {
            if (usuarioTmp.getId().equals(pedido.getIdUsuario())) {
                holder.clientePedidoTV.setText(usuarioTmp.getNombre());
            }
        }

        holder.idPedidoTV.setText(pedido.getIdpedido());
        holder.fechaPedidoTV.setText(pedido.getFecha());
        holder.estadoPedidoTV.setText(pedido.getEstado());

        //Listener para cuando seleccionemos un pedido del RecyclerView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, FichaPedidoActivity.class);
                i.putExtra("pedidoid", pedido);

                Usuario usuario = new Usuario();
                for (Usuario usuarioTmp : listaUsuarios) {
                    if (usuarioTmp.getId().equals(pedido.getIdUsuario())) {
                         usuario = usuarioTmp;
                    }
                }

                i.putExtra("pedidouser", usuario);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView clientePedidoTV,
                        idPedidoTV,
                        fechaPedidoTV,
                        estadoPedidoTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            clientePedidoTV = itemView.findViewById(R.id.nombreClientePedidoItemTV);
            idPedidoTV = itemView.findViewById(R.id.idPedidoItemTV);
            fechaPedidoTV = itemView.findViewById(R.id.fechaPedidoTV);
            estadoPedidoTV = itemView.findViewById(R.id.estadoPedidoTV);
        }
    }
}