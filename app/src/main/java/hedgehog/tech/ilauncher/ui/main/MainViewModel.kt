package hedgehog.tech.ilauncher.ui.main

import android.app.Activity
import android.content.Intent
import android.content.pm.ResolveInfo
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hedgehog.tech.ilauncher.app.utils.BlurUtils
import hedgehog.tech.ilauncher.app.utils.PrefUtils
import hedgehog.tech.ilauncher.app.variables.PackageNameDefault
import hedgehog.tech.ilauncher.app.variables.packageManager
import hedgehog.tech.ilauncher.app.variables.packageName
import hedgehog.tech.ilauncher.data.model.GridItemModel
import hedgehog.tech.ilauncher.databinding.ActivityMainBinding
import hedgehog.tech.ilauncher.databinding.FragmentHomeBinding
import hedgehog.tech.ilauncher.ui.home.HomeVPAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.ceil

class MainViewModel : ViewModel() {

    fun getApps(
        binding: FragmentHomeBinding?,
        mainBinding: ActivityMainBinding?,
        activity: Activity
    ) {
        viewModelScope.launch {
            // 1) получаем отсортированный список приложений, установленных на телефоне
            val apps = appList()
            val appSize = apps.size.toDouble()
            // 2) вычисляем количество страниц view pager
            // TODO пока что страницы 4х5, возможно в будущем будут адаптивными по высоте
            val viewPagerSize: Int = ceil(appSize / 20).toInt()
            // 3) разбиваем массив моделей списка приложений на подмассивы
            // по 20 элементов на каждую страницу view pager'а
            val viewPagerAppList = apps.chunked(20) as MutableList<MutableList<GridItemModel>>
            // 4) передаем данные на отрисовку в адаптер
            withContext(Dispatchers.Main) {
                setMainBinding(binding, mainBinding, activity, viewPagerSize, viewPagerAppList)
            }
        }
    }

    private fun setMainBinding(
        binding: FragmentHomeBinding?,
        mainBinding: ActivityMainBinding?,
        activity: Activity,
        viewPagerSize : Int,
        viewPagerAppList : MutableList<MutableList<GridItemModel>>
    ){
        binding?.apply {
            homeLoadLayout.visibility = View.GONE
            homeAppBar.visibility = View.VISIBLE
            homeViewpager.visibility = View.VISIBLE
            homeViewpager.adapter = HomeVPAdapter(activity, viewPagerSize, viewPagerAppList)
            homePageIndicator.visibility = View.VISIBLE
            homePageIndicator.attachTo(binding.homeViewpager)
        }
        // если первое открытие приложения то показываем хелп
        if (PrefUtils.isFirstOpenApp()) {
            mainBinding?.mainHelpLayout?.helpRootLayout?.visibility = View.VISIBLE
            BlurUtils.blurView(activity, mainBinding?.mainHelpLayout?.helpBlurView, 24f)
            PrefUtils.setFirstOpenApp(false)
        }
    }

    // ================================= Suspend functions  ====================================
    private suspend fun appList(): MutableList<GridItemModel> {
        var defPacList: MutableList<ResolveInfo>
        var resultAppPac: MutableList<GridItemModel>
        withContext(Dispatchers.IO) {
            // 1) читаем список активити с CATEGORY_LAUNCHER на устройстве
            val mainIntent = Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER)
            defPacList = packageManager.queryIntentActivities(mainIntent, 0)
            // 2) фильтруем 4 приложения, которые находятся в нижнем апбаре
            filterDoublePackages(defPacList)
            // 3) мапим модели данных
            resultAppPac = mapAppModels(defPacList)
        }

        return resultAppPac
    }


    // ==============================  Вспомогательные функции  =================================
    private fun mapAppModels(mapPac: MutableList<ResolveInfo>): MutableList<GridItemModel> {
        val resultAppPac: MutableList<GridItemModel> = mutableListOf()
        for (i in 0 until mapPac.size) {
                resultAppPac.add(
                    i, GridItemModel(
                        i,
                        mapPac[i].loadLabel(packageManager).toString(),
                        mapPac[i].loadIcon(packageManager),
                        mapPac[i].activityInfo.packageName,
                        mapPac[i].activityInfo.name,
                    )
                )
        }
        return resultAppPac
    }

    private fun filterDoublePackages(mapPac: MutableList<ResolveInfo>){
        // рабоатет аналогично removeIf (сокращение использования итератора) на всех API
        mapPac.removeAll {
            it.activityInfo.packageName.contains(PackageNameDefault.getCamera())
                    || it.activityInfo.packageName.contains(PackageNameDefault.getDialer())
                    || it.activityInfo.packageName.contains(PackageNameDefault.getSms())
                    || it.activityInfo.packageName.contains(PackageNameDefault.getBrowser())
                    || it.activityInfo.packageName.contains(packageName)
        }
    }

}
