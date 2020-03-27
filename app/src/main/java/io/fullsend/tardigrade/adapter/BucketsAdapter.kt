package io.fullsend.tardigrade.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import io.fullsend.tardigrade.R
import io.fullsend.tardigrade.fragment.BucketsFragmentArgs
import io.fullsend.tardigrade.fragment.BucketsFragmentDirections
import io.storj.BucketInfo


class BucketsAdapter(
    private val dataset: ArrayList<BucketInfo>,
    private val args: BucketsFragmentArgs
) :
    RecyclerView.Adapter<BucketsAdapter.BucketViewHolder>() {

    fun setDataset(newDataset: List<BucketInfo>) {
        dataset.clear()
        dataset.addAll(newDataset)
        notifyDataSetChanged()
    }

    open class BucketViewHolder(open var layout: View) :
        RecyclerView.ViewHolder(layout)

    class BucketItemViewHolder(
        override var layout: View, val bucketName: TextView
    ) :
        BucketViewHolder(layout)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BucketViewHolder {

        var rootView = LayoutInflater.from(parent.context).inflate(R.layout.bucket_item, parent, false)
        val textView = rootView.findViewById<TextView>(R.id.bucketName)

        return BucketItemViewHolder(
            rootView,
            textView
        )
    }

    override fun onBindViewHolder(holder: BucketViewHolder, position: Int) {

        val item = dataset[position]

        val bucketName = item.name
        val bucketItemView = holder as BucketItemViewHolder
        bucketItemView.bucketName.text = bucketName

        val action =
            BucketsFragmentDirections.actionBucketsFragmentToObjectsFragment(args.satelliteAddress, args.apiKey,
            args.passphrase, bucketName)

        holder.bucketName.setOnClickListener(
            Navigation.createNavigateOnClickListener(action)
        )

    }

    override fun getItemCount() = dataset.size

    override fun getItemViewType(position: Int): Int {
        return 1
    }
}