package com.example.gloovitomanager.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.example.gloovitomanager.R;
import com.example.gloovitomanager.activities.anadir.AnadirProductoActivity;
import com.example.gloovitomanager.activities.modificar_borrar.FichaProductoActivity;
import com.example.gloovitomanager.adapters.PedidosAdapter;
import com.example.gloovitomanager.adapters.ProductosAdapter;
import com.example.gloovitomanager.modelo.Pedido;
import com.example.gloovitomanager.modelo.Producto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListaProductosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductosAdapter productosAdapter;
    private List<Producto> listaProductos;

    private String localid;

    public ListaProductosActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_productos);

        //RecyclerView
        recyclerView = findViewById(R.id.productosRV);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        //Intent con el id del local
        Intent intent = getIntent();
        localid = intent.getStringExtra("localid");

        //Cargamos los productos
        cargarProductos(localid);
    }

    //Inflamos el menú
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_producto, menu);
        return true;
    }

    //Añadimos funcionalidad al item del menú
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Creamos un switch en el que cada caso representa la funcionalidad de uno de los botones del menú
        switch (item.getItemId()) {
            case R.id.anadirProductoIT:
                Intent i = new Intent(ListaProductosActivity.this, AnadirProductoActivity.class);
                i.putExtra("localid", localid);
                ListaProductosActivity.this.startActivity(i);

                return true;
        }
        return false;
    }

    //Cargamos los productos de un local concreto y los cargamos en el RV
    private void cargarProductos(final String localid) {
        listaProductos = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("locales")
                .child(localid)
                .child("productos");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaProductos.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Producto producto = dataSnapshot.getValue(Producto.class);
                    listaProductos.add(producto);

                    //Cargamos los productos en el RV
                    productosAdapter = new ProductosAdapter(ListaProductosActivity.this, listaProductos, localid);
                    recyclerView.setAdapter(productosAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
