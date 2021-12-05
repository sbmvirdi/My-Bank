package cf.projectspro.bank.ui.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import cf.projectspro.bank.ui.fragments.Dashboard;
import cf.projectspro.bank.ui.fragments.Notifications;
import cf.projectspro.bank.ui.fragments.Profile;

/*
 * class created  by shubam virdi
 * No Rights to reproduce,edit from unknown sources
 * Social Stack Dev.
 * All rights reserved.
 *
 * */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Dashboard();
            case 1:
                return new Notifications();
            case 2:
                return new Profile();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }
}
