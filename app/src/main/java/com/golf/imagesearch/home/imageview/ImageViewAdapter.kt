package com.golf.imagesearch.home.imageview


import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.golf.imagesearch.base.BaseFragment

class ImageViewAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    var tabFragments = mutableListOf<BaseFragment<*, *>>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val pageIds= tabFragments.map { it.hashCode().toLong() }

    override fun getItemId(position: Int): Long {
        return tabFragments[position].hashCode().toLong()
    }

    fun removeView(index : Int) {
        tabFragments.removeAt(index)
        notifyItemRangeChanged(index, tabFragments.size)
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return tabFragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return tabFragments[position]
    }

    override fun containsItem(itemId: Long): Boolean {
        return pageIds.contains(itemId)
    }
}
