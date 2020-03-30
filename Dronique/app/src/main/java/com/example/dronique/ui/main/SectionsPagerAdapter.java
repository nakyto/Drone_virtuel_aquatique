package com.example.dronique.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.dronique.R;

/**
 * Cette classe permet de gérer les différentes vue
 * Permet de faire le lien entre vue et identifiant
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Context mContext;


    /**
     * @param context   Context d'éxécution
     * @param fm        Gestionnaire de fragment
     */
    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }


    /**
     * @param position Identifiant de la vue
     * @return  Le fragment initialisé corresponsant à la vue
     */
    @Override
    public Fragment getItem(int position) {
        return MapFragment.newInstance(position + 1);
    }


    /**
     * @param position  Identifiant de la vue
     * @return  Le titre de la vue
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }


    /**
     * @return  Le nombre total de page
     */
    @Override
    public int getCount() {
        return 3;
    }
}