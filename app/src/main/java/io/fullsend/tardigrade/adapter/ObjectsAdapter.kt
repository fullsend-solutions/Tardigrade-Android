package io.fullsend.tardigrade.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.fullsend.tardigrade.R
import io.fullsend.tardigrade.fragment.ObjectsFragment
import io.storj.ObjectInfo


class ObjectsAdapter(
    private val dataset: ArrayList<ObjectInfo>,
    private val fragment: ObjectsFragment
) :
    RecyclerView.Adapter<ObjectsAdapter.ObjectViewHolder>() {

    fun setDataset(newDataset: List<ObjectInfo>) {
        dataset.clear()
        dataset.addAll(newDataset)
        notifyDataSetChanged()
    }

    open class ObjectViewHolder(open var layout: View) :
        RecyclerView.ViewHolder(layout)

    class ObjectItemViewHolder(
        override var layout: View, val objectName: TextView
    ) :
        ObjectsAdapter.ObjectViewHolder(layout)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ObjectViewHolder {

        var rootView =
            LayoutInflater.from(parent.context).inflate(R.layout.object_item, parent, false)
        val textView = rootView.findViewById<TextView>(R.id.objectName)

        return ObjectItemViewHolder(
            rootView,
            textView
        )
    }

    override fun onBindViewHolder(holder: ObjectViewHolder, position: Int) {

        val item = dataset[position]

        val objectName = item.path
        val objectItemView = holder as ObjectItemViewHolder
        objectItemView.objectName.text = objectName

        holder.objectName.setOnClickListener{
            fragment.downloadObject(objectName)
        }
    }

    override fun getItemCount() = dataset.size

    override fun getItemViewType(position: Int): Int {
        return 1
    }
}