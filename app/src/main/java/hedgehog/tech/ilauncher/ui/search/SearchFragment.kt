package hedgehog.tech.ilauncher.ui.search

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.fragment.app.Fragment
import hedgehog.tech.ilauncher.app.utils.InsetsUtils
import hedgehog.tech.ilauncher.databinding.FragmentSearchBinding
import hedgehog.tech.ilauncher.ui.home.HomeFragment
import java.net.URLEncoder


class SearchFragment: Fragment(), OnGlobalLayoutListener {

    fun newInstance(): SearchFragment{
        val args = Bundle()
        val fragment = SearchFragment()
        fragment.arguments = args
        return fragment
    }

    private var binding: FragmentSearchBinding? = null
    private var searchButtonWidth = 0f
    private var searchButtonInnerViewWidth = 0f
    private var searchEditRootViewWidth = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.searchCloseBtn?.setOnClickListener {
            binding?.searchEditText?.apply {
                // чтобы скрыть клавиатуру убираем фокус с editText
                clearFocus()
                InsetsUtils.hideKeyboardOnView(this)
                setText("")
            }
            binding?.searchEditFrameRoot?.visibility = View.GONE
            binding?.searchButtonFrame?.visibility = View.VISIBLE
            SearchAnimations.scaleSearchButtonAnimation(
                binding?.searchButtonInnerView, searchButtonInnerViewWidth,
                searchButtonWidth
            )
        }

        binding?.searchEditText?.setOnKeyListener { _, keyCode, _ ->
            // если была нажата кнопка Enter на системной клавиатуре
            if (keyCode == 66){
                val searchURL = binding?.searchEditText?.text.toString()
                val escapedQuery: String = URLEncoder.encode(searchURL, "UTF-8")
                val uri: Uri = Uri.parse("https://srchfeed.com/8y83hh?pid=launcmob&q=$escapedQuery")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
            true
        }

        binding?.searchButtonFrame?.setOnClickListener {
            binding?.searchButtonFrame?.visibility = View.GONE
            binding?.searchEditFrameRoot?.visibility = View.VISIBLE
            SearchAnimations.scaleEditTextAnimation(
                binding?.searchEditFrame, searchEditRootViewWidth
            )
            binding?.searchEditText?.apply {
                // при нажатии на поиск фокусируем каретку на editText и активируем клавиатуру
                requestFocus()
                InsetsUtils.openKeyboardOnView(this)
                setText("")
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        HomeFragment.binding = null
    }

    override fun onResume() {
        super.onResume()
        binding?.searchButtonFrame?.viewTreeObserver?.addOnGlobalLayoutListener(searchButtonFrameListener)
        binding?.searchButtonInnerView?.viewTreeObserver?.addOnGlobalLayoutListener(searchButtonInnerViewListener)
        binding?.searchEditFrameRoot?.viewTreeObserver?.addOnGlobalLayoutListener(searchEditFrameRootListener)
        binding?.searchEditFrameRoot?.visibility = View.GONE
        binding?.searchButtonFrame?.visibility = View.VISIBLE
    }

    private val searchButtonFrameListener = OnGlobalLayoutListener {
        binding?.searchButtonFrame?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
        binding?.searchButtonFrame?.width?.let { width -> searchButtonWidth = width.toFloat() }
    }

    private val searchButtonInnerViewListener = OnGlobalLayoutListener {
        binding?.searchButtonInnerView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
        binding?.searchButtonInnerView?.width?.let { width -> searchButtonInnerViewWidth = width.toFloat() }
    }

    private val searchEditFrameRootListener = OnGlobalLayoutListener {
        binding?.searchEditFrameRoot?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
        binding?.searchEditFrameRoot?.width?.let { width -> searchEditRootViewWidth = width.toFloat() }
    }

    // =================  Для отслеживания момента отрисовки view и получения ее размеров
    // (только в момент выполнения метода onLayout мы можем с достоверностью узнать метрики view)
    override fun onGlobalLayout() { }
}