package ru.moondi.rallyinfo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import ru.moondi.rallyinfo.databinding.FragmentSiteBinding

class SiteFragment : Fragment() {
    private var urlSiteNews = URL_SITE_NEWS_VALUE
    private lateinit var webView: WebView
    private var _binding: FragmentSiteBinding? = null
    private val localDataSource = LocalDataSource()
    private val binding get() = _binding!!
    private var backPressedTime: Long = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                showDialog()
                  }
        }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getLinkFromFirebase()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSiteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            webView = binding.webView
            webViewSetup()
            savedInstanceState.getBundle("webViewState")?.let { webView.restoreState(it) }
        } else {
            webView = binding.webView
            webViewSetup()

        }
    }

    private fun getLinkFromFirebase() {

        urlSiteNews = localDataSource.getDataUrlSiteNewsFromSharedPreferences()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun webViewSetup() {
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                webView.loadUrl("javascript:(function() { " + "document.getElementsByClassName('block box_raspisanie')[0].style.display='none'; })()")
               /* webView.loadUrl("javascript:var con = document.getElementById('block box_raspisanie'); " +
                        "con.parentNode.removeChild(con); ");*/
                CookieManager.getInstance().setAcceptCookie(true)
                CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
                CookieManager.getInstance().acceptCookie()
                CookieManager.getInstance().flush()
            }
        }
        webView.apply {
            loadUrl(urlSiteNews)
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            settings.allowFileAccess = true
            settings.userAgentString
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val bundle = Bundle()
        webView.saveState(bundle)
        outState.putBundle("webViewState", bundle)
    }


    private fun showDialog(): AlertDialog {
        val builder = AlertDialog.Builder(context)
            .setMessage(R.string.exit_app_show_dialog_text)
            .setCancelable(false)
            .setPositiveButton("да") { _, _ ->
                requireActivity().finish()
            }
            .setNegativeButton("нет"){ _, _ ->
            }
        val alert = builder.create()
        alert.show()
        return alert
    }

    companion object {

        fun newInstance() = SiteFragment()
    }
}