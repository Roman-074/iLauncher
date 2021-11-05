package hedgehog.tech.ilauncher.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import hedgehog.tech.ilauncher.R
import hedgehog.tech.ilauncher.data.model.GridItemModel
import hedgehog.tech.ilauncher.databinding.ActivityMainBinding
import hedgehog.tech.ilauncher.databinding.FragmentHomeBinding
import hedgehog.tech.ilauncher.ui.fastsettings.FastSettingsHelper
import hedgehog.tech.ilauncher.ui.main.MainViewModel


class HomeFragment(
    private var viewModel: MainViewModel,
    private var mainBinding: ActivityMainBinding
): Fragment() {

    fun newInstance(): HomeFragment {
        val args = Bundle()
        val fragment = HomeFragment(viewModel, mainBinding)
        fragment.arguments = args
        return fragment
    }

    // пока что оставим этот костыль, позже нужно будет исправить баг
    companion object{
        var binding: FragmentHomeBinding? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // загружаем и отображаем список установленных на телефоне приложение
        viewModel.getApps(binding, mainBinding, requireActivity())

        // формируем нижнее меню, с функцией перетаскивания иконок
//        binding?.homeBottomMenu?.apply {
//            val customAdapter = DragHomeItemAdapter(createBottomMenuIcons())
//            val flexLayout = FlexboxLayoutManager(context)
//            flexLayout.apply {
//                justifyContent = JustifyContent.SPACE_EVENLY   // выравнивание элементов по главной оси
//                alignItems = AlignItems.CENTER                 // выравнивание элементов по поперечной оси
//                flexDirection = FlexDirection.ROW              // направление оси
//                flexWrap = FlexWrap.NOWRAP                       // <однострочный> контейнер
//            }
//            layoutManager = flexLayout
//            adapter = customAdapter
//            setOnDragListener(customAdapter.dragHomeItemInstance)
//        }

        binding?.homeAppbarPhone?.setOnClickListener { FastSettingsHelper.openCaller() }
        binding?.homeAppbarMessages?.setOnClickListener { FastSettingsHelper.openMessage() }
        binding?.homeAppbarSafari?.setOnClickListener { FastSettingsHelper.openSafari() }
        binding?.homeAppbarCamera?.setOnClickListener { FastSettingsHelper.openCamera(requireContext()) }
    }

    private fun createBottomMenuIcons(): List<GridItemModel> {
        val itemPhone = GridItemModel(666, "Phone",
            ResourcesCompat.getDrawable(resources, R.drawable.app_bar_phone, context?.theme)!!,
            "", "")
        val itemMessages = GridItemModel(666, "Messages",
            ResourcesCompat.getDrawable(resources, R.drawable.app_bar_messages, context?.theme)!!,
            "", "")
        val itemSafari = GridItemModel(666, "Safari",
            ResourcesCompat.getDrawable(resources, R.drawable.app_bar_safari, context?.theme)!!,
            "", "")
        val itemCamera = GridItemModel(666, "Camera",
            ResourcesCompat.getDrawable(resources, R.drawable.app_bar_camera, context?.theme)!!,
            "", "")
        return listOf(itemPhone, itemMessages, itemSafari, itemCamera)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}