package ru.cherryperry.amiami.presentation.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.widget.Toast
import dagger.android.support.DaggerAppCompatActivity
import org.chromium.customtabsclient.shared.CustomTabsHelper
import org.chromium.customtabsdemos.CustomTabActivityHelper
import ru.cherryperry.amiami.R
import javax.inject.Inject

class SingleActivity : DaggerAppCompatActivity() {

    companion object {
        private const val BACK_BUTTON_DEBOUNCE_TIME = 2000L
    }

    @Inject
    lateinit var navigator: Navigator

    private val customTabActivityHelper = CustomTabActivityHelper()
    private var backButtonWasPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            navigator.openList()
        }
    }

    override fun onStart() {
        super.onStart()
        customTabActivityHelper.session
        customTabActivityHelper.bindCustomTabsService(this)
    }

    override fun onStop() {
        super.onStop()
        customTabActivityHelper.unbindCustomTabsService(this)
    }

    override fun onBackPressed() {
        // at least one fragment intercepted onBackPressed action
        if (supportFragmentManager.fragments
                .asSequence()
                .filterIsInstance(OnBackKeyPressedListener::class.java)
                .any { it.onBackPressed() }) {
            return
        }
        if (navigator.canGoBack()) {
            navigator.back()
        } else {
            // close app only after double back click
            if (backButtonWasPressed) {
                backButtonWasPressed = false
                super.onBackPressed()
            } else {
                backButtonWasPressed = true
                window?.decorView?.postDelayed({ backButtonWasPressed = false }, BACK_BUTTON_DEBOUNCE_TIME)
                Toast.makeText(this, R.string.back_button_alert, Toast.LENGTH_SHORT).show()
            }
        }
    }

    internal fun tryOpenCustomTabs(uri: Uri): Boolean {
        if (customTabActivityHelper.mayLaunchUrl(uri, null, null)) {
            val intent = CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.white))
                .build()
            CustomTabsHelper.addKeepAliveExtra(this, intent.intent)
            CustomTabActivityHelper.openCustomTab(this, intent, uri)
            { _, failedUri -> openDefault(failedUri) }
            return true
        }
        return false
    }

    internal fun openDefault(uri: Uri) {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = uri
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, 0)
        }
    }
}
