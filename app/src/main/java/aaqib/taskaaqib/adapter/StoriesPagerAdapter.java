package aaqib.taskaaqib.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import aaqib.taskaaqib.fragment.StoriesFragment;

/**
 * Adapter for the stories viewpager on the main screen
 */
public class StoriesPagerAdapter extends FragmentStatePagerAdapter {

    public StoriesPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return StoriesFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return StoriesFragment.getTypeCount();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return StoriesFragment.getType(position);
    }
}
