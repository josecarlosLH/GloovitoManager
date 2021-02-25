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

public class FichaProductoActivity extends AppCompatActivity {

    //Componentes
    private ImageView productoIV;

    private EditText nombreProductoET,
            descripcionET,
            stockET,
            precioET;

    //Imagen local
    private StorageReference storageReference;
    private static final int PEDIR_IMAGEN = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    private String imagenURL = "default";

    private ArrayList<Producto> productos;

    //Firebase
    private DatabaseReference reference;

    private Producto producto;
    private String localid;
    private Local local;

    private ImageButton editarProductoBT;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ficha_producto);

        //Componentes del layout
        productoIV = findViewById(R.id.productoIV);
        nombreProductoET = findViewById(R.id.nombreProductoET);
        descripcionET = findViewById(R.id.descripcionET);
        stockET = findViewById(R.id.stockET);
        precioET = findViewById(R.id.precioET);
        editarProductoBT = findViewById(R.id.editarProductoBT);
        ImageButton borrarProductoBT = findViewById(R.id.borrarProductoBT);

        //Ubicar imagen de perfil en FireBase
        storageReference = FirebaseStorage.getInstance().getReference("Uploads");
        productoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarImagen();
            }
        });

        //Recibimos la id del producto que seleccionamos en el RecyclerView de productos
        Intent intent = getIntent();
        producto = (Producto) intent.getSerializableExtra("productoid");
        localid = intent.getStringExtra("localprodid");

        cargarInterfaz();

        editarProductoBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(FichaProductoActivity.this);
                dialog.setTitle(R.string.seguro);
                dialog.setMessage(R.string.editarproducto);
                dialog.setPositiveButton(getString(R.string.editar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        final String nombreProducto = nombreProductoET.getText().toString();
                        final String descripcion = descripcionET.getText().toString();
                        final String stock = stockET.getText().toString();
                        final String precio = precioET.getText().toString();

                        //Comprobamos que los datos del producto no estén vacíos
                        if ((!TextUtils.isEmpty(nombreProducto)) && (!TextUtils.isEmpty(descripcion)) && (!TextUtils.isEmpty(stock)) && (!TextUtils.isEmpty(precio))) {
                            //Actualizamos los datos
                            producto.setDescipcion(descripcion);
                            producto.setNombre(nombreProducto);
                            producto.setImagenURL(imagenURL);
                            producto.setPrecio(Double.valueOf(precio));
                            producto.setStock(Integer.parseInt(stock));

                            FirebaseDatabase.getInstance().getReference("locales")
                                    .child(localid)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Local local = snapshot.getValue(Local.class);

                                            //Actualizamos el producto en el objeto de local
                                            productos = local.getProductos();

                                            for (int cont = 0; cont < productos.size(); cont++) {
                                                if (producto.getIdproducto().equals(productos.get(cont).getIdproducto())) {
                                                    productos.set(cont, producto);
                                                }
                                            }

                                            local.setProductos(productos);

                                            //Guardamos los cambios en la BD
                                            FirebaseDatabase.getInstance().getReference("locales")
                                                    .child(localid)
                                                    .setValue(local);

                                            Toast.makeText(FichaProductoActivity.this, R.string.productoeditado, Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) { }
                                    });

                        } else {
                            Toast.makeText(FichaProductoActivity.this, getString(R.string.camposvacios), Toast.LENGTH_SHORT).show();
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

        borrarProductoBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(FichaProductoActivity.this);
                dialog.setTitle(R.string.seguro);
                dialog.setMessage(R.string.productopermanentemente);
                dialog.setPositiveButton(getString(R.string.borrar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //Borramos el producto
                        FirebaseDatabase.getInstance().getReference("locales")
                                .child(localid)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        local = snapshot.getValue(Local.class);
                                        ArrayList<Producto> productos = local.getProductos();

                                        //Borramos el producto de la lista de productos del local
                                        for (int cont = 0; cont < productos.size(); cont++) {
                                            if (producto.getIdproducto().equals(productos.get(cont).getIdproducto())) {
                                                productos.remove(cont);
                                            }
                                        }

                                        //Actualizamos el local
                                        local.setProductos(productos);
                                        FirebaseDatabase.getInstance().getReference("locales").child(localid).setValue(local);
                                        FichaProductoActivity.this.finish();

                                        Toast.makeText(FichaProductoActivity.this, R.string.producto_borrado, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) { }
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

    //Todos los métodos de abajo son para subir la imagen, asignásrsela al nuevo producto y ponerla en el ImageView del layout

    private String getExtensionArchivo(Uri uri) {
        ContentResolver contentResolver = FichaProductoActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void subirImagen() {
        final ProgressDialog progressDialog = new ProgressDialog(FichaProductoActivity.this);
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

                        producto.setImagenURL(imagenURL);
                        Toast.makeText(FichaProductoActivity.this, getString(R.string.imagenanadida), Toast.LENGTH_SHORT).show();
                        editarProductoBT.setEnabled(true);
                        cargarInterfaz();

                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(FichaProductoActivity.this, R.string.falloimagen, Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FichaProductoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(FichaProductoActivity.this, "No hay imagen seleccionada", Toast.LENGTH_SHORT).show();
        }
    }

    private void seleccionarImagen() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, PEDIR_IMAGEN);
        editarProductoBT.setEnabled(false);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PEDIR_IMAGEN && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(FichaProductoActivity.this, getString(R.string.subidaprogresp), Toast.LENGTH_SHORT).show();
            } else {
                subirImagen();
            }
        }
    }

    private void cargarInterfaz() {
        imagenURL = producto.getImagenURL();
        nombreProductoET.setText(producto.getNombre());
        descripcionET.setText(producto.getDescipcion());
        stockET.setText(String.valueOf(producto.getStock()));
        precioET.setText(String.valueOf(producto.getPrecio()));

        //IMAGEN
        if (producto.getImagenURL().equals("default")) {
            Glide.with(FichaProductoActivity.this)
                    .load(R.drawable.ic_product)
                    .circleCrop()
                    .into(productoIV);
        } else {
            Glide.with(FichaProductoActivity.this)
                    .load(imagenURL)
                    .circleCrop()
                    .into(productoIV);
        }
    }
}