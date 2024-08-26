package com.example.smu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smu.databinding.FragmentChatMainBinding
import com.google.android.material.tabs.TabLayout

class FragmentChatMain : Fragment() {

    private lateinit var binding: FragmentChatMainBinding
    private lateinit var fragmentOne: Fragment
    private lateinit var fragmentGroup: Fragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatMainBinding.inflate(layoutInflater)

        fragmentOne = FragmentChat()
        fragmentGroup = FragmentChatGroup()

        replaceFragment(fragmentOne)

        val tabLayout = binding.chatMTablayout

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> replaceFragment(fragmentOne)
                    1 -> replaceFragment(fragmentGroup)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        return binding.root
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.chatM_framlayout, fragment)
            .commit()
    }
}