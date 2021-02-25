package com.example.gloovitomanager.activities.modificar_borrar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gloovitomanager.MainActivity;
import com.example.gloovitomanager.R;
import com.example.gloovitomanager.activities.ListaProductosActivity;
import com.example.gloovitomanager.adapters.LineasAdapter;
import com.example.gloovitomanager.adapters.ProductosAdapter;
import com.example.gloovitomanager.modelo.Linea;
import com.example.gloovitomanager.modelo.Local;
import com.example.gloovitomanager.modelo.Pedido;
import com.example.gloovitomanager.modelo.Producto;
import com.example.gloovitomanager.modelo.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//REVISAR POR SI HAY ALGÚN ERROR
public class FichaPedidoActivity extends AppCompatActivity implements LineasAdapter.OnLineasClickListener {

    //Componentes
    private EditText mensajeET;
    private ImageButton editarPedidoBT;
    private TextView totalTV;
    private RecyclerView lineasRV;
    private Spinner spinnerPedido;

    private String opcion;
    public Double total;
    private List<Linea> listaStock, listaNoExiste;
    public Pedido pedidoGlobal;
    private Usuario usuario;

    public FichaPedidoActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ficha_pedido);

        //RecyclerView
        lineasRV = findViewById(R.id.lineasRV);
        lineasRV.setHasFixedSize(true);
        lineasRV.setLayoutManager(new LinearLayoutManager(FichaPedidoActivity.this));

        //Componentes del layout
        TextView idPedidoTV = findViewById(R.id.idPedidoTV);
        totalTV = findViewById(R.id.totalTV);
        TextView idClientePedidoTV = findViewById(R.id.idClientePedidoTV);
        TextView fechaTV = findViewById(R.id.fechaTV);
        mensajeET = findViewById(R.id.mensajeET);
        spinnerPedido = findViewById(R.id.spinnerPedido);
        editarPedidoBT = findViewById(R.id.editarPedidoBT);

        //Intent
        listaNoExiste = new ArrayList<>();
        listaStock = new ArrayList<>();

        Intent intent = getIntent();
        pedidoGlobal = (Pedido) intent.getSerializableExtra("pedidoid");
        usuario = (Usuario) intent.getSerializableExtra("pedidouser");

        //Guardamos el total original del pedido
        total = pedidoGlobal.getTotal();

        //Cargamos el RecyvlerView
        recargarLista();

        //Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.opciones, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPedido.setAdapter(adapter);
        spinnerPedido.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                opcion = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        //Ponemos los datos en los componentes
        idPedidoTV.setText(pedidoGlobal.getIdpedido());
        idClientePedidoTV.setText(pedidoGlobal.getIdUsuario());
        totalTV.setText(String.valueOf(pedidoGlobal.getTotal()));
        mensajeET.setText(pedidoGlobal.getMensajeEstado());
        fechaTV.setText(pedidoGlobal.getFecha());

        //Desactivamos componentes dependiendo del estado del pedido y se lo ponemos como opción en el spinner
        switch (pedidoGlobal.getEstado()) {
            case "Revision":
                spinnerPedido.setSelection(0);
                break;
            case "Cancelado":
                spinnerPedido.setSelection(1);
                editarPedidoBT.setEnabled(false);
                mensajeET.setEnabled(false);
                spinnerPedido.setEnabled(false);
                break;
            case "Completado":
                spinnerPedido.setSelection(2);
                editarPedidoBT.setEnabled(false);
                mensajeET.setEnabled(false);
                spinnerPedido.setEnabled(false);
                break;
        }

        //Listener de editar pedido
        editarPedidoBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(FichaPedidoActivity.this);
                dialog.setTitle(R.string.seguro);
                dialog.setMessage(R.string.editarpedido);
                dialog.setPositiveButton(getString(R.string.editar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Comprobamos el estado del pedido y llamamos a su método respectivo
                        if (opcion.equals("Completado")) {
                            completarPedido(pedidoGlobal);
                        } else if (opcion.equals("Cancelado")) {
                            cancelarPedido();
                        }
                    }
                });

                dialog.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });

        recargarLista();
    }

    private void cancelarPedido() {
        pedidoGlobal.setEstado("Cancelado");
        pedidoGlobal.setMensajeEstado(mensajeET.getText().toString());
        spinnerPedido.setEnabled(false);
        mensajeET.setEnabled(false);
        editarPedidoBT.setEnabled(false);

        //Ponemos el pedido como cancelado
        FirebaseDatabase.getInstance()
                .getReference("pedidos")
                .child(pedidoGlobal.getIdUsuario())
                .child(pedidoGlobal.getIdpedido())
                .setValue(pedidoGlobal, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError error, DatabaseReference ref) { }
                });

        //Quitamos el importe del pedido de la reserva del cliente y lo actualizamos en la BD
        Double dineroASumar = pedidoGlobal.getTotal();
        Double reservaUsuario = usuario.getReserva();
        Double reservaActual = reservaUsuario - dineroASumar;
        usuario.setReserva(reservaActual);

        Double carteraSinSumar = usuario.getCartera();
        Double carteraActual = carteraSinSumar + dineroASumar;
        usuario.setCartera(carteraActual);

        FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(usuario.getId())
                .setValue(usuario, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError error, DatabaseReference ref) {}
                });

        Toast.makeText(FichaPedidoActivity.this, getString(R.string.pedidocancelado), Toast.LENGTH_SHORT).show();
    }


    private void completarPedido(final Pedido pedidoTmp) {
        DatabaseReference referenciaLocales = FirebaseDatabase.getInstance().getReference("locales");
        final Double diferencia = total - pedidoTmp.getTotal();

        referenciaLocales.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaStock.clear();
                listaNoExiste.clear();

                //Se recogen los locales. El String es el ID del local
                HashMap<String, Local> locales = new HashMap<>();

                //Rellenamos el HashMap con los locales
                for (DataSnapshot localsnap : snapshot.getChildren()) {
                    Local local = localsnap.getValue(Local.class);
                    locales.put(localsnap.getKey(), local);
                }

                //Obtenemos las líneas del pedido
                for (Linea linea : pedidoTmp.getLineas()) {
                    Producto producto = null;
                    Local local1 = locales.get(linea.getLocalid());
                    ArrayList<Producto> listaProductos = null;

                    //Obtenemos los productos del local al que hace referencia la línea
                    if (local1 != null) {
                        listaProductos = local1.getProductos();
                    }

                    //Localizamos el producto de la línea en la lista de productos del local
                    if (listaProductos != null)
                        for (Producto prod : listaProductos) {
                            if (prod.getIdproducto().equals(linea.getProductoid())) {
                                producto = prod;
                            }
                        }

                    //Si el producto existe y no tiene stock suficiente, lo añadimos a la lista de productos sin stock
                    if (producto != null) {
                        //Comprobamos si la línea es válida
                        if (!(linea.getCantidad() <= producto.getStock()))
                            listaStock.add(linea);
                    } else {
                        //Si no existe, lo añadimos a la lista de productos que no existen
                        listaNoExiste.add(linea);
                    }
                }

                //Aquí se tiene que cambiar estado pedido y el stock de cada producto
                if (listaNoExiste.isEmpty() && listaStock.isEmpty()) {

                    //Cambiamos el estado de los componentes
                    spinnerPedido.setEnabled(false);
                    mensajeET.setEnabled(false);
                    editarPedidoBT.setEnabled(false);

                    for (final Linea linea : pedidoGlobal.getLineas()) {
                        Local local = locales.get(linea.getLocalid());
                        ArrayList<Producto> productos = local.getProductos();

                        int cont;
                        Producto producto;
                        for (cont = 0; cont < productos.size(); cont++) {
                            if (productos.get(cont).getIdproducto().equals(linea.getProductoid())) {
                                producto = productos.get(cont);

                                //Modificamos el stock del producto
                                int stock = producto.getStock();
                                int cantidad = linea.getCantidad();
                                int cantidadTotal = stock - cantidad;
                                producto.setStock(cantidadTotal);

                                productos.set(cont, producto);
                            }
                        }

                        //Cambiamos el estado del pedido y su mensaje
                        pedidoTmp.setEstado("Completado");
                        pedidoTmp.setMensajeEstado(mensajeET.getText().toString());

                        //Actualizamos el local con el Array de productos
                        FirebaseDatabase.getInstance()
                            .getReference("locales")
                            .child(linea.getLocalid())
                            .setValue(local, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError error, DatabaseReference ref) {
                                    Log.e("Producto bien act.", "nice");
                                }
                            });
                    }

                    //Quitamos el importe del pedido de la reserva del cliente y lo actualizamos en la BD
                    Double dineroARestar = pedidoTmp.getTotal() + diferencia;
                    Double reservaUsuario = usuario.getReserva();
                    Double reservaActual = reservaUsuario - dineroARestar;
                    usuario.setReserva(reservaActual);

                    if (diferencia > 0) {
                        usuario.setCartera(usuario.getCartera() + diferencia);
                    }

                    FirebaseDatabase.getInstance()
                            .getReference("usuarios")
                            .child(usuario.getId())
                            .setValue(usuario, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError error, DatabaseReference ref) {
                                    Log.e("Usuario bien act.", "nice");
                                }
                            });

                    //Actualizamos el pedido junto con todas sus líneas
                    FirebaseDatabase.getInstance()
                            .getReference("pedidos")
                            .child(pedidoTmp.getIdUsuario())
                            .child(pedidoTmp.getIdpedido())
                            .setValue(pedidoTmp, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError error, DatabaseReference ref) {
                                    Toast.makeText(FichaPedidoActivity.this, getString(R.string.pedido_confirmado), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    recargarLista();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Controlamos la línea modificada en la FichaLineaActivity
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            ArrayList<Linea> lineas = pedidoGlobal.getLineas();
            Linea linea = (Linea) data.getSerializableExtra("linea");

            String resultado = data.getStringExtra("estado");

            int cont;
            for (cont = 0; cont < lineas.size(); cont++) {
                if (linea.getNumlinea().equals(lineas.get(cont).getNumlinea())) {
                    if (resultado.equals("editado")) {
                        //Modificamos el subtotal de la linea y el total del pedido
                        Double subtotalOld = lineas.get(cont).getSubtotal();
                        Double totalTmp = pedidoGlobal.getTotal() - subtotalOld;
                        pedidoGlobal.setTotal(totalTmp);
                        Double totalNew = pedidoGlobal.getTotal() + linea.getSubtotal();
                        pedidoGlobal.setTotal(totalNew);

                        lineas.set(cont, linea);
                    } else {
                        pedidoGlobal.setTotal(pedidoGlobal.getTotal() - lineas.get(cont).getSubtotal());
                        lineas.remove(cont);
                    }
                }
            }
            totalTV.setText(String.valueOf(pedidoGlobal.getTotal()));

            pedidoGlobal.setLineas(lineas);
            recargarLista();
        }
    }

    //Refrescamos el RV de líneas para que nos muestre los productos con y sin stock y los no existentes.
    public void recargarLista() {
        lineasRV.setAdapter(new LineasAdapter(pedidoGlobal.getLineas(), FichaPedidoActivity.this, listaStock, listaNoExiste, usuario, this));
    }

    //Método que se ejecuta cuando seleccionamos un item del RV de línea
    @Override
    public void abrir(Linea l) {
        Intent i = new Intent(FichaPedidoActivity.this, FichaLineaActivity.class);
        i.putExtra("linea", l);
        i.putExtra("usuario", usuario);
        i.putExtra("estado", pedidoGlobal.getEstado());
        startActivityForResult(i, 1000);
    }
}