package org.example.mqtt;

import java.util.Iterator;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	private List<Fragment> fragments;
	private FragmentManager fm;
	
	private final String TAG = "TabsPagerAdapter";
	
	
    public TabsPagerAdapter(FragmentManager fm,List<Fragment> fragments) {
        super(fm);
        this.fm = fm;
        this.fragments = fragments;
    }
	
	@Override
	public Fragment getItem(int index) {

	       return  this.fragments.get(index);
	}

	@Override
	public int getCount() {
		return this.fragments.size();
	}
	
	// 0 - Status, 1 Service, 2 Config (due to the order in which they have been added)
	public Fragment findFragmentByPosition(int position) {
	    return fm.findFragmentByTag(
	            "android:switcher:" + R.id.pager + ":"
	                    + this.getItemId(position));
	}
	
/*	public Fragment getVisibleFrag(){
		Iterator<Fragment> iterator = fragments.iterator();
		while (iterator.hasNext()) {
			Fragment f = iterator.next();
			if(f.isVisible()){
				Log.d(TAG, "fragment found is " + f.getTag() );
				return f;
			}
		}
		Log.d(TAG, "no visible fragment found");
		return null;
	}*/


}
