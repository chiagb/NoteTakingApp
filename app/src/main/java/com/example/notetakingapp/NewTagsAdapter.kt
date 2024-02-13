package com.example.notetakingapp

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.notetakingapp.Models.Tag
import com.example.notetakingapp.databinding.NewTagItemBinding
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape


private const val TAG = "TagsAdapter"

class NewTagsAdapter(
    var tagList: MutableList<Tag>,
    val oldTags: MutableSet<Tag>,
    private val clickCallback: (Tag) -> Unit
) : RecyclerView.Adapter<NewTagsAdapter.NewTagsViewHolder>() {

    override fun getItemCount() = tagList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewTagsViewHolder {
        return NewTagsViewHolder.from(parent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: NewTagsViewHolder, position: Int) {
        val item = tagList[position]
        oldTags.removeIf{ tagList.map { tags->tags.name }.contains(it.name) }
        holder.bind(item,
            oldTags,
            clickCallback,
            position)
    }

    class NewTagsViewHolder private constructor(private val binding: NewTagItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val closeX = binding.closeTag
        private val color = binding.color
        private val et = binding.editText
        private val etAC = binding.autocomplete


        @SuppressLint("SimpleDateFormat", "SetTextI18n", "ResourceAsColor")
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(
            item: Tag,
            oldTags: MutableSet<Tag>,
            clickCallback: (Tag) -> Unit,
            position: Int
        ) {
            val selectedColor : String? = null


            if(item.name!="") {
                etAC.setText(item.name)
                color.backgroundTintList = ColorStateList.valueOf(item.colorHex)
            }

            etAC.addTextChangedListener {
                item.name = it.toString()
            }

            color.setOnClickListener {
                MaterialColorPickerDialog
                    .Builder(binding.root.context)        				// Pass Activity Instance
                    .setTitle("Pick Theme")           	// Default "Choose Color"
                    .setColorShape(ColorShape.SQAURE)   // Default ColorShape.CIRCLE
                    .setDefaultColor(R.color.light_yellow)     // Pass Default Color
                    .setColorListener { color, colorHex ->
                        item.colorHex = android.graphics.Color.parseColor(colorHex)
                        it.backgroundTintList =
                            ColorStateList.valueOf(color)
                    }
                    .show()
            }

            var selectedTag : Int? = null


            //val arrayAdapter = ArrayAdapter(binding.root.context,R.layout.textview_item, oldTags.map { it.name }.filter { it.isNotEmpty() })
            val arrayAdapter = ArrayListTagsAdapter(oldTags,binding.root.context,R.layout.tag_item)
            etAC.setAdapter(arrayAdapter)

            etAC.setOnItemClickListener { parent, _, position, _ ->
                val city = arrayAdapter.getItem(position)
                etAC.setText(city.name)
                item.tagId =  city.tagId
            }

            closeX.setOnClickListener {
                clickCallback(item)
            }
        }


        companion object {
            fun from(parent: ViewGroup): NewTagsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val viewBinding = NewTagItemBinding
                    .inflate(layoutInflater, parent, false)
                return NewTagsViewHolder(viewBinding)
            }
        }
//
//        @SuppressLint("SetTextI18n")
//        @RequiresApi(Build.VERSION_CODES.O)
//        private fun showBottomDialog(
//            item: Post,
//            onDeleteCallback: (Post) -> Unit,
//            onUpdateCallBack: (Post) -> Unit
//        ) {
//
//            val bottomSheetDialog = BottomSheetDialog(binding.root.context)
//            val inflater = LayoutInflater.from(binding.root.context)
//
//            val mBottomSheetBinding = BottomSheetDialogPostBinding.inflate(inflater, null, false)
//
//            bottomSheetDialog.setContentView(mBottomSheetBinding.root)
//
//            bottomSheetDialog.setOnShowListener { dia ->
//                val bottomSheetDia = dia as BottomSheetDialog
//                val bottomSheetInternal: FrameLayout =
//                    bottomSheetDia.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
//                bottomSheetInternal.setBackgroundResource(R.drawable.dialog_bg)
//                val behavior = BottomSheetBehavior.from(bottomSheetInternal)
//                behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            }
//            bottomSheetDialog.setCancelable(true)
//            bottomSheetDialog.setOnDismissListener {
//                post.backgroundTintList = ColorStateList.valueOf(item.colorHex)
//            }
//
//
//            mBottomSheetBinding.titleEt.text = item.title
//            mBottomSheetBinding.contentEt.text = item.content
//            mBottomSheetBinding.time.text = "created " + DateTimeUtil.formatDate(item.created)
//
//            mBottomSheetBinding.deleteButton.setOnClickListener {
//                onDeleteCallback(item).apply {
//                    bottomSheetDialog.dismiss()
//                }
//            }
//
//            mBottomSheetBinding.editButton.setOnClickListener {
//                showSecondDialog(item, onUpdateCallBack).apply {
//                    bottomSheetDialog.dismiss()
//                }
//            }
//
//
//
//            bottomSheetDialog.show();
//        }
//
//        @SuppressLint("SetTextI18n")
//        @RequiresApi(Build.VERSION_CODES.O)
//        private fun showSecondDialog(item: Post, onUpdateCallBack: (Post) -> Unit) {
//
//            val bottomSheetDialog = BottomSheetDialog(binding.root.context)
//            val inflater = LayoutInflater.from(binding.root.context)
//
//            val mBottomSheetBinding = BottomSheetDialogLayoutBinding.inflate(inflater, null, false)
//
//            bottomSheetDialog.setContentView(mBottomSheetBinding.root)
//
//            bottomSheetDialog.setOnShowListener { dia ->
//                val bottomSheetDia = dia as BottomSheetDialog
//                val bottomSheetInternal: FrameLayout =
//                    bottomSheetDia.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
//                bottomSheetInternal.setBackgroundResource(R.drawable.dialog_bg)
//                val behavior = BottomSheetBehavior.from(bottomSheetInternal)
//                behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            }
//            bottomSheetDialog.setCancelable(true)
//
//            mBottomSheetBinding.titleEt.setText(item.title)
//            mBottomSheetBinding.contentEt.setText(item.content)
//            mBottomSheetBinding.addPostButton.text = "Edit Post"
//
//
//            mBottomSheetBinding.addPostButton.setOnClickListener {
//                item.title = mBottomSheetBinding.titleEt.text.toString()
//                item.content = mBottomSheetBinding.contentEt.text.toString()
//                onUpdateCallBack(item).apply {
//                    bottomSheetDialog.dismiss()
//                }
//            }
//
//            bottomSheetDialog.show();
//        }
//
//        fun manipulateColor(color: Int, factor: Float): Int {
//            val a = Color.alpha(color)
//            val r = (Color.red(color) * factor).roundToInt()
//            val g = (Color.green(color) * factor).roundToInt()
//            val b = (Color.blue(color) * factor).roundToInt()
//            return Color.argb(
//                a,
//                r.coerceAtMost(255),
//                g.coerceAtMost(255),
//                b.coerceAtMost(255)
//            )
//        }
//
    }


}


