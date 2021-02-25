package com.example.gloovitomanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class InicioActivity extends AppCompatActivity {

    //Componentes
    private EditText usuarioET, contrasenaET;
    private Button conectarBT;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        //Inicializamos componentes
        usuarioET = findViewById(R.id.usuarioET);
        contrasenaET = findViewById(R.id.contrasenaET);
        conectarBT = findViewById(R.id.conectrBT);

        //Listener al botón conectarse
        conectarBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usuario_texto = usuarioET.getText().toString();
                String contrasena_texto = contrasenaET.getText().toString();

                //Comprobamos si los campos están vacíos
               if (TextUtils.isEmpty(usuario_texto) || TextUtils.isEmpty(contrasena_texto)) {
                   Toast.makeText(InicioActivity.this, R.string.camposvacios, Toast.LENGTH_SHORT).show();
               } else {
                   //Obtenemos los valores de los campos en las preferencias
                   SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(InicioActivity.this);
                   String prefUsuario = prefs.getString("pref_usuario", "system");
                   String prefContrasena = prefs.getString("pref_contrasena", "manager");

                   //Tenemos que comprobar si los datos son correctos
                    if (usuario_texto.equals(prefUsuario) && contrasena_texto.equals(prefContrasena)) {
                        //Si los datos son correctos, pasamos a la pantalla principal
                        Intent i = new Intent(InicioActivity.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    } else {
                        //Si no son correctos, le damos un toast indicando que hay error
                        Toast.makeText(InicioActivity.this, R.string.credencialesincorrectas, Toast.LENGTH_SHORT).show();
                    }
               }
            }
        });
    }
}