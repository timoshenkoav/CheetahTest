package com.tunebrains.cheetahtest.pres

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.squareup.picasso.Picasso
import com.tunebrains.cheetahtest.R
import com.tunebrains.cheetahtest.databinding.ActivityMainBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val vm: MainViewModel by viewModel()
    private val orderItemFormatter: OrderItemFormatter by inject()
    private val imageLoader: Picasso by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val mainAdapter =
            AsyncListDifferDelegationAdapter(
                object : DiffUtil.ItemCallback<MainAdapterItem>() {
                    override fun areItemsTheSame(
                        oldItem: MainAdapterItem,
                        newItem: MainAdapterItem
                    ): Boolean {
                        return when {
                            oldItem is CartItem && newItem is CartItem -> {
                                oldItem.data == newItem.data
                            }
                            oldItem is LoadingItem && newItem is LoadingItem -> true
                            oldItem is RetryItem && newItem is RetryItem -> true

                            else -> false
                        }
                    }

                    override fun areContentsTheSame(
                        oldItem: MainAdapterItem,
                        newItem: MainAdapterItem
                    ): Boolean {
                        return areItemsTheSame(oldItem, newItem)
                    }

                },
                loadingDelegate(),
                retryDelegate {
                    vm.retry()
                },
                cartDelegate(imageLoader, orderItemFormatter)
            )
        binding.recycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = mainAdapter
        }
        vm.data.observe(this) { state ->
            val newItems = mutableListOf<MainAdapterItem>()
            binding.totalHolder.isVisible = false
            when (state) {
                is MainUiState.LOADING -> {
                    newItems.add(LoadingItem)
                }
                is MainUiState.Error -> {
                    newItems.add(RetryItem)
                }
                is MainUiState.Data -> {
                    binding.totalHolder.isVisible = true
                    binding.total.text = orderItemFormatter.total(state)
                    newItems.addAll(state.items.map { CartItem(it) })
                }
            }
            mainAdapter.items = newItems
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        (menu.findItem(R.id.appSearchBar).actionView as? SearchView)?.let {
            it.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {

                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    vm.queryChanged(newText?:"")
                    return true
                }

            })
        }
        return super.onCreateOptionsMenu(menu)
    }
}