package ru.cherryperry.amiami.presentation.highlight

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import dagger.android.support.DaggerFragment
import ru.cherryperry.amiami.R
import ru.cherryperry.amiami.presentation.activity.Navigator
import ru.cherryperry.amiami.presentation.base.NotNullObserver
import ru.cherryperry.amiami.presentation.highlight.adapter.HighlightAdapter
import ru.cherryperry.amiami.presentation.util.ViewDelegate
import javax.inject.Inject

class HighlightFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var navigator: Navigator

    private lateinit var highlightViewModel: HighlightViewModel

    private val recyclerView by ViewDelegate<RecyclerView>(R.id.recyclerView)
    private val toolbar by ViewDelegate<Toolbar>(R.id.toolbar)

    private val adapter = HighlightAdapter()
    private val safRuleExport = SafRuleExport()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.highlight, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        highlightViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(HighlightViewModel::class.java)

        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(HighlightSeparatorItemDecoration(context!!))

        // Toolbar
        navigator.configureToolbar(toolbar)
        if (safRuleExport.isAvailable()) {
            toolbar.inflateMenu(R.menu.highlight_menu)
            toolbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_export -> safRuleExport.requestCreateDocument(this)
                    R.id.action_import -> safRuleExport.requestOpenDocument(this)
                }
                true
            }
        }

        highlightViewModel.list.observe(this, NotNullObserver { adapter.submitList(it) })
        highlightViewModel.toastError.observe(this, NotNullObserver {
            Toast.makeText(context!!, it, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (safRuleExport.isAvailable()) {
            safRuleExport.onRequestCreateDocumentComplete(context!!, requestCode, resultCode, data,
                highlightViewModel::exportRules)
            safRuleExport.onRequestOpenDocumentComplete(context!!, requestCode, resultCode, data,
                highlightViewModel::importRules)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
