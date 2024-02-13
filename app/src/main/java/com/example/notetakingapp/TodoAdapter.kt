package com.example.notetakingapp

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.marginEnd
import androidx.core.view.marginRight
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.notetakingapp.Models.Tag
import com.example.notetakingapp.Models.Todo
import com.example.notetakingapp.databinding.TagItemBinding
import com.example.notetakingapp.databinding.TodoItemBinding


private const val TAG = "TagsAdapter"

class TodoAdapter(
    var todoList: MutableList<Todo>,
    private val clickCallback: (Todo) -> Unit,
    val isDeletable: Boolean = false
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    override fun getItemCount() = todoList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder.from(parent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val item = todoList.elementAt(position)
        holder.bind(item,clickCallback, isDeletable, position)
    }

    class TodoViewHolder private constructor(private val binding: TodoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val checkbox = binding.doneCheckBox
        private val descriptionET = binding.checkboxET
        private val closeX = binding.closeTodo


        @SuppressLint("SimpleDateFormat", "SetTextI18n", "ResourceAsColor")
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(
            item: Todo,
            clickCallback: (Todo) -> Unit,
            isDeletable: Boolean,
            position: Int
        ) {

            if (isDeletable) {
                closeX.visibility = View.VISIBLE
                descriptionET.isEnabled = true
            } else {
                closeX.visibility = View.GONE
                descriptionET.isEnabled = false
                checkbox.isClickable = false
            }

            checkbox.isChecked = item.done

            if (item.activityText!="")
                descriptionET.setText(item.activityText)

            checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                item.done = isChecked
            }

            descriptionET.addTextChangedListener {
                item.activityText = it.toString()
            }

            closeX.setOnClickListener {
                //todo add list of active filters
                clickCallback(item)
            }
        }

        companion object {
            fun from(parent: ViewGroup): TodoViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val viewBinding = TodoItemBinding
                    .inflate(layoutInflater, parent, false)
                return TodoViewHolder(viewBinding)
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


