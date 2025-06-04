package com.example.ezpos;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.ezpos.fragments.HistorialFragment;
import com.example.ezpos.fragments.InventarioFragment;
import com.example.ezpos.fragments.PedidosFragment;
import com.example.ezpos.EasterEgg;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PedidosFragment()).commit();

        bottomNav.setOnLongClickListener(v -> {
            EasterEgg.show(this);
            return true;
        });

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_pedidos) {
                selectedFragment = new PedidosFragment();
            } else if (item.getItemId() == R.id.nav_inventario) {
                selectedFragment = new InventarioFragment();
            } else if (item.getItemId() == R.id.nav_historial) {
                selectedFragment = new HistorialFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }

            return false;
        });
    }
}
