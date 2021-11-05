package hedgehog.tech.ilauncher.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import hedgehog.tech.ilauncher.app.utils.InsetsUtils
import hedgehog.tech.ilauncher.app.utils.PrefUtils
import hedgehog.tech.ilauncher.app.variables.PAGE_VP_ON_BOARDING
import hedgehog.tech.ilauncher.databinding.ActivityGetstartedBinding
import hedgehog.tech.ilauncher.ui.main.MainActivity
import hedgehog.tech.ilauncher.ui.onboarding.onboarding.OnBoardingActivity


class GetStartedActivity: AppCompatActivity() {

    private lateinit var binding: ActivityGetstartedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetstartedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!PrefUtils.isFirstOpenApp()){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.getstartedViewpager.adapter = GetStartedAdapter()
        binding.getstartedViewpager.registerOnPageChangeCallback(viewPagerListener)

        binding.getstartedButton.setOnClickListener {
            if (!binding.getstartedCheckbox.isChecked){
                PAGE_VP_ON_BOARDING = 666
            }
            startActivity(Intent(this, OnBoardingActivity::class.java))
            finish()
        }

        binding.getstartedCheckboxTextview.setOnClickListener {
            binding.getstartedCheckbox.isChecked = !binding.getstartedCheckbox.isChecked
        }

    }

    override fun onResume() {
        super.onResume()
        InsetsUtils.hideSystemUI(window, binding.root)
    }

    private var viewPagerListener = object: ViewPager2.OnPageChangeCallback(){
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            PAGE_VP_ON_BOARDING = position
        }
    }

}