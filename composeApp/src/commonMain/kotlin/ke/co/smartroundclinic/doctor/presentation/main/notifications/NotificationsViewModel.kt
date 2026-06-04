package ke.co.smartroundclinic.doctor.presentation.main.notifications

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.co.smartroundclinic.doctor.common.Resource
import ke.co.smartroundclinic.doctor.domain.model.Notification
import ke.co.smartroundclinic.doctor.domain.usecase.notification.GetMyNotificationsUseCase
import ke.co.smartroundclinic.doctor.domain.usecase.notification.MarkNotificationReadUseCase
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val getMyNotificationsUseCase: GetMyNotificationsUseCase,
    private val markReadUseCase: MarkNotificationReadUseCase,
) : ViewModel() {

    var notifications by mutableStateOf<List<Notification>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    val unreadCount: Int get() = notifications.count { it.isUnread }

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            isLoading = true
            when (val result = getMyNotificationsUseCase()) {
                is Resource.Success -> notifications = result.data
                    ?.sortedByDescending { it.createdAt }
                    ?: emptyList()
                else -> {}
            }
            isLoading = false
        }
    }

    fun markRead(id: String) {
        viewModelScope.launch {
            markReadUseCase(id)
            notifications = notifications.map { n ->
                if (n.id == id) n.copy(status = "READ") else n
            }
        }
    }

    fun markAllRead() {
        val unread = notifications.filter { it.isUnread }
        if (unread.isEmpty()) return
        notifications = notifications.map { it.copy(status = "READ") }
        viewModelScope.launch {
            unread.forEach { markReadUseCase(it.id) }
        }
    }
}
