package com.example.smartcallblocker.ui.settings

import com.example.smartcallblocker.data.model.NotificationPreference
import com.example.smartcallblocker.data.repository.FakeCallBlockerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeCallBlockerRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeCallBlockerRepository()
        viewModel = SettingsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `toggleBlockPrivateCallers updates appSettings in repository`() {
        assertFalse(viewModel.appSettings.value.blockPrivateCallers)

        viewModel.toggleBlockPrivateCallers()

        assertTrue(viewModel.appSettings.value.blockPrivateCallers)

        viewModel.toggleBlockPrivateCallers()

        assertFalse(viewModel.appSettings.value.blockPrivateCallers)
    }

    @Test
    fun `toggleBlockAllInternational updates appSettings in repository`() {
        assertFalse(viewModel.appSettings.value.blockAllInternational)

        viewModel.toggleBlockAllInternational()

        assertTrue(viewModel.appSettings.value.blockAllInternational)

        viewModel.toggleBlockAllInternational()

        assertFalse(viewModel.appSettings.value.blockAllInternational)
    }

    @Test
    fun `setNotificationPreference updates preference in repository`() {
        assertEquals(NotificationPreference.ALL, viewModel.appSettings.value.notificationPreference)

        viewModel.setNotificationPreference(NotificationPreference.INTERNATIONAL_ONLY)
        assertEquals(NotificationPreference.INTERNATIONAL_ONLY, viewModel.appSettings.value.notificationPreference)

        viewModel.setNotificationPreference(NotificationPreference.PATTERNS_ONLY)
        assertEquals(NotificationPreference.PATTERNS_ONLY, viewModel.appSettings.value.notificationPreference)

        viewModel.setNotificationPreference(NotificationPreference.NONE)
        assertEquals(NotificationPreference.NONE, viewModel.appSettings.value.notificationPreference)
    }

    @Test
    fun `updateNotificationPermission updates state flow`() {
        assertTrue(viewModel.isNotificationPermissionGranted.value)

        viewModel.updateNotificationPermission(false)
        assertFalse(viewModel.isNotificationPermissionGranted.value)

        viewModel.updateNotificationPermission(true)
        assertTrue(viewModel.isNotificationPermissionGranted.value)
    }
}
