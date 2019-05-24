package com.example.criminalintent.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.criminalintent.R
import com.example.criminalintent.activity.CrimePagerActivity
import com.example.criminalintent.holder.CrimeHolder
import com.example.criminalintent.holder.CrimeHolderPolicy
import com.example.criminalintent.holder.BaseCrimeHolder
import com.example.criminalintent.model.Crime
import com.example.criminalintent.model.CrimeLab
import kotlinx.android.synthetic.main.fragment_crime_list.view.*
import java.util.*
import kotlin.collections.ArrayList

class CrimeListFragment : Fragment() {
    private var mAdapter:CrimeAdapter? = null
    private var mView:View? = null

    //分配一个值存储需要刷新的item的位置
    private var refreshPosition:Int = -1

    //标识subtitle是否需要显示，为真时不显示
    private var mSubtitleVisible = true

    companion object{
        const val REQUEST_CRIME = 1
        const val SAVED_SUBTITLE_VISIBLE = "subtitle"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.fragment_crime_list,container,false)
        mView = view
        view.crime_recycler_view.layoutManager = LinearLayoutManager(activity)
        if (savedInstanceState != null){
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE)
        }
        updateUI()
        return view
    }

    /**
     * subTitleItem?.title = R.string.hide_subtitle.toString()显示的是资源id
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list,menu)

        val subTitleItem = menu.findItem(R.id.show_subtitle)
        if (mSubtitleVisible){
            subTitleItem?.setTitle(R.string.hide_subtitle)
        }else{
            subTitleItem?.setTitle(R.string.show_subtitle)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE,mSubtitleVisible)
    }

    override fun onResume() {
        super.onResume()
        //暂定修改后直接返回更新item，后期调整
        updateUI()
        Log.d("RESUME","resume is successful and UI was updated")
    }

    private fun updateUI() {
        val crimeLab = activity?.let { CrimeLab.get(it) }
        val crimes = crimeLab?.getCrimes()
        if (mAdapter == null){
            mAdapter = CrimeAdapter(crimes!!)
            mView!!.crime_recycler_view.adapter = mAdapter
        }else{
            //之后可以修改单独更新指定item
            Log.i("refreshPosition", refreshPosition.toString())
            if (refreshPosition != -1){
                refreshPosition.let { mAdapter!!.notifyItemChanged(refreshPosition) }
            }else{
                //创建新的crime时调用刷新CrimeLab
                if (crimes != null) {
                    mAdapter!!.setCrimes(crimes)
                }
                mAdapter!!.notifyDataSetChanged()
            }
        }
        updateSubtitle()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            //启动编辑crime的界面
            //以后再crimeFragment中添加保存按钮(已存在的item则显示为修改,并增加一个删除按钮)
            R.id.new_crime->{
                val crime = Crime(UUID.randomUUID())
                activity?.let { CrimeLab.get(it).addCrime(crime) }
                val intent = activity?.let { CrimePagerActivity.newIntent(it,crime.mId) }
                startActivity(intent)
                true
            }
            R.id.show_subtitle -> {
                mSubtitleVisible = !mSubtitleVisible
                activity?.invalidateOptionsMenu()
                updateSubtitle()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateSubtitle(){
        val lab = activity?.let { CrimeLab.get(it) }
        //获取crime的数量
        val count = lab?.mCrimes!!.size
        var subtitle:String? = resources.getQuantityString(R.plurals.subtitle_plurals,count,count)
        if (!mSubtitleVisible){
            subtitle = null
        }
        (activity as AppCompatActivity).supportActionBar?.subtitle = subtitle
    }

    //之后把Adapter分离出去
    inner class CrimeAdapter(private var mCrimes : ArrayList<Crime>) : RecyclerView.Adapter<BaseCrimeHolder>() {
        override fun onBindViewHolder(holder: BaseCrimeHolder, position: Int) {
            val crime : Crime = mCrimes[position]
            holder.bind(crime)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseCrimeHolder {
            val inflater : LayoutInflater = LayoutInflater.from(activity)
            val holder: BaseCrimeHolder
            if (viewType == 1) {
                holder = CrimeHolder(inflater,parent)
            }else{
                holder = CrimeHolderPolicy(inflater,parent)
            }
            return holder
        }

        override fun getItemCount(): Int {
            Log.d("CRIME SIZE",mCrimes.size.toString())
            return mCrimes.size
        }

        fun setCrimes(crimes:ArrayList<Crime>){
            mCrimes = crimes
        }

        override fun getItemViewType(position: Int): Int {
            return if (!mCrimes[position].mRequiresPolice)
                1
            else
                0
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //直接点击返回会导致resultCode为RESULT_CANCEL
        //暂时使用直接返回来实现item数据更新后的加载
        if (data != null) {
            refreshPosition = if (requestCode == REQUEST_CRIME){
                data.getIntExtra("position",-1)
            }else{
                -1
            }
        }
        Log.d("CallBack", refreshPosition.toString())
    }
}
