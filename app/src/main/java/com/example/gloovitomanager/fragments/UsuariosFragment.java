package com.example.gloovitomanager.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gloovitomanager.MainActivity;
import com.example.gloovitomanager.R;
import com.example.gloovitomanager.adapters.UsuariosAdapter;
import com.example.gloovitomanager.modelo.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UsuariosFragment extends Fragment {

    private RecyclerView recyclerView;
    private UsuariosAdapter usuariosAdapter;

    private DatabaseReference reference;
    private ValueEventListener obtenerUsuariosListener;

    public UsuariosFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usuarios, container, false);

        //RecyclerView
        recyclerView = view.findViewById(R.id.usuariosRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Obtener usuarios
        ((MainActivity)getActivity()).listaUsuariosMain = new ArrayList<>();
        obtenerUsuarios();
        return view;
    }

    private void obtenerUsuarios() {
        reference = FirebaseDatabase.getInstance().getReference("usuarios");

        obtenerUsuariosListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ((MainActivity)getActivity()).listaUsuariosMain.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    ((MainActivity)getActivity()).listaUsuariosMain.add(usuario);
                }
                usuariosAdapter = new UsuariosAdapter(getContext(), ((MainActivity)getActivity()).listaUsuariosMain);
                recyclerView.setAdapter(usuariosAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        reference.addValueEventListener(obtenerUsuariosListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        reference.addValueEventListener(obtenerUsuariosListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        reference.removeEventListener(obtenerUsuariosListener);
    }
}