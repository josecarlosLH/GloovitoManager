package com.example.gloovitomanager.activities.modificar_borrar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gloovitomanager.R;
import com.example.gloovitomanager.modelo.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FichaUsuarioActivity extends AppCompatActivity {

    //Componentes
    private ImageView usuarioFichaIV;

    private EditText nombreUsuarioET,
                    carteraET,
                    reservaET;

    //Firebase
    private DatabaseReference reference;

    private String usuarioid;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ficha_usuario);

        //Componentes del layout
        usuarioFichaIV = findViewById(R.id.usuarioFichaIV);
        nombreUsuarioET = findViewById(R.id.nombreUsuarioET);
        carteraET = findViewById(R.id.carteraET);
        reservaET = findViewById(R.id.reservaET);
        ImageButton editarUsuarioBT = findViewById(R.id.editarUsuarioBT);
        ImageButton borrarUsuarioBT = findViewById(R.id.borrarUsuarioBT);

        //Intent. Recibimos la id del usuario que seleccionamos en el RecyclerView de usuarios
        Intent intent = getIntent();
        usuarioid = intent.getStringExtra("usuarioid");
        reference = FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(usuarioid);

        //Cargamos los datos del usuario
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuario = snapshot.getValue(Usuario.class);
                nombreUsuarioET.setText(usuario.getNombre());
                carteraET.setText(String.valueOf(usuario.getCartera()));
                reservaET.setText(String.valueOf(usuario.getReserva()));

                Glide.with(FichaUsuarioActivity.this)
                        .load(R.drawable.ic_user_large)
                        .circleCrop()
                        .into(usuarioFichaIV);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }

        });

        editarUsuarioBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(FichaUsuarioActivity.this);
                dialog.setTitle(R.string.seguro);
                dialog.setMessage(R.string.editarusuario);
                dialog.setPositiveButton(getString(R.string.editar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        final String nombreUsuario = nombreUsuarioET.getText().toString();
                        final String cartera = carteraET.getText().toString();
                        final String reserva = reservaET.getText().toString();

                        //Comprobamos que los datos del cliente no estén vacíos
                        if ((!TextUtils.isEmpty(nombreUsuario)) && (!TextUtils.isEmpty(cartera)) && (!TextUtils.isEmpty(reserva))) {
                            //Actualizamos los datos
                            usuario.setNombre(nombreUsuario);
                            usuario.setCartera(Double.valueOf(cartera));
                            usuario.setReserva(Double.valueOf(reserva));

                            reference.setValue(usuario, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError error, DatabaseReference ref) {
                                            Toast.makeText(FichaUsuarioActivity.this, getString(R.string.usuarioeditado), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            Toast.makeText(FichaUsuarioActivity.this, getString(R.string.camposvacios), Toast.LENGTH_SHORT).show();
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

        borrarUsuarioBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(FichaUsuarioActivity.this);
                dialog.setTitle(R.string.seguro);
                dialog.setMessage(R.string.usuariopermanentemente);
                dialog.setPositiveButton(getString(R.string.borrar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Restablecemos el usuario
                        usuario.setReserva(0.0);
                        usuario.setCartera(0.0);

                        //Actualizamos el user
                        FirebaseDatabase.getInstance()
                                        .getReference("usuarios")
                                        .child(usuarioid)
                                        .setValue(usuario, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                carteraET.setText(String.valueOf(usuario.getCartera()));
                                                reservaET.setText(String.valueOf(usuario.getReserva()));
                                                Toast.makeText(FichaUsuarioActivity.this, getString(R.string.usuario_restablecido), Toast.LENGTH_SHORT).show();
                                            }
                                        });
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