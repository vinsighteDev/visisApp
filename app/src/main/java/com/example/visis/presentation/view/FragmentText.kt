package com.example.visis.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.visis.R
import com.example.visis.presentation.viewmodel.ViewModel

class TextFragment : Fragment() {
    private var textViewModel: ViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        textViewModel = ViewModelProviders.of(this).get(ViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_text, container, false)
        val textView = root.findViewById<TextView>(R.id.text_text)
        textViewModel?.text?.observe(viewLifecycleOwner,
            Observer<String?> { s -> textView.text = s })
        return root
    }
}