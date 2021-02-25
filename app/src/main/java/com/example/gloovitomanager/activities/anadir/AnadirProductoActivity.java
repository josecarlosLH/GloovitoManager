package com.example.gloovitomanager.activities.anadir;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.example.gloovitomanager.activities.modificar_borrar.FichaProductoActivity;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AnadirProductoActivity extends AppCompatActivity {

    //Componentes
    private ImageView productoAnadirIV;

    private EditText nombreProductoAnadirET,
            descripcionAnadirET,
            stockAnadirET,
            precioAnadirET;

    //Imagen producto
    private StorageReference storageReference;
    private static final int PEDIR_IMAGEN = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    private DatabaseReference reference;

    private String productoid;
    private String imagenURL = "default";
    private Local local;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir_producto);

        //Intent con el ID del local
        Intent intent = getIntent();
        String localid = intent.getStringExtra("localid");

        //Componentes del layout
        productoAnadirIV = findViewById(R.id.productoAnadirIV);
        nombreProductoAnadirET = findViewById(R.id.nombreProductoAnadirET);
        descripcionAnadirET = findViewById(R.id.descripcionAnadirET);
        stockAnadirET = findViewById(R.id.stockAnadirET);
        precioAnadirET = findViewById(R.id.precioAnadirET);
        ImageButton anadirProductoBT = findViewById(R.id.anadirProductoBT);

        //Ubicar imagen de perfil en FireBase
        storageReference = FirebaseStorage.getInstance().getReference("Uploads");
        productoAnadirIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarImagen();
            }
        });

        //Imagen
        cargarImagen();

        //Reference y listener de añadir producto
        reference = FirebaseDatabase.getInstance().getReference("locales").child(localid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                local = snapshot.getValue(Local.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        anadirProductoBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Comprobamos que los datos del producto no estén vacíos
                final String nombreProducto = nombreProductoAnadirET.getText().toString();
                final String descripcion = descripcionAnadirET.getText().toString();
                final String stock = stockAnadirET.getText().toString();
                final String precio = precioAnadirET.getText().toString();

                if ((!TextUtils.isEmpty(nombreProducto)) && (!TextUtils.isEmpty(descripcion)) && (!TextUtils.isEmpty(stock)) && (!TextUtils.isEmpty(precio))) {
                    //Añadimos el producto nuevo

                    //Creamos la ID basándonos en la fecha actual
                    Date date = new Date();
                    long id = date.getTime();
                    productoid = String.valueOf(id);

                    ArrayList<Producto> listaProductos = local.getProductos();

                    Producto productoNuevo = new Producto(productoid, descripcion, nombreProducto, imagenURL, Double.valueOf(precio), Integer.parseInt(stock));
                    if (listaProductos == null)
                        listaProductos = new ArrayList<>();

                    listaProductos.add(productoNuevo);
                    local.setProductos(listaProductos);
                    reference.setValue(local, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError error, DatabaseReference ref) {
                                    Toast.makeText(AnadirProductoActivity.this, getString(R.string.productoanadido), Toast.LENGTH_SHORT).show();
                                }
                            });

                } else {
                    Toast.makeText(AnadirProductoActivity.this, getString(R.string.camposvacios), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //Todos los métodos de abajo son para subir la imagen, asignásrsela al nuevo producto y ponerla en el ImageView del layout

    private String getExtensionArchivo(Uri uri) {
        ContentResolver contentResolver = AnadirProductoActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void subirImagen() {
        final ProgressDialog progressDialog = new ProgressDialog(AnadirProductoActivity.this);
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
                        Toast.makeText(AnadirProductoActivity.this, getString(R.string.imagenanadida), Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(AnadirProductoActivity.this, R.string.falloimagen, Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AnadirProductoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(AnadirProductoActivity.this, "No hay imagen seleccionada", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AnadirProductoActivity.this, getString(R.string.subidaprogresp), Toast.LENGTH_SHORT).show();
            } else {
                subirImagen();
            }
        }
    }

    private void cargarImagen() {
        if (imagenURL.equals("default")) {
            Glide.with(AnadirProductoActivity.this)
                    .load(R.drawable.ic_product)
                    .circleCrop()
                    .into(productoAnadirIV);
        } else {
            Glide.with(AnadirProductoActivity.this)
                    .load(imagenURL)
                    .circleCrop()
                    .into(productoAnadirIV);
        }
    }
}