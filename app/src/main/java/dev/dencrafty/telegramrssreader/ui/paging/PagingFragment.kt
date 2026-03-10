package dev.dencrafty.telegramrssreader.ui.paging

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.dencrafty.telegramrssreader.R
import dev.dencrafty.telegramrssreader.databinding.FragmentPagingBinding
import dev.dencrafty.telegramrssreader.ui.paging.InvalidChannelDialogFragment.Companion.DIALOG_INVALID_CHANNEL
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PagingFragment : Fragment() {

    val args: PagingFragmentArgs by navArgs()
    private val viewModel: PagingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPagingBinding.inflate(layoutInflater)
        val list = binding.list

        val items = viewModel.pagingDataFlow
        val adapter = FeedAdapter(UserComparator)
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(list.context)
        val decoration = DividerItemDecoration(list.context, DividerItemDecoration.VERTICAL)
        list.addItemDecoration(decoration)

        if (savedInstanceState == null) {
            viewModel.requestChannelInfo(args.channelId)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    when (it) {
                        is PagingUIState.IndexSuccess ->
                            items.collectLatest { pagingData ->
                                adapter.submitData(pagingData)
                            }

                        is PagingUIState.InvalidChannelId ->
                            showInvalidChannelDialog()

                        is PagingUIState.Error ->
                            Toast.makeText(activity, it.exception.message, Toast.LENGTH_SHORT)
                                .show()

                        else -> Unit
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collect {
                    binding.prependProgress.isVisible = it.refresh is LoadState.Loading
                    binding.appendProgress.isVisible = it.append is LoadState.Loading
                }
            }
        }

        return binding.root
    }

    private fun showInvalidChannelDialog() {
        if (parentFragmentManager.findFragmentByTag(DIALOG_INVALID_CHANNEL) == null) {
            InvalidChannelDialogFragment().show(
                parentFragmentManager,
                DIALOG_INVALID_CHANNEL
            )
        }
    }
}

class InvalidChannelDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.invalid_channel_id))
            .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                dialog.dismiss()
            }
            .create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        parentFragment?.view?.findNavController()?.popBackStack()
        super.onDismiss(dialog)
    }

    companion object {
        const val DIALOG_INVALID_CHANNEL = "InvalidChannelDialog"
    }
}