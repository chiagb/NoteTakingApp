package com.example.notetakingapp

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notetakingapp.Models.Post
import com.example.notetakingapp.Models.PostWithTags
import com.example.notetakingapp.Models.Tag
import com.example.notetakingapp.Models.Todo
import com.example.notetakingapp.ViewModels.PostViewModel
import com.example.notetakingapp.databinding.BottomSheetDialogLayoutBinding
import com.example.notetakingapp.databinding.BottomSheetDialogPostBinding
import com.example.notetakingapp.databinding.PostItemBinding
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import kotlin.math.roundToInt
import kotlin.random.Random


private const val TAG = "PostWithTagsAdapter"

class PostAdapter(
    private val onDeleteCallback: (Post) -> Unit,
    private val onUpdateCallBack: (PostWithTags) -> Unit,
    private val filterByTagCallback: (Tag) -> Unit,
    private val showBottomDialogCallback: (PostWithTags) -> Unit,
    private val oldTags: MutableSet<Tag>,
) : RecyclerView.Adapter<PostAdapter.PostWithTagsViewHolder>() {

    var PostWithTagsList = listOf<PostWithTags>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = PostWithTagsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostWithTagsViewHolder {
        return PostWithTagsViewHolder.from(parent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: PostWithTagsViewHolder, position: Int) {
        val item = PostWithTagsList[position]
        holder.bind(item, onDeleteCallback, onUpdateCallBack, filterByTagCallback, showBottomDialogCallback, position, oldTags)
    }

    class PostWithTagsViewHolder private constructor(private val binding: PostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val PostWithTags = binding.post
        private val title = binding.title
        private val content = binding.content
        private val time = binding.time
        private val tagsRV = binding.tagsRV
        private val todoSummary = binding.todoSummary

        private lateinit var oldTags: MutableSet<Tag>


        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(
            item: PostWithTags,
            onDeleteCallback: (Post) -> Unit,
            onUpdateCallBack: (PostWithTags) -> Unit,
            filterByTagCallBack: (Tag) -> Unit,
            showBottomDialogCallback: (PostWithTags) -> Unit,
            position: Int,
            oldTags: MutableSet<Tag>
        ) {
            this@PostWithTagsViewHolder.oldTags =oldTags
            title.text = item.post.title
            content.text = item.post.content
            PostWithTags.backgroundTintList = ColorStateList.valueOf(item.post.colorHex)
            val timeCreation = item.post.created

            when (DateTimeUtil.getDate(timeCreation)) {
                SimpleDateFormat("dd/MM/yyyy").format(DateTimeUtil.now()) -> time.text =
                    DateTimeUtil.getHours(timeCreation) + " - Today"
                SimpleDateFormat("dd/MM/yyyy").format(DateTimeUtil.now() - 60 * 60 * 1000 * 24) -> time.text =
                    DateTimeUtil.getHours(timeCreation) + " - Yesterday"
                else -> time.text = DateTimeUtil.formatDate(timeCreation)
            }

            PostWithTags.setOnLongClickListener {
                PostWithTags.backgroundTintList = ColorStateList.valueOf(manipulateColor(item.post.colorHex,0.8f))
                showBottomDialog(item, onDeleteCallback, onUpdateCallBack, showBottomDialogCallback)
                true
            }

            tagsRV.layoutManager =
                LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
            val adapter = TagsAdapter(item.tags.toSortedSet(compareBy { it.tagId }), filterByTagCallBack, false)
            tagsRV.adapter = adapter

            if (!item.todos.isNullOrEmpty()) {
                todoSummary.visibility= View.VISIBLE
                todoSummary.backgroundTintList = ColorStateList.valueOf(manipulateColor(item.post.colorHex,0.8f))
                todoSummary.text = "${item.todos!!.filter { it.done == true }.size} done and ${item.todos!!.filter { it.done == false }.size} to go"
            }


        }

        companion object {
            fun from(parent: ViewGroup): PostWithTagsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val viewBinding = PostItemBinding
                    .inflate(layoutInflater, parent, false)
                return PostWithTagsViewHolder(viewBinding)
            }
        }

        @SuppressLint("SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.O)
        private fun showBottomDialog(
            item: PostWithTags,
            onDeleteCallback: (Post) -> Unit,
            onUpdateCallBack: (PostWithTags) -> Unit,
            showBottomDialogCallback: (PostWithTags) -> Unit,
        ) {

            val bottomSheetDialog = BottomSheetDialog(binding.root.context)
            val inflater = LayoutInflater.from(binding.root.context)

            val mBottomSheetBinding = BottomSheetDialogPostBinding.inflate(inflater, null, false)

            bottomSheetDialog.setContentView(mBottomSheetBinding.root)

            bottomSheetDialog.setOnShowListener { dia ->
                val bottomSheetDia = dia as BottomSheetDialog
                val bottomSheetInternal: FrameLayout =
                    bottomSheetDia.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
                bottomSheetInternal.setBackgroundResource(R.drawable.dialog_bg)
                val behavior = BottomSheetBehavior.from(bottomSheetInternal)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            bottomSheetDialog.setCancelable(true)
            bottomSheetDialog.setOnDismissListener {
                PostWithTags.backgroundTintList = ColorStateList.valueOf(item.post.colorHex)
            }


            mBottomSheetBinding.titleEt.text = item.post.title
            mBottomSheetBinding.contentEt.text = item.post.content
            mBottomSheetBinding.time.text = "created " + DateTimeUtil.formatDate(item.post.created)

            val adapter = TagsAdapter(item.tags,::doNothing)
            mBottomSheetBinding.tags.adapter = adapter

            val todoAdapter = TodoAdapter(item.todos!!.toMutableList(),::doNothingTodo)
            mBottomSheetBinding.todos.adapter = todoAdapter

            mBottomSheetBinding.deleteButton.setOnClickListener {
                onDeleteCallback(item.post).apply {
                    bottomSheetDialog.dismiss()
                }
            }

            mBottomSheetBinding.editButton.setOnClickListener {
                showBottomDialogCallback(item).apply {
                    bottomSheetDialog.dismiss()
                }
            }

            bottomSheetDialog.show();
        }

        fun doNothing(tag: Tag) = {}
        fun doNothingTodo(todo: Todo) = {



        }

        @SuppressLint("SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.O)
        private fun showSecondDialog(item: PostWithTags, onUpdateCallBack: (PostWithTags) -> Unit) {

            val bottomSheetDialog = BottomSheetDialog(binding.root.context)
            val inflater = LayoutInflater.from(binding.root.context)

            val mBottomSheetBinding = BottomSheetDialogLayoutBinding.inflate(inflater, null, false)

            bottomSheetDialog.setContentView(mBottomSheetBinding.root)

            bottomSheetDialog.setOnShowListener { dia ->
                val bottomSheetDia = dia as BottomSheetDialog
                val bottomSheetInternal: FrameLayout =
                    bottomSheetDia.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
                bottomSheetInternal.setBackgroundResource(R.drawable.dialog_bg)
                val behavior = BottomSheetBehavior.from(bottomSheetInternal)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            bottomSheetDialog.setCancelable(true)

            mBottomSheetBinding.titleEt.setText(item.post.title)
            mBottomSheetBinding.contentEt.setText(item.post.content)
            mBottomSheetBinding.color.backgroundTintList = ColorStateList.valueOf(item.post.colorHex)
            mBottomSheetBinding.addPostButton.text = "Edit Post"

            val newTagsRV = mBottomSheetBinding.newTagsRV
            newTagsRV.layoutManager =
                LinearLayoutManager(mBottomSheetBinding.root.context, LinearLayoutManager.VERTICAL, false)

            val newTags = mutableSetOf<Tag>()
            var oldTags = mutableSetOf<Tag>()
            var selectedColor : String?

            mBottomSheetBinding.color.setOnClickListener {
                MaterialColorPickerDialog
                    .Builder(mBottomSheetBinding.root.context)        				// Pass Activity Instance
                    .setTitle("Pick Theme")           	// Default "Choose Color"
                    .setColorShape(ColorShape.SQAURE)   // Default ColorShape.CIRCLE
                    .setDefaultColor(R.color.lilla)     // Pass Default Color
                    .setColorListener { color, colorHex ->
                        selectedColor = colorHex
                        mBottomSheetBinding.color.backgroundTintList =
                            ColorStateList.valueOf(color)
                        item.post.colorHex = Color.parseColor(selectedColor)
                    }
                    .show()
            }
            val adapter = NewTagsAdapter(item.tags.toMutableList(),oldTags,::doNothing)

            mBottomSheetBinding.plus.setOnClickListener {
                newTags.add(Tag(tagId= Random(DateTimeUtil.now()*17).nextInt()))
                adapter.tagList = newTags.toMutableList()
                adapter.notifyDataSetChanged()
            }

            mBottomSheetBinding.newTagsRV.adapter = adapter

            mBottomSheetBinding.addPostButton.setOnClickListener {
                item.post.title = mBottomSheetBinding.titleEt.text.toString()
                item.post.content = mBottomSheetBinding.contentEt.text.toString()

                item.tags = adapter.tagList.toMutableSet()

                onUpdateCallBack(item).apply {
                    bottomSheetDialog.dismiss()
                }
            }

            bottomSheetDialog.show();
        }




        fun manipulateColor(color: Int, factor: Float): Int {
            val a = Color.alpha(color)
            val r = (Color.red(color) * factor).roundToInt()
            val g = (Color.green(color) * factor).roundToInt()
            val b = (Color.blue(color) * factor).roundToInt()
            return Color.argb(
                a,
                r.coerceAtMost(255),
                g.coerceAtMost(255),
                b.coerceAtMost(255)
            )
        }

    }


}


