package pwj.study.headerfooter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter

class SimplePagerAdapter(fragmentManager: FragmentManager,
                         private val mFragments: List<Fragment>,
                         private val mTitles: Array<String>) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(p0: Int): Fragment {
        return mFragments[p0]
    }

    override fun getCount(): Int {
        return mFragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mTitles[position]
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }
}