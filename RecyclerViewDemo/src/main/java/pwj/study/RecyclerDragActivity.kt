package pwj.study

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_recycler_drag.*
import pwj.study.bean.Subject
import pwj.study.drag.*
import pwj.study.drag.swipe.SwipeSimpleRecyclerView
import java.util.*

class RecyclerDragActivity : AppCompatActivity() {

    private val titles = arrayOf("美食", "电影", "酒店住宿", "休闲娱乐", "外卖", "自助餐", "KTV", "机票/火车票", "周边游", "美甲美睫", "火锅", "生日蛋糕", "甜品饮品", "水上乐园", "汽车服务", "美发", "丽人", "景点", "足疗按摩", "运动健身", "健身", "超市", "买菜", "今日新单", "小吃快餐", "面膜", "洗浴/汗蒸", "母婴亲子", "生活服务", "婚纱摄影", "学习培训", "家装", "结婚", "全部分配")
    private val datas = ArrayList<Subject>()
    private var mItemTouchHelper: ItemTouchHelper? = null
    private var adapter: DragAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_drag)
        initData()
        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_drag, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.option_normal_1 -> {
                recyclerView.layoutManager = LinearLayoutManager(this@RecyclerDragActivity)
                val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST)
                recyclerView.removeItemDecoration(itemDecoration)
                recyclerView.addItemDecoration(itemDecoration)
                true
            }
            R.id.option_normal_2 -> {
                recyclerView.layoutManager = GridLayoutManager(this, 4)
                val itemDecoration = DividerGridItemDecoration(this)
                recyclerView.removeItemDecoration(itemDecoration)
                recyclerView.addItemDecoration(itemDecoration)
                true
            }
            R.id.option_normal_3 -> true
            R.id.option_normal_4 -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initData() {
        for (i in titles.indices) {
            //动态获取资源ID，第一个参数是资源名，第二个参数是资源类型例如drawable，string等，第三个参数包名
//            val imageId = resources.getIdentifier("ic_category_$i", "mipmap", packageName)
            val imageId = resources.getIdentifier("ic_category_0", "mipmap", packageName)
            datas.add(Subject(titles[i], imageId))
        }
    }

    private fun initView() {
        recyclerView.layoutManager = LinearLayoutManager(this@RecyclerDragActivity)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST))
        adapter = DragAdapter(datas, this@RecyclerDragActivity)
        recyclerView.adapter = adapter
        val simpleItemDragHelper = SimpleItemDragHelper()
        simpleItemDragHelper.setOnChannelListener(object : OnChannelListener {
            val builder = StringBuilder()

            override fun onFinish() {
                val dialog = SimpleDialog.getInstance(builder.toString())
                if (!dialog.isAdded && (fragmentManager.findFragmentByTag("SimpleDialog") == null)) {
                    dialog.show(this@RecyclerDragActivity.fragmentManager, "SimpleDialog")
                }
            }

            override fun onItemMove(starPos: Int, endPos: Int) {
                if (starPos < endPos) {
                    for (i in starPos until endPos) {
                        Collections.swap(datas, i, i + 1)
                        builder.append(datas[i].toString())
                    }
                } else {
                    for (i in starPos downTo endPos + 1) {
                        Collections.swap(datas, i, i - 1)
                        builder.append(datas[i].toString())
                    }
                }
            }
        })

        mItemTouchHelper = ItemTouchHelper(simpleItemDragHelper)
        mItemTouchHelper!!.attachToRecyclerView(recyclerView)
        recyclerView.addOnItemTouchListener(object : OnRecyclerItemClickListener(recyclerView) {
            override fun onItemClick(viewHolder: RecyclerView.ViewHolder) {

            }

            override fun onItemLongClick(viewHolder: RecyclerView.ViewHolder) {
                //长按拖拽
                if (viewHolder.layoutPosition % 2 == 0) {//筛选条件
                    mItemTouchHelper!!.startDrag(viewHolder)
                }
            }
        })

        recyclerView.setRightClickListener(object : SwipeSimpleRecyclerView.OnRightClickListener {
            override fun onRightClick(position: Int, id: String) {
                datas.removeAt(position)
                // myAdapter.notifyItemRemoved(position);
                adapter!!.notifyDataSetChanged()
                Toast.makeText(this@RecyclerDragActivity, " position = $position", Toast.LENGTH_SHORT).show()
            }

        })
    }
}