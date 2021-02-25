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

import com.example.gloovitomanager.MainActivity;
import com.example.gloovitomanager.R;
import com.example.gloovitomanager.adapters.PedidosAdapter;
import com.example.gloovitomanager.modelo.Linea;
import com.example.gloovitomanager.modelo.Pedido;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PedidosFragment extends Fragment {

    private RecyclerView recyclerView;
    private PedidosAdapter pedidosAdapter;
    private List<Pedido> listaPedidos;

    private DatabaseReference reference;
    private ValueEventListener obtenerPedidosListener;

    public PedidosFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedidos, container, false);

        //RecyclerView
        recyclerView = view.findViewById(R.id.pedidosRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Obtener pedidos
        listaPedidos = new ArrayList<>();
        obtenerPedidos();
        return view;
    }

    private void obtenerPedidos() {
        reference = FirebaseDatabase.getInstance().getReference("pedidos");

        obtenerPedidosListener = reference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPedidos.clear();

                //Cargamos los pedidos en la lista
                for(DataSnapshot usuario : snapshot.getChildren()) {
                    for (DataSnapshot pedidos : usuario.getChildren()) {
                        Pedido pedido = pedidos.getValue(Pedido.class);
                        listaPedidos.add(pedido);
                    }
                }

                Set<Pedido> set = new HashSet<>(listaPedidos);
                listaPedidos.clear();
                listaPedidos.addAll(set);
                //Ordenamos los pedidos por fecha y por estado
                Comparator<Pedido> ordenAlfabetico = new Comparator<Pedido>() {
                    @Override
                    public int compare(Pedido o1, Pedido o2) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Date x1 = null;
                        Date x2 = null;
                        try {
                            x1 = sdf.parse(o1.getFecha());
                            x2 = sdf.parse(o2.getFecha());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        int xComp = o1.getEstado().compareTo(o2.getEstado());
                        if (xComp != 0) {
                            return xComp;
                        }

                        return x1.compareTo(x2);
                    }
                };

                Collections.sort(listaPedidos, ordenAlfabetico);
                //Invertimos el orden para que salgan los primeros aquellos que están en revisión
                Collections.reverse(listaPedidos);
                //Cargamos el RV
                pedidosAdapter = new PedidosAdapter(getContext(), listaPedidos, ((MainActivity)getActivity()).listaUsuariosMain);
                recyclerView.setAdapter(pedidosAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        reference.addValueEventListener(obtenerPedidosListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        reference.addValueEventListener(obtenerPedidosListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        reference.removeEventListener(obtenerPedidosListener);
    }
}