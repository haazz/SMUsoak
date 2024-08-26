package com.example.smu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smu.databinding.FragmentChatGroupBinding

class FragmentChatGroup : Fragment() {

    private lateinit var binding: FragmentChatGroupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatGroupBinding.inflate(layoutInflater)

        return binding.root
    }
}