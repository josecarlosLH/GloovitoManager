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

import com.example.gloovitomanager.R;
import com.example.gloovitomanager.adapters.LocalesAdapter;
import com.example.gloovitomanager.modelo.Local;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LocalesFragment extends Fragment {

    private RecyclerView recyclerView;
    private LocalesAdapter localesAdapter;
    private List<Local> listaLocales;

    private DatabaseReference reference;
    private ValueEventListener obtenerLocalesListener;

    public LocalesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locales, container, false);

        //RecyclerView
        recyclerView = view.findViewById(R.id.localesRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Obtener locales
        listaLocales = new ArrayList<>();
        obtenerLocales();

        return view;
    }

    private void obtenerLocales() {
        reference = FirebaseDatabase.getInstance().getReference("locales");

        obtenerLocalesListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaLocales.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Local local = dataSnapshot.getValue(Local.class);
                    listaLocales.add(local);
                }
                localesAdapter = new LocalesAdapter(getContext(), listaLocales);
                recyclerView.setAdapter(localesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        reference.addValueEventListener(obtenerLocalesListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        reference.addValueEventListener(obtenerLocalesListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        reference.removeEventListener(obtenerLocalesListener);
    }
}