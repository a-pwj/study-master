package pwj.study

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main_recycler.*

class RecyclerMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_recycler)

        btn1.setOnClickListener { startActivity(Intent(this, RecyclerHeaderFooterActivity::class.java)) }
//        btn2.setOnClickListener { startActivity(Intent(this, XRecyclerViewActivity::class.java)) }
        btn3.setOnClickListener { startActivity(Intent(this, RecyclerDragActivity::class.java)) }
    }
}
