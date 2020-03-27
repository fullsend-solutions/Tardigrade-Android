package io.fullsend.tardigrade.fragment

import android.app.DownloadManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.fullsend.tardigrade.R
import io.fullsend.tardigrade.adapter.ObjectsAdapter
import io.fullsend.tardigrade.viewmodel.ObjectsViewModel
import kotlinx.android.synthetic.main.fragment_objects.*


class ObjectsFragment : Fragment() {

    private val args: ObjectsFragmentArgs by navArgs()
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ObjectsAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var parentLayout: View
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: ObjectsViewModel by viewModels()

        val satelliteAddress = args.satelliteAddress
        val apiKey = args.apiKey
        val passphrase = args.passphrase
        val bucketName = args.bucketName

        viewModel.satelliteAddress = satelliteAddress
        viewModel.apiKeyString = apiKey
        viewModel.passphrase = passphrase
        viewModel.bucketName = bucketName

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_buckets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: ObjectsViewModel by viewModels()
        parentLayout = view
        progress = progressBar

        progress.visibility = View.VISIBLE
        viewModel.getBucketObjectsList()
        viewModel.bucketObjects.observe(viewLifecycleOwner, Observer { list ->
            viewAdapter.setDataset(list)
            progress.visibility = View.INVISIBLE
        })

        viewManager = LinearLayoutManager(context)
        viewAdapter =
            ObjectsAdapter(arrayListOf(), this)

        recyclerView = view.findViewById<RecyclerView>(R.id.object_list).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }

        bucketName.text = args.bucketName

        back.setOnClickListener {
            findNavController().popBackStack(R.id.bucketsFragment, false)
        }
    }

    fun downloadObject(objectName: String) {
        val viewModel: ObjectsViewModel by viewModels()

        context?.let {
            progress.visibility = View.VISIBLE
            viewModel.downloadObject(objectName)
            viewModel.bucketFile.observe(viewLifecycleOwner, Observer { objectName ->
                progress.visibility = View.INVISIBLE
                showSnackbar(objectName)
            })
        }
    }

    private fun showSnackbar(fileName: String) {
        Snackbar.make(parentLayout, "$fileName downloaded!", Snackbar.LENGTH_LONG)
            .setAction("Show downloads") {
                startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
            }
            .show()
    }
}
