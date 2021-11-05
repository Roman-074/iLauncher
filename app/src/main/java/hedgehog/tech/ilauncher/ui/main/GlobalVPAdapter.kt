package hedgehog.tech.ilauncher.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import hedgehog.tech.ilauncher.databinding.ActivityMainBinding
import hedgehog.tech.ilauncher.ui.home.HomeFragment
import hedgehog.tech.ilauncher.ui.search.SearchFragment

/**
        Внешний ViewPager
 */

@Suppress("DEPRECATION")
class GlobalVPAdapter(
        private var viewModel: MainViewModel,
        private var fm: FragmentManager,
        private var mainBinding: ActivityMainBinding
): FragmentPagerAdapter(fm) {

    override fun getCount(): Int = 2

    // выполняется один раз при создании фрагментов
    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> SearchFragment().newInstance()
            else -> HomeFragment(viewModel, mainBinding).newInstance()
        }
    }

}