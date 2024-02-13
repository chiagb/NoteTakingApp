package com.example.notetakingapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.notetakingapp.Models.Tag
import com.example.notetakingapp.databinding.TagItemBinding
import java.util.*
import kotlin.collections.ArrayList


private const val TAG = "TagsAdapter"

class ArrayListTagsAdapter(
    var tagList: MutableSet<Tag>,
    val mContext: Context,
    val viewResourceId: Int
) : ArrayAdapter<Tag>(mContext,viewResourceId, tagList.toList()) {
    private val tag: MutableList<Tag> = ArrayList(tagList)
    private var allTags: List<Tag> = ArrayList(tagList)

    override fun getCount(): Int {
        return tag.size
    }

    override fun getItem(position: Int): Tag {
        return tag[position]
    }

    override fun getItemId(position: Int): Long {
        return tag[position].tagId.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = (mContext as Activity).layoutInflater
            convertView = inflater.inflate(viewResourceId, parent, false)
        }
        try {
            val tag: Tag = getItem(position)
            val tagAV = convertView!!.findViewById<View>(R.id.tagName) as TextView
            tagAV.text = tag.name
            convertView.backgroundTintList = ColorStateList.valueOf(tag.colorHex)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return convertView!!
    }
}


