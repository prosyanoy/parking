package sbs.pros.parking.menu

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(): ViewModel() {

    private val _title = MutableStateFlow("111111")
    val title = _title.asStateFlow()

    fun setTitle(title: String){
        _title.value = title
    }

}