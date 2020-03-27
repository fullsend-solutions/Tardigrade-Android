package io.fullsend.tardigrade.viewmodel

import android.app.Application
import android.content.ContentValues
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import io.storj.*
import kotlinx.coroutines.Dispatchers


class ObjectsViewModel(application: Application) : AndroidViewModel(application) {
    var satelliteAddress = String()
    var apiKeyString = String()
    var passphrase = String()
    var bucketName = String()

    var bucketObjects: LiveData<List<ObjectInfo>> = MutableLiveData()
    var bucketFile: LiveData<String> = MutableLiveData()

    fun getBucketObjectsList() {
        val apiKey = ApiKey.parse(apiKeyString)
        bucketObjects = liveData(Dispatchers.IO) {
            Uplink().use { uplink ->
                uplink.openProject(satelliteAddress, apiKey).use { project ->
                    val saltedKey: Key = Key.getSaltedKeyFromPassphrase(project, passphrase)
                    val access = EncryptionAccess(saltedKey)
                    val scope = Scope(satelliteAddress, apiKey, access)

                    val project = uplink.openProject(scope)
                    val bucket = project.openBucket(bucketName, scope)
                    val bucketObjects: Iterable<ObjectInfo> = bucket.listObjects()
                    val bucketObjectsList = bucketObjects.toList()
                    emit(bucketObjectsList)
                }
            }
        }
    }

    fun downloadObject(objectName: String) {
        val apiKey = ApiKey.parse(apiKeyString)
        bucketFile = liveData(Dispatchers.IO) {
            Uplink().use { uplink ->
                uplink.openProject(satelliteAddress, apiKey).use { project ->
                    val saltedKey: Key = Key.getSaltedKeyFromPassphrase(project, passphrase)
                    val access = EncryptionAccess(saltedKey)
                    val scope = Scope(satelliteAddress, apiKey, access)

                    val project = uplink.openProject(scope)
                    val bucket = project.openBucket(bucketName, scope)

                    val context = getApplication<Application>()
                    val resolver = context.contentResolver
                    val contentValues = ContentValues().apply {
                        put(MediaStore.DownloadColumns.DISPLAY_NAME, objectName)
                        put(MediaStore.DownloadColumns.MIME_TYPE, getMimeType(objectName))
                    }

                    val uri = resolver?.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                    uri?.let {
                        resolver.openOutputStream(uri).use {
                            bucket.downloadObject(objectName, it)
                        }
                    }

                    emit(objectName)
                }
            }
        }
    }

    fun getMimeType(url: String?): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }
}