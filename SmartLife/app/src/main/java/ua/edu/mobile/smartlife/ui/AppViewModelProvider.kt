package ua.edu.mobile.smartlife.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ua.edu.mobile.smartlife.SmartLifeApplication
import ua.edu.mobile.smartlife.ui.details.RecordDetailsViewModel
import ua.edu.mobile.smartlife.ui.home.HomeViewModel
import ua.edu.mobile.smartlife.ui.records.RecordsViewModel

/** Фабрика ViewModel: бере залежності з AppContainer і передає їх у конструктори. */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { HomeViewModel(smartLifeApplication().container.recordRepository) }
        initializer { RecordsViewModel(smartLifeApplication().container.recordRepository) }
        initializer {
            RecordDetailsViewModel(
                savedStateHandle = createSavedStateHandle(),
                recordRepository = smartLifeApplication().container.recordRepository,
            )
        }
    }
}

fun CreationExtras.smartLifeApplication(): SmartLifeApplication =
    this[APPLICATION_KEY] as SmartLifeApplication
