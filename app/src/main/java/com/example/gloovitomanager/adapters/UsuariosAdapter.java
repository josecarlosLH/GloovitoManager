package com.example.gloovitomanager.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gloovitomanager.R;
import com.example.gloovitomanager.activities.modificar_borrar.FichaUsuarioActivity;
import com.example.gloovitomanager.modelo.Usuario;

import java.util.List;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.ViewHolder> {
    private final Context context;
    private final List<Usuario> listaUsuarios;

    public UsuariosAdapter(Context context, List<Usuario> listaUsuarios) {
        this.context = context;
        this.listaUsuarios = listaUsuarios;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_usuario, parent, false);
        return new UsuariosAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Usuario usuario = listaUsuarios.get(position);
        holder.nombreClienteTV.setText(usuario.getNombre());
        holder.reservaTV.setText(String.valueOf(usuario.getReserva()));
        holder.carteraTV.setText(String.valueOf(usuario.getCartera()));
        holder.emailTV.setText(usuario.getMail());

        //Imagen
        Glide.with(context)
                .load(R.drawable.ic_user)
                .into(holder.usuarioIV);

        //Listener para cuando seleccionemos un usuario del RecyclerView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, FichaUsuarioActivity.class);
                i.putExtra("usuarioid", usuario.getId());
                context.startActivity(i);
            }
        });

        //Listener para abrir una app de email cuando seleccionemos el TV de email
        holder.emailTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, usuario.getMail());
                intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.managerdegloovito));
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreClienteTV,
                        reservaTV,
                        carteraTV,
                        emailTV;

        public ImageView usuarioIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nombreClienteTV = itemView.findViewById(R.id.nombreClienteItemTV);
            usuarioIV = itemView.findViewById(R.id.usuarioItemIV);
            carteraTV = itemView.findViewById(R.id.carteraTV);
            reservaTV = itemView.findViewById(R.id.reservaTV);
            emailTV = itemView.findViewById(R.id.emailTV);
        }
    }
}