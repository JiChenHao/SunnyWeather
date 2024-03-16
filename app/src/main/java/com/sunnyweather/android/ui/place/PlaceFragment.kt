package com.sunnyweather.android.ui.place

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.android.R
import com.sunnyweather.android.databinding.FragmentPlaceBinding
import com.sunnyweather.android.logic.model.Place

class PlaceFragment : Fragment() {
    //懒加载技术
    val viewModel by lazy { ViewModelProvider(this).get(PlaceViewModel::class.java) }
    private lateinit var adapter: PlaceAdapter
    private lateinit var mView: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //这里加载了fragment_place布局
        mView = inflater.inflate(R.layout.fragment_place, container, false)
        return mView
    }


    @SuppressLint("FragmentLiveDataObserve")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (mView != null) {
            val recyclerView: RecyclerView = mView.findViewById(R.id.recyclerView)
            val searchPlaceEdit: EditText = mView.findViewById(R.id.searchPlaceEdit)
            val bgImageView: ImageView = mView.findViewById(R.id.bgImageView)
            val layoutManager = LinearLayoutManager(activity)
            adapter = PlaceAdapter(this, viewModel.placeList)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
            searchPlaceEdit.addTextChangedListener { editable ->
                //获取搜索框内的文字
                val content = editable.toString()
                //如果文字不为空，就调用搜索方法去调用API查看结果
                if (content.isNotEmpty()) {
                    viewModel.searchPlaces(content)
                } else {
                    //如果文字为空，就清空已有的缓存数据，显示结果为空，隐藏结果视图，仅显示用于美观的背景
                    recyclerView.visibility = View.GONE
                    bgImageView.visibility = View.VISIBLE
                    viewModel.placeList.clear()
                    adapter.notifyDataSetChanged()
                }
            }
            //借助liveData来获取服务器响应的数据，对placeLiveData对象进行观察，有任何数据变化，就会回调到传入的Observe接口中
            //然后对数据进行判断
            viewModel.placeLiveData.observe(this, Observer { result: Result<List<Place>> ->
                val places = result.getOrNull()
                if (places != null) {
                    recyclerView.visibility = View.VISIBLE
                    bgImageView.visibility = View.GONE
                    viewModel.placeList.clear()
                    viewModel.placeList.addAll(places)
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                    result.exceptionOrNull()?.printStackTrace()
                }
            })
        }


    }
}