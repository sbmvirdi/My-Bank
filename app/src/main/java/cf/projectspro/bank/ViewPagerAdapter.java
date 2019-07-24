package cf.projectspro.bank;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/*
* class created  by shubam virdi
* No Rights to reproduce,edit from unknown sources
* Social Stack Inc.
* All rights reserverd.
*
* */
class ViewPagerAdapter  extends FragmentPagerAdapter {
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: Dashboard dashboard = new Dashboard();
                return dashboard;
            case 1: Notifications notifications = new Notifications();
                return notifications;
            case 2:Profile profile = new Profile();
                return profile;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }
}
