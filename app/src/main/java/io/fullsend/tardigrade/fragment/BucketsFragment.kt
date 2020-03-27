package io.fullsend.tardigrade.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.fullsend.tardigrade.viewmodel.BucketsViewModel
import io.fullsend.tardigrade.R
import io.fullsend.tardigrade.adapter.BucketsAdapter
import kotlinx.android.synthetic.main.fragment_buckets.*

class BucketsFragment : Fragment() {

    val args: BucketsFragmentArgs by navArgs()
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: BucketsAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: BucketsViewModel by viewModels()

        val satelliteAddress = args.satelliteAddress
        val apiKey = args.apiKey
        val passphrase = args.passphrase

        viewModel.satelliteAddress = satelliteAddress
        viewModel.apiKeyString = apiKey
        viewModel.passphrase = passphrase
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_buckets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: BucketsViewModel by viewModels()

        progressBar.visibility = View.VISIBLE
        viewModel.getBucketList()
        viewModel.bucketNames.observe(viewLifecycleOwner, Observer { list ->
            list.forEach {
                Log.d("Bucket Name: ", it.name)
            }
            viewAdapter.setDataset(list)
            progressBar.visibility = View.INVISIBLE
        })

        viewManager = LinearLayoutManager(context)
        viewAdapter =
            BucketsAdapter(arrayListOf(), args)

        recyclerView = view.findViewById<RecyclerView>(R.id.object_list).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        back.setOnClickListener {
            findNavController().popBackStack(R.id.main_fragment, false)
        }
    }

}
