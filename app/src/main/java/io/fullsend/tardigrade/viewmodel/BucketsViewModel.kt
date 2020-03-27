package io.fullsend.tardigrade.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import io.storj.*
import kotlinx.coroutines.Dispatchers

class BucketsViewModel : ViewModel() {
    var satelliteAddress = String()
    var apiKeyString = String()
    var passphrase = String()

    var bucketNames: LiveData<List<BucketInfo>> = MutableLiveData()

    fun getBucketList() {
        val apiKey = ApiKey.parse(apiKeyString)
        bucketNames = liveData(Dispatchers.IO) {
            Uplink().use { uplink ->
                uplink.openProject(satelliteAddress, apiKey).use { project ->
                    val saltedKey: Key = Key.getSaltedKeyFromPassphrase(project, passphrase)
                    val access = EncryptionAccess(saltedKey)
                    val scope = Scope(satelliteAddress, apiKey, access)

                    val project = uplink.openProject(scope)
                    val buckets: Iterable<BucketInfo> = project.listBuckets()
                    val bucketList = buckets.toList()
                    emit(bucketList)
                }
            }
        }
    }
}