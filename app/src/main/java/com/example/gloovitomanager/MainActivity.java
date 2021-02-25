package com.example.gloovitomanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;

import com.example.gloovitomanager.activities.anadir.AnadirLocalActivity;
import com.example.gloovitomanager.fragments.LocalesFragment;
import com.example.gloovitomanager.fragments.MovimientosFragment;
import com.example.gloovitomanager.fragments.PedidosFragment;
import com.example.gloovitomanager.fragments.UsuariosFragment;
import com.example.gloovitomanager.modelo.Usuario;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Componentes
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    public List<Usuario> listaUsuariosMain = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Declaramos el tab layout  el viewpager junto a su adapter
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.view_pager);

        //NOTA: getSupportFragmentManager() se pone dentro del constructor porque lo que hace es devolver una objeto de tipo FragmentManager.
        //Gracias al FragmentManager, el ViewPager podrá cargar los distintos fragment disponibles en el View Pager.
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        //Añadimos los Fragment que hemos creado dentro del paquete fragments al ViewPager
        viewPagerAdapter.anadirFragment(new LocalesFragment(), getString(R.string.locales));
        viewPagerAdapter.anadirFragment(new UsuariosFragment(), getString(R.string.usuarios));
        viewPagerAdapter.anadirFragment(new MovimientosFragment(), "Mov.");
        viewPagerAdapter.anadirFragment(new PedidosFragment(), getString(R.string.pedidos));

        //Le añadimos el adapter al ViewPager y cargamos el ViewPager en el TabLayout
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    //Inflamos el menú
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //Añadimos funcionalidad a los botones
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Creamos un switch en el que cada caso representa la funcionalidad de uno de los botones del menú
        switch (item.getItemId()) {
            case R.id.anadirLocalIT:
                startActivity(new Intent(MainActivity.this, AnadirLocalActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;

            case R.id.preferenciasIT:
                startActivity(new Intent(MainActivity.this, PreferenciasActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
        }
        return false;
    }

    //Clase del adapter del ViewPager
    class ViewPagerAdapter extends FragmentPagerAdapter {
        //En este ArrayList almacenaremos los fragments que va a contener el ViewPager
        private ArrayList<Fragment> fragments;
        //En este ArrayList almacenaraemos los títulos de las pestañas correspondientes a cada fragment del ViewPager
        private  ArrayList<String> titulos;

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titulos = new ArrayList<>();
        }

        //Lo que devolvemos con este método es la posición dentro del ArrayList en la que está el fragment que queramos seleccionar.
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        //Devolvemos el número de fragments almacenados en el ArrayList
        @Override
        public int getCount() {
            return fragments.size();
        }

        //Método para añadir un fragment con su correspondiente título
        public void anadirFragment(Fragment fragment, String titulo) {
            fragments.add(fragment);
            titulos.add(titulo);
        }

        //Obtener número de páginas del ViewPager
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titulos.get(position);
        }
    }
}