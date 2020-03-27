package io.fullsend.tardigrade.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import io.fullsend.tardigrade.viewmodel.MainViewModel
import io.fullsend.tardigrade.R
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment : Fragment() {

    private var documentationUri = "https://documentation.tardigrade.io/getting-started/uploading-your-first-object/prerequisites"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: MainViewModel by viewModels()

        viewModel.satelliteAddress = editTextSatelliteAddress.text.toString()
        viewModel.apiKeyString = editTextApiPhrase.text.toString()
        viewModel.passphrase = editTextPassphrase.text.toString()


        createAccount.setOnClickListener {
            val browserIntent =
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(documentationUri)
                )
            startActivity(browserIntent)
        }

        editTextSatelliteAddress.doOnTextChanged { text, start, count, after ->
            viewModel.satelliteAddress = text.toString()
        }

        editTextApiPhrase.doOnTextChanged { text, start, count, after ->
            viewModel.apiKeyString = text.toString()
        }

        editTextPassphrase.doOnTextChanged { text, start, count, after ->
            viewModel.passphrase = text.toString()
        }

        showBucketsButton.setOnClickListener {
            showBucketsButton.visibility = View.INVISIBLE
            mainProgress.visibility = View.VISIBLE
            viewModel.getProject()
            viewModel.project.observe(viewLifecycleOwner, Observer { project ->
                val action =
                    MainFragmentDirections.actionMainFragmentToBucketsFragment(
                        viewModel.satelliteAddress,
                        viewModel.apiKeyString,
                        viewModel.passphrase
                    )
                it.findNavController().navigate(action)
                mainProgress.visibility = View.INVISIBLE
            })
        }
    }

}