package sbs.pros.parking.support

import android.content.Intent
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.os.Message
import android.view.View
import android.widget.CheckedTextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_support.*
import sbs.pros.parking.Constants
import sbs.pros.parking.R
import sbs.pros.parking.databinding.FragmentSupportBinding
import sbs.pros.parking.menu.MenuViewModel
import sbs.pros.parking.utils.navigateSafe
import sbs.pros.parking.utils.setSafeOnClickListener
import sbs.pros.parking.utils.viewLifecycleLazy

@AndroidEntryPoint
class SupportFragment() : Fragment(R.layout.fragment_support) {

    private val viewModel by activityViewModels<MenuViewModel>()
    private val binding by viewLifecycleLazy { FragmentSupportBinding.bind(requireView()) }

    private var isThemeSelected = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setTitle("Обратная связь")
        setupFragmentListener()

        val dialog = ThemeFragment()

        with(binding) {

            themeBtn.setSafeOnClickListener {
                dialog.show(parentFragmentManager, THEME_REQUEST_KEY)
            }

            messageField.addTextChangedListener {
                checkButton()
            }

            sendBtn.setSafeOnClickListener {
                sendEmail(themeBtn.text.toString(), messageField.text.toString())
                messageField.text?.clear()
            }
        }
    }


    private fun sendEmail(theme: String, message: String) {
        val email = Constants.EMAIL_ADDRESS


        // define Intent object with action attribute as ACTION_SEND
        val intent = Intent(Intent.ACTION_SEND)

        // add three fields to intent using putExtra function
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        intent.putExtra(Intent.EXTRA_SUBJECT, theme)
        intent.putExtra(Intent.EXTRA_TEXT, message)

        // set type of intent
        intent.type = "message/rfc822"

        startActivity(Intent.createChooser(intent, "Choose an Email client :"))

    }

    private fun setupFragmentListener() {
        setFragmentResultListener(THEME_REQUEST_KEY) { key, bundle ->
            val theme = bundle.getString("theme")
            if (theme != "") theme?.let { binding.themeBtn.text = it }
            checkButton()
            if (binding.themeBtn.text != "Выберите тему") {
                binding.themeBtn.setTextColor(themeBtn.context.getColor(R.color.black_text))
                binding.themeBtn.isActivated = true
                binding.messageFieldContainer.visibility = View.VISIBLE
            }

        }
    }

    private fun checkButton() {
        val themeTxt = binding.themeBtn.text
        isThemeSelected = themeTxt != "Выберите тему"
        binding.sendBtn.isEnabled = binding.messageField.text?.length!! > 9 && isThemeSelected
        binding.sendBtn.alpha = if (binding.sendBtn.isEnabled) 1.0F else  0.6F

    }

    override fun onResume() {
        super.onResume()
        checkButton()
    }

    companion object {
        const val THEME_REQUEST_KEY = "THEME"

    }
}