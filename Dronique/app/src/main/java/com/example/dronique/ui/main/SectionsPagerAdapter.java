package com.example.dronique.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.dronique.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe permet de gérer les différentes vue
 * Permet de faire le lien entre vue et identifiant
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();


    /**
     * @param context   Context d'éxécution
     * @param fm        Gestionnaire de fragment
     */
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    /**
     * @param position Identifiant de la vue
     * @return  Le fragment initialisé corresponsant à la vue
     */
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }


    /**
     * @param fragment  Le fragment à ajouter
     * @param title     Le titre du fragment à ajouter
     */
    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }


    /**
     * @param position  Identifiant de la vue
     * @return  Le titre de la vue
     */
    @Override
    public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
    }


    /**
     * @return  Le nombre total de page
     */
    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}