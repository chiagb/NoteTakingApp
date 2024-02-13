package com.example.notetakingapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notetakingapp.Database.AppDatabase
import com.example.notetakingapp.Models.*
import com.example.notetakingapp.ViewModels.PostViewModel
import com.example.notetakingapp.ViewModels.TagViewModel
import com.example.notetakingapp.ViewModels.TodoViewModel
import com.example.notetakingapp.databinding.BottomSheetDialogLayoutBinding
import com.example.notetakingapp.databinding.FragmentHomeBinding
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.*
import java.lang.reflect.Type
import kotlin.random.Random


private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: PostAdapter
    private lateinit var tagsAdapter: TagsAdapter
    private lateinit var newTagsAdapter: NewTagsAdapter
    private lateinit var todoAdapter: TodoAdapter
    private val repository by inject<PostRepository>()
    private val tagRepository by inject<TagRepository>()
    private val todoRepository by inject<TodoRepository>()

    private lateinit var toggle: ActionBarDrawerToggle
    private val gson = Gson()
    private val viewModel: PostViewModel by viewModels {
        PostViewModelFactory(repository)
    }
    private val tagsViewModel: TagViewModel by viewModels {
        TagViewModelFactory(tagRepository)
    }
    private val todoViewModel: TodoViewModel by viewModels {
        TodoViewModelFactory(todoRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.postRV
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        lifecycleScope.launch {
            tagsViewModel.allTags.collect {
                adapter = PostAdapter(::delete, ::update, ::filterByTag, ::showBottomDialog, it.toMutableSet())
                recyclerView.adapter = adapter
            }
        }


        val tagsFilterRV = binding.activeFiltersRV
        tagsFilterRV.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        tagsAdapter = TagsAdapter(mutableSetOf(), ::removeTagFilter, isDeletable = true)


        lifecycleScope.launch {
            viewModel.activeFilters.collect {
                tagsAdapter.tagList = it
            }
        }

        tagsFilterRV.adapter = tagsAdapter

        binding.addButton.setOnClickListener {
            showBottomDialog()
        }

        initAllPosts()

        binding.refreshAction.setOnClickListener {
            binding.searchBar.text.clear()
            lifecycleScope.launch {
                viewModel.removeAllFilters()
            }
            initAllPosts()
        }

        binding.searchDelete.setOnClickListener {
            binding.searchBar.text.clear()
        }

        binding.searchBar.addTextChangedListener {
            lifecycleScope.launch {
                viewModel.allPostsWithTags.collect { posts ->
                    adapter.PostWithTagsList = SearchNotes.execute(posts, binding.searchBar.text.toString())
                }
            }
        }


        val drawerLayout = binding.drawerLayout
        val navView = binding.navView

        toggle =
            ActionBarDrawerToggle(requireActivity(), drawerLayout, R.string.open, R.string.close)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miBackup -> {
                    val stdJsonPosts = gson.toJson(adapter.PostWithTagsList)

                    if (isStoragePermissionGranted())
                        writeToStorage(
                            requireContext(),
                            "BackupNotetakingAppPostsTags_" + DateTimeUtil.formatDateFileWithTime(
                                DateTimeUtil.now()
                            )+".json",
                            stdJsonPosts
                        )
                }
                R.id.miDeleteAll -> deleteAll()
                R.id.miRestore -> {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "*/*"
                    val mimetypes = arrayOf("application/json")
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
                    if (
                        ActivityCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            1
                        )
                    } else {
                        getResult.launch(intent)
                    }

                }
                else -> Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
            }
            true
        }

        binding.sideMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }


    }

    private fun deleteTagFromPost(post: Post, tagId: Int) {
        lifecycleScope.launch {
            tagsViewModel.getTagWithPostsById(tagId).take(1).collect{
                if (it?.posts?.size == 0)
                    tagsViewModel.delete(it.tag)
            }
        }
        viewModel.deletePostTagCrossRef(PostTagCrossRef(post.id,tagId))


    }

    private fun initAllPosts() {
        lifecycleScope.launch {
            viewModel.allPostsWithTags.collect { posts ->
                adapter.PostWithTagsList = posts
            }
        }
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            try {
                if (it.resultCode == Activity.RESULT_OK) {
                    val contentResolver = requireActivity().contentResolver
                    val path = it.data?.data
                    val jsonSelectedFile =
                        path?.let { it1 -> contentResolver.openInputStream(it1) };
                    val inputAsString = jsonSelectedFile?.bufferedReader().use { it?.readText() }
                    val listType: Type = object : TypeToken<ArrayList<PostWithTags>>() {}.type

                    val posts : List<PostWithTags> = gson.fromJson(inputAsString, listType)

                    lifecycleScope.launch {
                        posts.forEach { post ->
                            val postId = viewModel.insert(post.post)
                            post.tags.forEach {
                                val tagId = tagsViewModel.insert(it)
                                if(tagId!=-1L && postId!=-1L) {
                                    viewModel.insertPostTagCrossRef(PostTagCrossRef(postId.toInt(),tagId.toInt()))
                                }
                            }
                        }
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    private fun delete(post: Post) =
        viewModel.delete(post)

    private fun deleteAll() {
        viewModel.deleteAll()
        tagsViewModel.deleteAll()
    }

    private fun update(post: PostWithTags) {
        viewModel.update(post.post)
        post.tags.forEach { tag ->
            viewModel.insertPostTagCrossRef(PostTagCrossRef(post.post.id,tag.tagId))
            tagsViewModel.update(tag) }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterByTag(tag: Tag) {

        lifecycleScope.launch {
            viewModel.addFilter(tag)
            tagsAdapter.notifyDataSetChanged()
            if (tagsAdapter.itemCount > 0)
                binding.activeFiltersRV.visibility = View.VISIBLE
            viewModel.filterByTag(viewModel.activeFilters.value).collect {
                adapter.PostWithTagsList = it
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeTagFilter(tag: Tag) =
        lifecycleScope.launch {
            viewModel.removeFilter(tag)
            tagsAdapter.notifyDataSetChanged()
            if (tagsAdapter.itemCount == 0)
                binding.activeFiltersRV.visibility = View.GONE
            viewModel.filterByTag(viewModel.activeFilters.value).collect {
                adapter.PostWithTagsList = it
            }}



    @SuppressLint("NotifyDataSetChanged", "ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showBottomDialog(postWithTags: PostWithTags? = null) {

        val bottomSheetDialog = BottomSheetDialog(requireContext())

        var selectedColor: String? = null

        val mBottomSheetBinding =
            BottomSheetDialogLayoutBinding.inflate(layoutInflater, null, false)

        bottomSheetDialog.setContentView(mBottomSheetBinding.root)

        val newTagsRV = mBottomSheetBinding.newTagsRV
        newTagsRV.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val todoRV = mBottomSheetBinding.newTodoRV
        todoRV.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        var newTags = mutableSetOf<Tag>()
        var newTodos = mutableSetOf<Todo>()
        var oldTags = mutableSetOf<Tag>()
        val oldPostWithTagsTags = postWithTags?.tags?.map { it.tagId }?.toList()

        if ( postWithTags != null ){
            mBottomSheetBinding.titleEt.setText(postWithTags.post.title)
            mBottomSheetBinding.contentEt.setText(postWithTags.post.content)
            mBottomSheetBinding.color.backgroundTintList = ColorStateList.valueOf(postWithTags.post.colorHex)
            mBottomSheetBinding.addPostButton.text = "Edit Post"

            newTags = postWithTags.tags
            if(!postWithTags.todos.isNullOrEmpty()) newTodos = postWithTags.todos!!
        }

        lifecycleScope.launch {
            tagsViewModel.allTags.collect{
                oldTags = it.toMutableSet()
                newTagsAdapter = NewTagsAdapter(newTags.toMutableList(), oldTags , ::removeFromNewTags)
                newTagsRV.adapter = newTagsAdapter
            }
        }

        todoAdapter= TodoAdapter(newTodos.toMutableList(),::removeFromNewTodos, isDeletable = true)

        todoRV.adapter= todoAdapter

        mBottomSheetBinding.color.setOnClickListener {
            MaterialColorPickerDialog
                .Builder(requireContext())        				// Pass Activity Instance
                .setTitle("Pick Theme")           	// Default "Choose Color"
                .setColorShape(ColorShape.SQAURE)   // Default ColorShape.CIRCLE
                .setDefaultColor(R.color.lilla)     // Pass Default Color
                .setColorListener { color, colorHex ->
                    selectedColor = colorHex
                    mBottomSheetBinding.color.backgroundTintList =
                        ColorStateList.valueOf(color)
                }
                .show()
        }

        mBottomSheetBinding.plus.setOnClickListener {
            newTagsAdapter.tagList.add(newTagsAdapter.itemCount,Tag(tagId= Random(DateTimeUtil.now()*17).nextInt()))
            newTagsAdapter.notifyItemInserted(newTagsAdapter.itemCount-1)
        }

        mBottomSheetBinding.plusTodo.setOnClickListener {
            todoAdapter.todoList.add(todoAdapter.itemCount,
                Todo(todoId= Random(DateTimeUtil.now()*17).nextInt())
            )
            todoAdapter.notifyItemInserted(todoAdapter.itemCount-1)
        }

        bottomSheetDialog.setOnShowListener { dia ->
            val bottomSheetDia = dia as BottomSheetDialog
            val bottomSheetInternal: FrameLayout =
                bottomSheetDia.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
            bottomSheetInternal.setBackgroundResource(R.drawable.dialog_bg)
            val behavior = BottomSheetBehavior.from(bottomSheetInternal)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        bottomSheetDialog.setCancelable(true)

        mBottomSheetBinding.addPostButton.setOnClickListener {
            val title = mBottomSheetBinding.titleEt.text.toString()
            val content = mBottomSheetBinding.contentEt.text.toString()
            val tags = newTagsAdapter.tagList.filter { it.name.isNotEmpty() }.toMutableSet()
            val todos = todoAdapter.todoList.filter { it.activityText.isNotEmpty() }.toMutableSet()
            val taglist = newTagsAdapter.tagList

            if(postWithTags==null) {

                var post : Post? = null
                post = if(selectedColor!=null) {
                    Post(title = title, content = content, colorHex =
                    android.graphics.Color.parseColor(selectedColor)
                    )
                } else {
                    Post(title = title, content = content)
                }
                if(validatePost(post)) {
                    lifecycleScope.launch {
                        val postId = viewModel.insert(post)
                        tags.forEach { tag ->
                            val idTag = tagsViewModel.insert(tag)
                            if (idTag == -1L)
                                viewModel.insertPostTagCrossRef(
                                    PostTagCrossRef(
                                        postId.toInt(),
                                        tag.tagId
                                    )
                                )
                            else
                                viewModel.insertPostTagCrossRef(
                                    PostTagCrossRef(
                                        postId.toInt(),
                                        idTag.toInt()
                                    )
                                )
                        }
                        todos.forEach { todo ->
                            val idTodo = todoViewModel.insert(todo)
                            if (idTodo == -1L)
                                viewModel.insertPostTodoCrossRef(PostTodoCrossRef(postId.toInt(), todo.todoId))
                            else
                                viewModel.insertPostTodoCrossRef(PostTodoCrossRef(postId.toInt(), idTodo.toInt()))

                        }.apply {
                            bottomSheetDialog.dismiss()
                        }
                    }
                } else {
                    mBottomSheetBinding.titleEt.setError("Add title")
                    mBottomSheetBinding.contentEt.setError("Add description")
                }
            } else {
                postWithTags.post.title = title
                postWithTags.post.content = content
                if (selectedColor!=null)
                    postWithTags.post.colorHex = android.graphics.Color.parseColor(selectedColor)

                if (validatePost(postWithTags.post)) {
                    tags.forEach { afterTag ->
                        if (oldPostWithTagsTags?.contains(afterTag.tagId) == true) {
                            //se già c'era
                            tagsViewModel.update(afterTag)
                        } else {
                            lifecycleScope.launch{
                                val tagId= tagsViewModel.insert(afterTag)
                                if (tagId == -1L)
                                    viewModel.insertPostTagCrossRef(
                                        PostTagCrossRef(
                                            postWithTags.post.id,
                                            afterTag.tagId
                                        )
                                    )
                                else
                                    viewModel.insertPostTagCrossRef(
                                    PostTagCrossRef(
                                        postWithTags.post.id,
                                        tagId.toInt()
                                    )
                                )
                            }

                        }
                    }

                    lifecycleScope.launch {
                        todos.forEach { todo ->
                            val todoId = todoViewModel.insert(todo)
                            if(todoId==-1L)
                                todoViewModel.update(todo)
                            else
                                viewModel.insertPostTodoCrossRef(PostTodoCrossRef(postWithTags.post.id,todoId.toInt()))
                        }
                    }


                    oldPostWithTagsTags?.forEach { beforeTag ->
                        if (!tags.map { it.tagId }.contains(beforeTag))
                        {
                            deleteTagFromPost(postWithTags.post,beforeTag)
                        }
                    }

                    viewModel.update(postWithTags.post)
                        .apply {
                            adapter.notifyDataSetChanged()
                            bottomSheetDialog.dismiss()
                        }
                }
                else {
                    mBottomSheetBinding.titleEt.setError("Add title")
                    mBottomSheetBinding.contentEt.setError("Add description")
                }
            }
            selectedColor = null
        }

        bottomSheetDialog.show();
    }

    private fun validatePost(post: Post): Boolean = post.title.isNotEmpty()

    private fun removeFromNewTags(tag:Tag) {
        val position = newTagsAdapter.tagList.indexOf(tag)
        newTagsAdapter.tagList.remove(tag)
        newTagsAdapter.notifyItemRemoved(position)
    }


    private fun removeFromNewTodos(todo:Todo) {
        val position = todoAdapter.todoList.indexOf(todo)
        todoAdapter.todoList.remove(todo)
        todoAdapter.notifyItemRemoved(position)
    }

    private fun writeToStorage(context: Context, filename: String, json: String) {
        val storageDir: File? = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                .toString() + File.separator + "NotetakingApp"
        )


        if (storageDir != null) {
            if (!storageDir.exists()) {
                storageDir.mkdir()
            }
        }
        try {
            val mFile = File(storageDir, filename)
            val output: Writer = BufferedWriter(FileWriter(mFile))
            output.write(json)
            output.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Toast.makeText(context, "File stored in " + storageDir?.absolutePath, Toast.LENGTH_LONG)
            .show()
    }

    private fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //Permission is granted
                true
            } else {
                //Permission is revoked
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
                false
            }
        } else {
            // Permission is automatically granted on sdk<23 upon installation
            true
        }
    }
}