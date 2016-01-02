package com.jcefinal.itamarsh.persontoperson;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jcefinal.itamarsh.persontoperson.MainScreenActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> FragmentList = new ArrayList();
    private final List<String> FragmentTitles = new ArrayList();
    private int tabCount = 2;
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return MainScreenActivity.PlaceholderFragment.newInstance(position + 1);
    }
    public void addFragment(Fragment fragment) {
        FragmentList.add(fragment);
    }
    public void setTabCount(int num){
        this.tabCount = num;
    }
    @Override
    public int getCount() {
        return tabCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Contacts";
            case 1:
                return "Search";
        }
        return null;
    }
}
