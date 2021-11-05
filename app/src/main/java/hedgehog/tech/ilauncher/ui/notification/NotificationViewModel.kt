package hedgehog.tech.ilauncher.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val notificationRepo: NotificationRepo
) : ViewModel(){

    val mutableLiveData = NotificationRepo.mutableLiveData


    fun remove(position:Int) = viewModelScope.launch {
        notificationRepo.remove(position)
    }

}