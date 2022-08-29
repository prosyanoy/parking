package sbs.pros.parking.app_info

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import sbs.pros.parking.BuildConfig
import sbs.pros.parking.Constants
import sbs.pros.parking.R
import sbs.pros.parking.databinding.AppInfoFragmentBinding
import sbs.pros.parking.menu.MenuViewModel
import sbs.pros.parking.utils.setSafeOnClickListener
import sbs.pros.parking.utils.viewLifecycleLazy

@AndroidEntryPoint
class AppInfoFragment() : Fragment(R.layout.app_info_fragment) {

    private val viewModel by activityViewModels<MenuViewModel>()
    private val binding by viewLifecycleLazy { AppInfoFragmentBinding.bind(requireView()) }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setTitle("О приложении")

        with(binding){

            versionText.text = requireContext().getString(R.string.app_version, BuildConfig.VERSION_NAME)


            yandexPolicy.setSafeOnClickListener {
                val url = Constants.YANDEX_URL
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }

            appPolicy.setSafeOnClickListener {
                val url = Constants.APP_POLICY
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }

            personalData.setSafeOnClickListener {
                val url = Constants.PERSONAL_DATA
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }

        }
    }
}