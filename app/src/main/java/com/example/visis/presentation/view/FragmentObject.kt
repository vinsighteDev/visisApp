package com.example.visis.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.visis.R
import com.example.visis.presentation.viewmodel.ViewModel

class ObjectFragment : Fragment() {
    private var objectViewModel: ViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        objectViewModel = ViewModelProviders.of(this).get(ViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_object, container, false)
        objectViewModel?.text?.observe(viewLifecycleOwner, Observer<String?> { })
        return root
    }
}