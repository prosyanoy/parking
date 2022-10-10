package sbs.pros.parking.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import sbs.pros.parking.R
import sbs.pros.parking.databinding.MenuBottomSheetPayBinding
import java.lang.Thread.sleep

class BottomSheetDialogPay : BottomSheetDialogFragment() {

    lateinit var binding: MenuBottomSheetPayBinding

   override fun getTheme() = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MenuBottomSheetPayBinding.bind(inflater.inflate(R.layout.menu_bottom_sheet_pay, container))
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBehavior.peekHeight = bottomSheet.height
        }
        with(binding){
            backFromPay.setOnClickListener{
                sleep(400L)
                dismiss()
            }
        }
    }
}




