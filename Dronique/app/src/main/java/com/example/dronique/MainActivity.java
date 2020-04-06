package com.example.dronique;

import android.os.Bundle;

import com.example.dronique.ui.main.Tab1Fragment;
import com.example.dronique.ui.main.Tab2Fragment;
import com.example.dronique.ui.main.Tab3Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.dronique.ui.main.SectionsPagerAdapter;

/**
 * Classe principal de l'application
 */
public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mTabAdapter;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    /**
     * Creation de l'activité
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Recupération de la vue
        mViewPager = findViewById(R.id.view_pager);
        TabLayout tabs = findViewById(R.id.tabs);

        // Création de l'adapter
        mTabAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mTabAdapter.addFragment(new Tab1Fragment(), "vue 1");
        mTabAdapter.addFragment(new Tab2Fragment(), "vue 2");
        mTabAdapter.addFragment(new Tab3Fragment(), "vue 3");

        // Ajout des tab a la vue
        mViewPager.setAdapter(mTabAdapter);
        tabs.setupWithViewPager(mViewPager);
    }
}