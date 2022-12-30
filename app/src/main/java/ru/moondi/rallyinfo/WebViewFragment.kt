package ru.moondi.rallyinfo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import ru.moondi.rallyinfo.databinding.FragmentResultsBinding
import ru.moondi.rallyinfo.databinding.FragmentWebViewBinding


class WebViewFragment : Fragment() {
    private lateinit var urlSite: String
    private lateinit var webView: WebView
    private var _binding: FragmentWebViewBinding? = null
    private val binding get() = _binding!!




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWebViewBinding.inflate(inflater, container, false)

        val args = arguments

        if (args != null) {
            urlSite = args.getString(URL_KEY, DEFAULT_URL_VALUE)
            Log.d("mylogs", urlSite)
            Log.d("mylogs", urlSite)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            webView = binding.webView
            savedInstanceState.getBundle("webViewState")?.let { webView.restoreState(it) }
        } else {
            webView = binding.webView
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
            loadUrl(urlSite)
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

        fun newInstance(bundle: Bundle): WebViewFragment {
            val fragment = WebViewFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
