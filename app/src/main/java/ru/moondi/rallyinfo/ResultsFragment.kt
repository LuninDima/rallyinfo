package ru.moondi.rallyinfo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import ru.moondi.rallyinfo.databinding.FragmentResultsBinding


class ResultsFragment : Fragment() {
    private var urlDakarResults = URL_RESULTS_VALUE
    private lateinit var webView: WebView
    private var _binding: FragmentResultsBinding? = null
    private val binding get() = _binding!!
    private val localDataSource = LocalDataSource()

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getLinkFromFirebase()
    }

    private fun getLinkFromFirebase() {

        urlDakarResults = localDataSource.getDataUrlResultsFromSharedPreferences()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            webView = binding.webViewResults
            savedInstanceState.getBundle("webViewState")?.let { webView.restoreState(it) }
        } else {
            webView = binding.webViewResults
        }

    }

    override fun onResume() {
        super.onResume()
        webViewSetup()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun webViewSetup() {
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                CookieManager.getInstance().flush()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                webView.loadUrl("javascript:(function() { " + "document.getElementsByClassName('rally-page-header')[0].style.display='none'; })()")
                webView.loadUrl("javascript:(function() { " + "document.getElementsByClassName('content')[0].style.display='none'; })()")
                webView.loadUrl("javascript:(function() { " + "document.getElementsByClassName('contel100 contel100--banner')[0].style.display='none'; })()")
                webView.loadUrl("javascript:(function() { " + "document.getElementsByClassName('content-tabnav')[0].style.display='none'; })()")
                webView.loadUrl("javascript:(function() { " + "document.getElementsByClassName('header')[0].style.display='none'; })()")
                webView.loadUrl("javascript:(function() { " + "document.getElementById('header').style.display='none';})()")
                CookieManager.getInstance().flush()

            }
        }

        webView.apply {
            loadUrl(urlDakarResults)
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

    companion object {

        fun newInstance() = ResultsFragment()
    }
}
