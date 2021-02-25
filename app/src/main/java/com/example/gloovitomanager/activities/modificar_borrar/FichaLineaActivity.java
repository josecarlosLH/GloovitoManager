package com.example.gloovitomanager.activities.modificar_borrar;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gloovitomanager.R;
import com.example.gloovitomanager.modelo.Linea;
import com.example.gloovitomanager.modelo.Usuario;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FichaLineaActivity extends AppCompatActivity {

    private EditText cantidadLineaET, precioLineaET;
    private Linea linea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ficha_linea);

        //Componentes del layout
        TextView numeroLineaFichaTV = findViewById(R.id.numeroLineaFichaTV);
        TextView subtotalTV = findViewById(R.id.subtotalTV);
        TextView nombreLocalLineaTV = findViewById(R.id.nombreLocalLineaFichaTV);
        cantidadLineaET = findViewById(R.id.cantidadLineaET);
        precioLineaET = findViewById(R.id.precioLineaET);
        TextView nombreProductoLineaTV = findViewById(R.id.nombreProductoLineaTV);
        final ImageButton editarLineaBT = findViewById(R.id.editarLineaBT);
        final ImageButton borrarLineaBT = findViewById(R.id.borrarLineaBT);

        //Intent
        Intent intent = getIntent();
        linea = (Linea) intent.getSerializableExtra("linea");
        String estado = intent.getStringExtra("estado");

        //Cargamos los datos en los componentes
        numeroLineaFichaTV.setText(linea.getNumlinea());
        subtotalTV.setText(String.valueOf(linea.getSubtotal()));
        nombreLocalLineaTV.setText(linea.getLocal());
        cantidadLineaET.setText(String.valueOf(linea.getCantidad()));
        precioLineaET.setText(String.valueOf(linea.getPrecio()));
        nombreProductoLineaTV.setText(linea.getProducto());

        //Desactivamos la modificación de la línea en caso de que el pedido esté completado o cancelado
        if ((estado.equals("Completado")) || estado.equals("Cancelado")) {
            cantidadLineaET.setEnabled(false);
            precioLineaET.setEnabled(false);
            editarLineaBT.setEnabled(false);
            borrarLineaBT.setEnabled(false);
        }

        //Listener del botón para editar la línea
        editarLineaBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(FichaLineaActivity.this);
                dialog.setTitle(R.string.seguro);
                dialog.setMessage(R.string.editarlinea);
                dialog.setPositiveButton(getString(R.string.editar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Comprobamos que los datos de la línea no estén vacíos
                        final String cantidad = cantidadLineaET.getText().toString();
                        final String precio = precioLineaET.getText().toString();

                        if ((!TextUtils.isEmpty(cantidad)) && (!TextUtils.isEmpty(precio))) {
                            if (Integer.parseInt(cantidad) < linea.getCantidad()) {
                                //Actualizamos el subtotal de la línea
                                linea.setCantidad(Integer.parseInt(cantidad));
                                linea.setPrecio(Double.valueOf(precio));
                                Double subtotalNew = linea.getCantidad() * linea.getPrecio();
                                linea.setSubtotal(subtotalNew);

                                //Pasamos la línea al onActivityResult de ficha pedido para obtener los datos de la línea modificada
                                Intent data = new Intent();
                                data.putExtra("linea", linea);
                                data.putExtra("estado", "editado");
                                setResult(RESULT_OK, data);
                                finish();
                            } else {
                                Toast.makeText(FichaLineaActivity.this, getString(R.string.cantidaderror), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(FichaLineaActivity.this, getString(R.string.camposvacios), Toast.LENGTH_SHORT).show();
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

        //Listener para borrar línea
        borrarLineaBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(FichaLineaActivity.this);
                dialog.setTitle(R.string.seguro);
                dialog.setMessage(R.string.usuariopermanentemente);
                dialog.setPositiveButton(getString(R.string.borrar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Pasamos la línea al onActivityResult de ficha pedido para obtener los datos de la línea a borrar
                        Intent data = new Intent();
                        data.putExtra("linea", linea);
                        data.putExtra("estado", "borrado");
                        setResult(RESULT_OK, data);
                        finish();
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
    }
}
