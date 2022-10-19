package ru.moondi.rallyinfo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import ru.moondi.rallyinfo.databinding.FragmentStreamBinding

class StreamFragment : Fragment() {
    private var streamUrl = URL_STREAM_VALUE
    private lateinit var webView: WebView
    private lateinit var _binding: FragmentStreamBinding
    private val binding get() = _binding
    private val localDataSource = LocalDataSource()


    override fun onAttach(context: Context) {
        super.onAttach(context)
    /*    val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)*/
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getLinkFromFirebase()
    }

    private fun getLinkFromFirebase() {
        streamUrl = localDataSource.getDataUrlStreamFromSharedPreferences()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentStreamBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            webView = binding.webViewStream
            savedInstanceState.getBundle("webViewState")?.let { webView.restoreState(it) }
        } else {
            webView = binding.webViewStream
        }
        webViewSetup()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun webViewSetup() {
        webView.webViewClient = object : WebViewClient() {
            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
                CookieManager.getInstance().flush()
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                CookieManager.getInstance().flush()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                CookieManager.getInstance().flush()
            }
        }

        webView.apply {
            loadUrl(streamUrl)
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            settings.allowFileAccess = true
            settings.userAgentString
        }
    }

    private fun hideSystemBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(requireActivity().window.decorView) ?: return
        // Configure the behavior of the hidden system bars
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }


    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    override fun onResume() {
        super.onResume()

        webView.webChromeClient = object : WebChromeClient() {

            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                super.onShowCustomView(view, callback)
                hideSystemBars()
                binding.webViewStream.visibility = View.GONE
                binding.customView.visibility = View.VISIBLE
                binding.customView.addView(view)


            }

            override fun onHideCustomView() {
                super.onHideCustomView()
                activity?.actionBar?.show()
                binding.webViewStream.visibility = View.VISIBLE
                binding.customView.visibility = View.GONE
            }
        }
    }

    companion object {

        fun newInstance() =
            StreamFragment()
    }
}