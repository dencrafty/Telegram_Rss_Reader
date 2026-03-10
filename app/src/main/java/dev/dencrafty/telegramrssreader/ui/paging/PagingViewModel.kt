package dev.dencrafty.telegramrssreader.ui.paging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dencrafty.telegramrssreader.data.paging.FeedPagingSource
import dev.dencrafty.telegramrssreader.data.paging.PAGE_SIZE
import dev.dencrafty.telegramrssreader.data.repository.Repository
import dev.dencrafty.telegramrssreader.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PagingViewModel @Inject constructor(
    private val repository: Repository,
    private val pagingSource: FeedPagingSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState: MutableStateFlow<PagingUIState?> = MutableStateFlow(null)
    val uiState: StateFlow<PagingUIState?> get() = _uiState.asStateFlow()

    val pagingDataFlow =
        Pager(PagingConfig(pageSize = PAGE_SIZE)) { pagingSource }.flow.cachedIn(viewModelScope)

    /*
    Здесь получаю через репозиторий индекс канала. Обновляю инстанс pagingSource и уведомляю UI для
    дальнейшей работы UI с Paging3 api.
     */
    fun requestChannelInfo(channelId: String) {
        viewModelScope.launch(ioDispatcher) {
            try {
                val channel = repository.channelInfo(channelId)
                _uiState.update {
                    pagingSource.channel = channel
                    if (channel.size != 0) {
                        PagingUIState.IndexSuccess
                    } else {
                        PagingUIState.InvalidChannelId
                    }
                }
            } catch (ioe: IOException) {
                _uiState.update {
                    PagingUIState.Error(ioe)
                }
            }
        }
    }
}