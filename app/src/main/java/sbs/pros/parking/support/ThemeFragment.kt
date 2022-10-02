package sbs.pros.parking.support

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import sbs.pros.parking.R
import sbs.pros.parking.databinding.ThemeFragmentBinding

@AndroidEntryPoint
class ThemeFragment: DialogFragment() {

    private lateinit var binding: ThemeFragmentBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = ThemeFragmentBinding.inflate(layoutInflater)

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.themeAppeal))
            .setPositiveButton(getString(R.string.themeSelect)) { dialog, id ->
                parentFragmentManager.setFragmentResult(
                    SupportFragment.THEME_REQUEST_KEY, bundleOf(
                        "theme" to when (binding.themeGroup.checkedRadioButtonId) {
                            binding.mode.id -> binding.mode.text.toString()
                            binding.others.id -> binding.others.text.toString()
                            binding.rating.id -> binding.rating.text.toString()
                            binding.paymentCheck.id -> binding.paymentCheck.text.toString()
                            binding.refund.id -> binding.refund.text.toString()
                            else -> {}
                        }
                    )
                )
            }
            .setNegativeButton(getString(R.string.themeCancel)) { dialog, id -> }
            .setView(binding.dialogFragment)
            .create()
    }
}