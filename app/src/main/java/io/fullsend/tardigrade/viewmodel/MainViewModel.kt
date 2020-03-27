package io.fullsend.tardigrade.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import io.storj.*
import kotlinx.coroutines.Dispatchers

class MainViewModel : ViewModel() {
    var satelliteAddress = String()
    var apiKeyString = String()
    var passphrase = String()

    var project: LiveData<Project> = MutableLiveData()

    fun getProject() {
        val apiKey = ApiKey.parse(apiKeyString)
        project = liveData(Dispatchers.IO) {
            Uplink().use { uplink ->
                uplink.openProject(satelliteAddress, apiKey).use { project ->
                    val saltedKey: Key = Key.getSaltedKeyFromPassphrase(project, passphrase)
                    val access = EncryptionAccess(saltedKey)
                    val scope = Scope(satelliteAddress, apiKey, access)
                    val project = uplink.openProject(scope)
                    emit(project)
                }
            }
        }
    }
}