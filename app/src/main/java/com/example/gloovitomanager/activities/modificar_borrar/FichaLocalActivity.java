package com.example.gloovitomanager.activities.modificar_borrar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gloovitomanager.R;
import com.example.gloovitomanager.modelo.Local;
import com.example.gloovitomanager.modelo.Producto;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class FichaLocalActivity extends AppCompatActivity {

    //Componentes
    private ImageView localIV;

    private EditText nombreLocalET,
             direccionET;

    //Imagen local
    private StorageReference storageReference;
    private static final int PEDIR_IMAGEN = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    private String imagenURL = "default";

    private DatabaseReference reference;

    private String localid;
    private ArrayList<Producto> listaProductos;
    private Local local;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ficha_local);

        //Componentes del layout
        localIV = findViewById(R.id.localIV);
        nombreLocalET = findViewById(R.id.nombreLocalET);
        direccionET = findViewById(R.id.direccionET);
        ImageButton editarLocalBT = findViewById(R.id.editarLocalBT);
        ImageButton borrarLocalBT = findViewById(R.id.borrarLocalBT);

        //Ubicar imagen de perfil en FireBase
        storageReference = FirebaseStorage.getInstance().getReference("Uploads");
        localIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarImagen();
            }
        });

        //Intent. Recibimos la id del local que seleccionamos en el RecyclerView de locales
        Intent intent = getIntent();
        localid = intent.getStringExtra("localid");
        reference = FirebaseDatabase.getInstance()
                                    .getReference("locales")
                                    .child(localid);

        //Cargamos los datos del local
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                local = snapshot.getValue(Local.class);
                nombreLocalET.setText(local.getNombre());
                direccionET.setText(local.getDireccion());
                listaProductos = local.getProductos();

                //Si el local no tiene una imagen determinado, cargamos una por defecto
                if (local.getImagenURL().equals("default")) {
                    Glide.with(FichaLocalActivity.this)
                            .load(R.drawable.ic_local)
                            .circleCrop()
                            .into(localIV);
                } else {
                    Glide.with(FichaLocalActivity.this)
                            .load(local.getImagenURL())
                            .circleCrop()
                            .into(localIV);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }

        });

        //Listener del botón editar local
        editarLocalBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(FichaLocalActivity.this);
                dialog.setTitle(R.string.seguro);
                dialog.setMessage(R.string.editarlocal);
                dialog.setPositiveButton(getString(R.string.editar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        final String nombreLocal = nombreLocalET.getText().toString();
                        final String direccion = direccionET.getText().toString();

                        //Comprobamos que los datos del local no estén vacíos
                        if ((!TextUtils.isEmpty(nombreLocal)) && (!TextUtils.isEmpty(direccion))) {
                            //Actualizamos el objeto en la base de datos
                            Local localModificado = new Local(nombreLocal, direccion, localid, listaProductos, imagenURL);
                            reference.setValue(localModificado, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError error, DatabaseReference ref) {
                                    Toast.makeText(FichaLocalActivity.this, getString(R.string.localeditado), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(FichaLocalActivity.this, getString(R.string.camposvacios), Toast.LENGTH_SHORT).show();
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

        //Listener de borrar local
        borrarLocalBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(FichaLocalActivity.this);
                dialog.setTitle(R.string.seguro);
                dialog.setMessage(R.string.localpermanentemente);
                dialog.setPositiveButton(getString(R.string.borrar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        final String nombreLocal = nombreLocalET.getText().toString();
                        final String direccion = direccionET.getText().toString();

                        //Comprobamos que los datos del cliente no estén vacíos
                        if ((!TextUtils.isEmpty(nombreLocal)) && (!TextUtils.isEmpty(direccion))) {
                            //Borramos el local de la base de datos
                            reference.removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError error, DatabaseReference ref) {
                                    Toast.makeText(FichaLocalActivity.this, getString(R.string.local_borrado), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(FichaLocalActivity.this, R.string.camposvacios, Toast.LENGTH_SHORT).show();
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
    }

    //Todos los métodos de abajo son para subir la imagen, asignásrsela al nuevo producto y ponerla en el ImageView del layout

    private String getExtensionArchivo(Uri uri) {
        ContentResolver contentResolver = FichaLocalActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void subirImagen() {
        final ProgressDialog progressDialog = new ProgressDialog(FichaLocalActivity.this);
        progressDialog.setMessage(getString(R.string.subiendo));
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getExtensionArchivo(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()) throw task.getException();
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {

                        Uri descargarUri = task.getResult();
                        imagenURL = descargarUri.toString();

                        local.setImagenURL(imagenURL);
                        reference.setValue(local, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError error, DatabaseReference ref) {
                                Toast.makeText(FichaLocalActivity.this, getString(R.string.imagenanadida), Toast.LENGTH_SHORT).show();
                            }
                        });

                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(FichaLocalActivity.this, R.string.falloimagen, Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FichaLocalActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(FichaLocalActivity.this, "No hay imagen seleccionada", Toast.LENGTH_SHORT).show();
        }
    }

    private void seleccionarImagen() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, PEDIR_IMAGEN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PEDIR_IMAGEN && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(FichaLocalActivity.this, getString(R.string.subidaprogresp), Toast.LENGTH_SHORT).show();
            } else {
                subirImagen();
            }
        }
    }
}