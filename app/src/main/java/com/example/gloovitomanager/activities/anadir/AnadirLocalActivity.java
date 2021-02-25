package com.example.gloovitomanager.activities.anadir;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;

public class AnadirLocalActivity extends AppCompatActivity {

    //Componentes
    private ImageView localAnadirIV;
    private EditText nombreLocalAnadirET, direccionAnadirET;

    //Imagen local
    private StorageReference storageReference;
    private static final int PEDIR_IMAGEN = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    private String imagenURL = "default";
    private DatabaseReference reference;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir_local);

        //Componentes del layout
        localAnadirIV = findViewById(R.id.localAnadirIV);
        nombreLocalAnadirET = findViewById(R.id.nombreLocalAnadirET);
        direccionAnadirET = findViewById(R.id.direccionAnadirET);
        ImageButton anadirLocalBT = findViewById(R.id.anadirLocalBT);

        //Ubicar imagen de perfil en FireBase
        storageReference = FirebaseStorage.getInstance().getReference("Uploads");
        localAnadirIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarImagen();
            }
        });

        //Imagen
        cargarImagen();

        //Referencia y listener de añadir
        reference = FirebaseDatabase.getInstance().getReference("locales");

        anadirLocalBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Comprobamos que los datos del producto no estén vacíos
                final String nombreLocal = nombreLocalAnadirET.getText().toString();
                final String direccion = direccionAnadirET.getText().toString();

                if ((!TextUtils.isEmpty(nombreLocal)) && (!TextUtils.isEmpty(direccion))) {
                    //Añadimos el producto nuevo

                    //Creamos la ID basándonos en la fecha actual
                    Date date = new Date();
                    long id = date.getTime();

                    ArrayList<Producto> lista = new ArrayList<>();

                    Local localNuevo = new Local(nombreLocal, direccion, String.valueOf(id), lista, imagenURL);
                    reference.child(String.valueOf(id))
                             .setValue(localNuevo, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError error, DatabaseReference ref) {
                                    Toast.makeText(AnadirLocalActivity.this, getString(R.string.localanadido), Toast.LENGTH_SHORT).show();
                                }
                            });

                } else {
                    Toast.makeText(AnadirLocalActivity.this, getString(R.string.camposvacios), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //Todos los métodos de abajo son para subir la imagen, asignásrsela al nuevo producto y ponerla en el ImageView del layout

    private String getExtensionArchivo(Uri uri) {
        ContentResolver contentResolver = AnadirLocalActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void subirImagen() {
        final ProgressDialog progressDialog = new ProgressDialog(AnadirLocalActivity.this);
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
                        cargarImagen();
                        Toast.makeText(AnadirLocalActivity.this, getString(R.string.imagenanadida), Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(AnadirLocalActivity.this, R.string.falloimagen, Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AnadirLocalActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(AnadirLocalActivity.this, "No hay imagen seleccionada", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AnadirLocalActivity.this, getString(R.string.subidaprogresp), Toast.LENGTH_SHORT).show();
            } else {
                subirImagen();
            }
        }
    }

    private void cargarImagen() {
        //Imagen
        if (imagenURL.equals("default")) {
            Glide.with(AnadirLocalActivity.this)
                    .load(R.drawable.ic_local)
                    .circleCrop()
                    .into(localAnadirIV);
        } else {
            Glide.with(AnadirLocalActivity.this)
                    .load(imagenURL)
                    .circleCrop()
                    .into(localAnadirIV);
        }
    }

}