package com.example.smartcallblocker.ui.history

import com.example.smartcallblocker.data.local.entity.BlockedCallLogEntity
import com.example.smartcallblocker.data.repository.FakeCallBlockerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CallHistoryViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeCallBlockerRepository
    private lateinit var viewModel: CallHistoryViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeCallBlockerRepository()
        viewModel = CallHistoryViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun subscribeToViewModelFlows(scope: kotlinx.coroutines.CoroutineScope) {
        scope.launch { viewModel.blockedCallLogs.collect {} }
        scope.launch { viewModel.showClearConfirmDialog.collect {} }
    }

    @Test
    fun `blockedCallLogs emits logs added to repository`() = runTest {
        subscribeToViewModelFlows(backgroundScope)

        assertEquals(0, viewModel.blockedCallLogs.value.size)

        val log1 = BlockedCallLogEntity(
            phoneNumber = "+447911123456",
            timestamp = System.currentTimeMillis(),
            blockReason = "Country Code (+44)"
        )
        val log2 = BlockedCallLogEntity(
            phoneNumber = "18005550199",
            timestamp = System.currentTimeMillis(),
            blockReason = "Prefix (1800)"
        )

        repository.addBlockedCallLog(log1)
        repository.addBlockedCallLog(log2)

        val logs = viewModel.blockedCallLogs.value
        assertEquals(2, logs.size)
        assertEquals("+447911123456", logs[0].phoneNumber)
        assertEquals("18005550199", logs[1].phoneNumber)
    }

    @Test
    fun `showClearConfirmationDialog and dismissClearConfirmationDialog updates state`() = runTest {
        subscribeToViewModelFlows(backgroundScope)

        assertFalse(viewModel.showClearConfirmDialog.value)

        viewModel.showClearConfirmationDialog()
        assertTrue(viewModel.showClearConfirmDialog.value)

        viewModel.dismissClearConfirmationDialog()
        assertFalse(viewModel.showClearConfirmDialog.value)
    }

    @Test
    fun `clearAllLogs clears all blocked call logs in repository and closes dialog`() = runTest {
        subscribeToViewModelFlows(backgroundScope)

        val log1 = BlockedCallLogEntity(
            phoneNumber = "+447911123456",
            timestamp = System.currentTimeMillis(),
            blockReason = "Country Code (+44)"
        )
        repository.addBlockedCallLog(log1)

        assertEquals(1, viewModel.blockedCallLogs.value.size)

        viewModel.showClearConfirmationDialog()
        assertTrue(viewModel.showClearConfirmDialog.value)

        viewModel.clearAllLogs()

        assertEquals(0, viewModel.blockedCallLogs.value.size)
        assertFalse(viewModel.showClearConfirmDialog.value)
    }

    @Test
    fun `deleteLog removes specific log entry from repository`() = runTest {
        subscribeToViewModelFlows(backgroundScope)

        val log1 = BlockedCallLogEntity(
            phoneNumber = "+447911123456",
            timestamp = System.currentTimeMillis(),
            blockReason = "Country Code (+44)"
        )
        val log2 = BlockedCallLogEntity(
            phoneNumber = "18005550199",
            timestamp = System.currentTimeMillis(),
            blockReason = "Prefix (1800)"
        )

        val id1 = repository.addBlockedCallLog(log1)
        val id2 = repository.addBlockedCallLog(log2)

        val logsBefore = viewModel.blockedCallLogs.value
        assertEquals(2, logsBefore.size)

        val logToDelete = logsBefore.first { it.id == id1 }
        viewModel.deleteLog(logToDelete)

        val logsAfter = viewModel.blockedCallLogs.value
        assertEquals(1, logsAfter.size)
        assertEquals(id2, logsAfter[0].id)
        assertEquals("18005550199", logsAfter[0].phoneNumber)
    }
}
