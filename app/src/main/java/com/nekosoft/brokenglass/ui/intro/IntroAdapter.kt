package com.nekosoft.brokenglass.ui.intro

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class IntroAdapter(showFragment4: Boolean, Fa: IntroActivity, listener: (Int) -> Unit) :
    FragmentStateAdapter(Fa) {
    private val dataFragments = mutableListOf(
            Intro0Fragment.newInstance(listener),
            Intro1Fragment.newInstance(listener),
            Intro2Fragment.newInstance(listener),
            Intro3Fragment.newInstance(listener)
        )


    override fun getItemCount(): Int = dataFragments.size

    override fun createFragment(position: Int): Fragment = dataFragments[position]

    fun getFragment(position: Int): Fragment {
        return dataFragments[position]
    }
}