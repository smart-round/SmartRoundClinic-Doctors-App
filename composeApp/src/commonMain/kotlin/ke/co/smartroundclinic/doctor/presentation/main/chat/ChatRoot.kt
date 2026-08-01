package ke.co.smartroundclinic.doctor.presentation.main.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ke.co.smartroundclinic.doctor.core.notification.OutgoingCallState
import ke.co.smartroundclinic.doctor.core.notification.OutgoingCallStatus
import ke.co.smartroundclinic.doctor.presentation.main.bookings.MedicalRecordViewModel
import ke.co.smartroundclinic.doctor.presentation.main.chat.destinations.Call
import ke.co.smartroundclinic.doctor.presentation.main.chat.destinations.ChatList
import ke.co.smartroundclinic.doctor.presentation.main.chat.destinations.Conversation
import ke.co.smartroundclinic.doctor.presentation.main.chat.destinations.OutgoingCall
import ke.co.smartroundclinic.doctor.presentation.main.chat.otherdoctors.DoctorChatViewModel
import ke.co.smartroundclinic.doctor.presentation.main.chat.otherdoctors.DoctorConversationScreen
import ke.co.smartroundclinic.doctor.presentation.main.chat.otherdoctors.DoctorOutgoingCallStatus
import ke.co.smartroundclinic.doctor.presentation.main.chat.otherdoctors.destinations.DoctorCall
import ke.co.smartroundclinic.doctor.presentation.main.chat.otherdoctors.destinations.DoctorConversation
import ke.co.smartroundclinic.doctor.presentation.main.chat.otherdoctors.destinations.OutgoingDoctorCall
import ke.co.smartroundclinic.doctor.presentation.main.chat.ui.CallScreen
import ke.co.smartroundclinic.doctor.presentation.main.chat.ui.ChatListScreen
import ke.co.smartroundclinic.doctor.presentation.main.chat.ui.ChatTopTab
import ke.co.smartroundclinic.doctor.presentation.main.chat.ui.ConversationScreen
import ke.co.smartroundclinic.doctor.presentation.main.chat.ui.OutgoingCallScreen
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatRoot(
    modifier: Modifier = Modifier,
    onAtRootChanged: (Boolean) -> Unit = {},
    pendingConversation: Conversation? = null,
    onPendingNavigated: () -> Unit = {},
    pendingCall: Call? = null,
    onPendingCallNavigated: () -> Unit = {},
    pendingDoctorChatDoctorId: String? = null,
    onPendingDoctorChatNavigated: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
) {
    val backStack = retain { mutableStateListOf<NavKey>(ChatList) }
    val isAtRoot = backStack.size == 1
    val vm: ConsultationViewModel = koinViewModel()
    val medicalRecordVm: MedicalRecordViewModel = koinViewModel()
    val doctorChatVm: DoctorChatViewModel = koinViewModel()

    var chatTopTab by retain { mutableStateOf(ChatTopTab.CONSULTATIONS) }
    var isSearchingDoctors by retain { mutableStateOf(false) }
    var doctorSearchQuery by retain { mutableStateOf("") }

    SideEffect { onAtRootChanged(isAtRoot) }

    // Services tab's "Chat" CTA (Flow 3) lands here — find-or-create the thread then jump straight
    // into it, same funnel as starting a chat from the Other Doctors search box.
    LaunchedEffect(pendingDoctorChatDoctorId) {
        val doctorId = pendingDoctorChatDoctorId ?: return@LaunchedEffect
        chatTopTab = ChatTopTab.OTHER_DOCTORS
        backStack.removeAll { it is DoctorConversation }
        doctorChatVm.startChatWith(doctorId) { thread ->
            backStack.add(DoctorConversation(thread.threadId, thread.counterpartName, thread.counterpartPicture))
        }
        onPendingDoctorChatNavigated()
    }

    // The chat list has no socket of its own — keep its online/last-seen previews live by polling
    // only while it's the visible screen (stop once a specific conversation is opened).
    LaunchedEffect(isAtRoot) {
        if (isAtRoot) vm.startThreadsPolling() else vm.stopThreadsPolling()
    }

    LaunchedEffect(pendingConversation) {
        if (pendingConversation != null) {
            backStack.removeAll { it is Conversation }
            backStack.add(pendingConversation)
            onPendingNavigated()
        }
    }

    // Fires after the pendingConversation effect above (both are set together from the same
    // notification event), so Conversation is already on the stack for Call's entry to read
    // the patient name/picture off of.
    LaunchedEffect(pendingCall) {
        if (pendingCall != null) {
            backStack.removeAll { it is Call }
            backStack.add(pendingCall)
            onPendingCallNavigated()
        }
    }

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<ChatList> {
                ChatListScreen(
                    threads = vm.threads,
                    onThreadClick = { thread ->
                        backStack.add(Conversation(thread.patientId, thread.counterpartName, thread.latestAppointmentId))
                    },
                    onDeleteThread = { thread -> vm.deleteThread(thread.doctorId, thread.patientId) },
                    selectedTopTab = chatTopTab,
                    onTopTabSelected = { chatTopTab = it },
                    doctorThreads = doctorChatVm.threads,
                    isSearchingDoctors = isSearchingDoctors,
                    onToggleDoctorSearch = {
                        isSearchingDoctors = !isSearchingDoctors
                        if (isSearchingDoctors) doctorChatVm.loadSearchableDoctors() else doctorSearchQuery = ""
                    },
                    doctorSearchQuery = doctorSearchQuery,
                    onDoctorSearchQueryChange = { doctorSearchQuery = it },
                    doctorSearchResults = doctorChatVm.searchableDoctors,
                    isLoadingDoctorSearch = doctorChatVm.isLoadingSearchableDoctors,
                    onDoctorThreadClick = { thread ->
                        backStack.add(DoctorConversation(thread.threadId, thread.counterpartName, thread.counterpartPicture))
                    },
                    onDoctorResultClick = { doctor ->
                        doctorChatVm.startChatWith(doctor.id) { thread ->
                            backStack.add(DoctorConversation(thread.threadId, thread.counterpartName, thread.counterpartPicture))
                        }
                    },
                    onProfileClick = onProfileClick,
                    onNotificationsClick = onNotificationsClick,
                )
            }
            entry<DoctorConversation> { dest ->
                LaunchedEffect(dest.threadId) {
                    doctorChatVm.connectToThread(dest.threadId)
                    doctorChatVm.loadHistory(dest.threadId)
                }
                DoctorConversationScreen(
                    counterpartName = dest.counterpartName,
                    counterpartPicture = dest.counterpartPicture,
                    messages = doctorChatVm.messages,
                    isLoadingHistory = doctorChatVm.isLoadingHistory,
                    isLoadingMoreHistory = doctorChatVm.isLoadingMoreHistory,
                    hasMoreHistory = doctorChatVm.hasMoreHistory,
                    onLoadMoreHistory = { doctorChatVm.loadMoreHistory(dest.threadId) },
                    isConnected = doctorChatVm.isConnected,
                    isUploadingFile = doctorChatVm.isUploadingFile,
                    pendingFiles = doctorChatVm.pendingFiles,
                    currentUserId = doctorChatVm.currentUserId,
                    incomingCall = doctorChatVm.incomingCall,
                    onAcceptIncomingCall = {
                        val call = doctorChatVm.incomingCall ?: return@DoctorConversationScreen
                        doctorChatVm.clearIncomingCall()
                        backStack.add(DoctorCall(dest.threadId, isVideo = call.isVideo))
                    },
                    onDeclineIncomingCall = { doctorChatVm.declineIncomingCall(dest.threadId) },
                    onBack = {
                        doctorChatVm.disconnect()
                        backStack.removeLastOrNull()
                    },
                    onVoiceCall = { backStack.add(OutgoingDoctorCall(dest.threadId, dest.counterpartName, isVideo = false)) },
                    onVideoCall = { backStack.add(OutgoingDoctorCall(dest.threadId, dest.counterpartName, isVideo = true)) },
                    onSendText = doctorChatVm::sendText,
                    onSendFile = { fileName, contentType, bytes -> doctorChatVm.sendFile(dest.threadId, fileName, contentType, bytes) },
                )
            }
            entry<OutgoingDoctorCall> { dest ->
                LaunchedEffect(dest.threadId) {
                    doctorChatVm.startCall(dest.threadId, dest.isVideo)
                }
                DisposableEffect(dest.threadId) {
                    onDispose {
                        val status = doctorChatVm.outgoingCallStatus
                        if (status is DoctorOutgoingCallStatus.Calling) {
                            doctorChatVm.cancelOutgoingCall(dest.threadId, status.callId)
                        }
                    }
                }

                val status = doctorChatVm.outgoingCallStatus
                LaunchedEffect(status) {
                    when (val s = status) {
                        is DoctorOutgoingCallStatus.Answered -> {
                            doctorChatVm.clearOutgoingCallStatus()
                            backStack.removeLastOrNull()
                            backStack.add(DoctorCall(dest.threadId, isVideo = dest.isVideo))
                        }
                        is DoctorOutgoingCallStatus.Declined -> {
                            doctorChatVm.clearOutgoingCallStatus()
                            backStack.removeLastOrNull()
                        }
                        is DoctorOutgoingCallStatus.Calling -> {
                            // Mirrors the backend's RedisKeys.CALL_INVITE_TTL_SECONDS ring timeout.
                            delay(45_000L)
                            if (doctorChatVm.outgoingCallStatus == s) {
                                doctorChatVm.cancelOutgoingCall(dest.threadId, s.callId)
                                backStack.removeLastOrNull()
                            }
                        }
                        null -> Unit
                    }
                }

                OutgoingCallScreen(
                    calleeName = dest.calleeName,
                    statusText = when (status) {
                        is DoctorOutgoingCallStatus.Declined -> "Call declined"
                        else -> "Calling…"
                    },
                    onCancel = {
                        val s = doctorChatVm.outgoingCallStatus
                        if (s is DoctorOutgoingCallStatus.Calling) doctorChatVm.cancelOutgoingCall(dest.threadId, s.callId)
                        else doctorChatVm.clearOutgoingCallStatus()
                        backStack.removeLastOrNull()
                    },
                )
            }
            entry<DoctorCall> { dest ->
                val callConversation = backStack.filterIsInstance<DoctorConversation>().firstOrNull()
                val counterpartName = callConversation?.counterpartName ?: "Doctor"
                val counterpartPicture = callConversation?.counterpartPicture
                CallScreen(
                    participantName = counterpartName,
                    participantPicture = counterpartPicture,
                    selfPicture = doctorChatVm.currentUserProfilePicture,
                    isVideo = dest.isVideo,
                    joinState = doctorChatVm.callJoinState,
                    onJoin = { doctorChatVm.joinCall(dest.threadId) },
                    onEnd = {
                        doctorChatVm.endCall(dest.threadId)
                        backStack.removeLastOrNull()
                    },
                )
            }
            entry<Conversation> { dest ->
                LaunchedEffect(dest.patientId) {
                    vm.connectToThread(dest.patientId)
                    vm.loadNextAppointment(dest.patientId)
                    medicalRecordVm.loadPatientBio(dest.patientId)
                    medicalRecordVm.loadPatientHistory(dest.patientId)
                }
                // currentUserId loads asynchronously from Room — re-key on it so that if this fires
                // before it's populated (cold start / fresh login), it retries once the id is ready
                // instead of calling loadMergedHistory with an empty doctorId forever.
                LaunchedEffect(dest.patientId, vm.currentUserId) {
                    if (vm.currentUserId.isNotBlank()) {
                        vm.loadMergedHistory(vm.currentUserId, dest.patientId)
                    }
                }
                val appointment = vm.appointments.firstOrNull { it.id == dest.latestAppointmentId }
                val patientPicture = vm.threads.firstOrNull { it.patientId == dest.patientId }?.counterpartPicture
                    ?: appointment?.patientProfilePicture
                ConversationScreen(
                    patientName = dest.patientName,
                    patientPicture = patientPicture,
                    appointment = vm.nextAppointment,
                    onLockedCallClick = { vm.notifyCallLocked(it) },
                    messages = vm.messages,
                    isLoadingHistory = vm.isLoadingHistory,
                    isLoadingMoreHistory = vm.isLoadingMoreHistory,
                    hasMoreHistory = vm.hasMoreHistory,
                    onLoadMoreHistory = { vm.loadMoreHistory(vm.currentUserId, dest.patientId) },
                    isConnected = vm.isConnected,
                    isUploadingFile = vm.isUploadingFile,
                    pendingFiles = vm.pendingFiles,
                    currentUserId = vm.currentUserId,
                    patientBio = medicalRecordVm.patientBio,
                    patientHistory = medicalRecordVm.patientHistory.toList(),
                    otherPartyTyping = vm.otherPartyTyping,
                    otherPartyOnline = vm.otherPartyOnline,
                    otherPartyLastSeenAt = vm.otherPartyLastSeenAt,
                    onTyping = vm::sendTypingEvent,
                    onBack = {
                        vm.disconnect()
                        backStack.removeLastOrNull()
                    },
                    onVoiceCall = { backStack.add(OutgoingCall(dest.patientId, dest.patientName, isVideo = false)) },
                    onVideoCall = { backStack.add(OutgoingCall(dest.patientId, dest.patientName, isVideo = true)) },
                    onSendText = vm::sendText,
                    onSendFile = vm::sendFile,
                )
            }
            entry<OutgoingCall> { dest ->
                LaunchedEffect(dest.otherUserId) {
                    vm.startCall(dest.otherUserId, dest.isVideo, dest.calleeName)
                }
                DisposableEffect(dest.otherUserId) {
                    onDispose {
                        val status = OutgoingCallState.current.value
                        if (status is OutgoingCallStatus.Calling && status.otherUserId == dest.otherUserId) {
                            vm.cancelOutgoingCall(dest.otherUserId, status.callId)
                        }
                    }
                }

                val status by OutgoingCallState.current.collectAsState()
                LaunchedEffect(status) {
                    when (val s = status) {
                        is OutgoingCallStatus.Answered -> {
                            OutgoingCallState.clear()
                            backStack.removeLastOrNull()
                            backStack.add(Call(dest.otherUserId, isVideo = dest.isVideo))
                        }
                        is OutgoingCallStatus.Declined -> {
                            OutgoingCallState.clear()
                            backStack.removeLastOrNull()
                        }
                        is OutgoingCallStatus.Calling -> {
                            // Ring timeout mirrors the backend's RedisKeys.CALL_INVITE_TTL_SECONDS —
                            // if nobody answers in time, cancel on the caller's own initiative too.
                            delay(45_000L)
                            if (OutgoingCallState.current.value == s) {
                                vm.cancelOutgoingCall(dest.otherUserId, s.callId)
                                backStack.removeLastOrNull()
                            }
                        }
                        null -> Unit
                    }
                }

                OutgoingCallScreen(
                    calleeName = dest.calleeName,
                    statusText = when (status) {
                        is OutgoingCallStatus.Declined -> "Call declined"
                        else -> "Calling…"
                    },
                    onCancel = {
                        val s = OutgoingCallState.current.value
                        if (s is OutgoingCallStatus.Calling) vm.cancelOutgoingCall(dest.otherUserId, s.callId)
                        else OutgoingCallState.clear()
                        backStack.removeLastOrNull()
                    },
                )
            }
            entry<Call> { dest ->
                val callConversation = backStack.filterIsInstance<Conversation>().firstOrNull()
                val patientName = callConversation?.patientName ?: "Patient"
                val patientPicture = callConversation?.let { c ->
                    vm.threads.firstOrNull { it.patientId == c.patientId }?.counterpartPicture
                        ?: vm.appointments.firstOrNull { it.id == c.latestAppointmentId }?.patientProfilePicture
                }
                CallScreen(
                    participantName = patientName,
                    participantPicture = patientPicture,
                    selfPicture = vm.currentUserProfilePicture,
                    isVideo = dest.isVideo,
                    joinState = vm.callJoinState,
                    onJoin = { vm.joinCall(dest.otherUserId) },
                    onEnd = {
                        vm.endCall()
                        backStack.removeLastOrNull()
                    },
                )
            }
        },
    )
}
