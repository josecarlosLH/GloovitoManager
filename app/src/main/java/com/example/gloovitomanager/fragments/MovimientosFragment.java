package com.example.gloovitomanager.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gloovitomanager.MainActivity;
import com.example.gloovitomanager.R;
import com.example.gloovitomanager.adapters.MovimientosAdapter;
import com.example.gloovitomanager.modelo.Movimiento;
import com.example.gloovitomanager.modelo.Pedido;
import com.example.gloovitomanager.modelo.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MovimientosFragment extends Fragment implements MovimientosAdapter.OnProductosClickListener {

    private RecyclerView recyclerView;
    private MovimientosAdapter movimientosAdapter;
    private List<Movimiento> listaMovimientos;

    private DatabaseReference reference;
    private ValueEventListener obtenerMovimientosListener;

    public MovimientosFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movimientos, container, false);

        //RecyclerView
        recyclerView = view.findViewById(R.id.movimientosRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Obtener movimientos
        listaMovimientos = new ArrayList<>();
        obtenerMovimientos();
        return view;
    }

    private void obtenerMovimientos() {
        reference = FirebaseDatabase.getInstance().getReference("movimientos");

        obtenerMovimientosListener = reference.addValueEventListener(new ValueEventListener() {

            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaMovimientos.clear();
                for(DataSnapshot usuario : snapshot.getChildren()) {
                    String idusuario = usuario.getKey();

                    for (DataSnapshot movimientos : usuario.getChildren()) {
                            Movimiento movimiento = movimientos.getValue(Movimiento.class);
                            listaMovimientos.add(movimiento);
                    }

                }

                Comparator<Movimiento> ordenAlfabetico = new Comparator<Movimiento>() {
                    @Override
                    public int compare(Movimiento o1, Movimiento o2) {
                        return o1.getEstado().compareTo(o2.getEstado());
                    }
                };
                Collections.sort(listaMovimientos, ordenAlfabetico);
                Collections.reverse(listaMovimientos);

                movimientosAdapter = new MovimientosAdapter(getContext(), listaMovimientos, ((MainActivity)getActivity()).listaUsuariosMain, MovimientosFragment.this);
                recyclerView.setAdapter(movimientosAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        reference.addValueEventListener(obtenerMovimientosListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        reference.addValueEventListener(obtenerMovimientosListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        reference.removeEventListener(obtenerMovimientosListener);
    }

    @Override
    public void completar(Movimiento movimiento) {
        if (!movimiento.getEstado().equals("Completado")) {

            //Obtenemos el usuario que ha realizado el movimiento
            Usuario usuario = new Usuario();
            for (Usuario usuarioTmp : ((MainActivity)getActivity()).listaUsuariosMain) {
                if (usuarioTmp.getId().equals(movimiento.getClienteId())) {
                    usuario = usuarioTmp;
                }
            }

            //Sumamos el dinero del movimiento a su cartera y completamos el estado del movimiento
            Double dineroASumar = movimiento.getDinero();
            Double carteraOld = usuario.getCartera();
            Double carteraNew = dineroASumar + carteraOld;
            usuario.setCartera(carteraNew);

            movimiento.setEstado("Completado");

            //Actualizamos datos en la BD
            FirebaseDatabase.getInstance().getReference("usuarios")
                    .child(usuario.getId())
                    .setValue(usuario, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError error, DatabaseReference ref) {
                            Log.e("Cartera usuario:", "ok");
                        }
                    });

            FirebaseDatabase.getInstance()
                    .getReference("movimientos")
                    .child(movimiento.getClienteId())
                    .child(movimiento.getMovimientoId())
                    .setValue(movimiento, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError error, DatabaseReference ref) {
                            Toast.makeText(getContext(), R.string.dinero_anadido, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void cancelar(Movimiento movimiento) {
        movimiento.setEstado("Cancelado");

        FirebaseDatabase.getInstance()
                .getReference("movimientos")
                .child(movimiento.getClienteId())
                .child(movimiento.getMovimientoId())
                .setValue(movimiento, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError error, DatabaseReference ref) {
                        Toast.makeText(getContext(), R.string.movimiento_cancelado, Toast.LENGTH_SHORT).show();
                    }
                });

    }
}