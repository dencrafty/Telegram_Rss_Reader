package dev.dencrafty.telegramrssreader.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import dev.dencrafty.telegramrssreader.R
import dev.dencrafty.telegramrssreader.databinding.FragmentStartBinding

class StartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentStartBinding.inflate(layoutInflater)
        val startBtn = binding.start
        val channelField = binding.channel

        startBtn.setOnClickListener {
            navigateToPagingScreen(channelField)
        }

        channelField.setOnEditorActionListener { textView, action, event ->
            if (action == EditorInfo.IME_ACTION_DONE) {
                navigateToPagingScreen(textView as EditText)
            }
            false
        }

        return binding.root
    }

    private fun navigateToPagingScreen(channelField: EditText) {
        if (activity?.hasConnection() == true) {
            val action = StartFragmentDirections.actionStartFragmentToPagingFragment(
                channelField.text.toString().trim()
            )
            channelField.findNavController().navigate(action)
        } else {
            Toast.makeText(activity, R.string.check_internet, Toast.LENGTH_SHORT).show()
        }
    }
}