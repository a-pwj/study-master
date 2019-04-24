package pwj.study

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_recycler_header_footer.*
import pwj.study.headerfooter.OnFragmentInteractionListener
import pwj.study.headerfooter.RecyclerHeadFootFragment
import pwj.study.headerfooter.SimplePagerAdapter
import java.util.*

class RecyclerHeaderFooterActivity : AppCompatActivity(), OnFragmentInteractionListener {

    private val mFragments = ArrayList<Fragment>()
    private val mTitles: Array<String> = arrayOf("正在投票", "即将投票", "往期投票")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_header_footer)

        initTab()

        mFragments.add(RecyclerHeadFootFragment.newInstance(1))
        mFragments.add(RecyclerHeadFootFragment.newInstance(2))
        mFragments.add(RecyclerHeadFootFragment.newInstance(3))

        viewpager.adapter = SimplePagerAdapter(supportFragmentManager, mFragments, mTitles)
        viewpager.offscreenPageLimit = tabLayout.tabCount
        tabLayout.setupWithViewPager(viewpager)//绑定
    }

    private fun initTab() {
//        tabLayout.addTab(tabLayout.newTab().setText("选项卡一").setIcon(R.mipmap.ic_launcher));
        //自定义tab
        for (i in mTitles.indices) {
            val view: View = View.inflate(this, R.layout.tablayout_diy, null)
            val name: TextView = view.findViewById(R.id.tab_name)
            val num: TextView = view.findViewById(R.id.tab_num)
            name.text = mTitles[i]
            num.text = i.toString()
            val tab: TabLayout.Tab = tabLayout.newTab()
            tab.customView = view
            tabLayout.addTab(tab)
        }
    }

    override fun onFragmentInteraction() {
        //与fragment 调用
    }
}
