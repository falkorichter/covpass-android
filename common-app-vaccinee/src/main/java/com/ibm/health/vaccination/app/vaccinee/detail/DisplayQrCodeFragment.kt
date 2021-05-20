package com.ibm.health.vaccination.app.vaccinee.detail

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.ensody.reactivestate.autoRun
import com.ensody.reactivestate.dispatchers
import com.ensody.reactivestate.get
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.ibm.health.common.android.utils.viewBinding
import com.ibm.health.common.navigation.android.FragmentNav
import com.ibm.health.common.navigation.android.getArgs
import com.ibm.health.common.navigation.android.triggerBackPress
import com.ibm.health.common.vaccination.app.BaseBottomSheet
import com.ibm.health.vaccination.app.vaccinee.R
import com.ibm.health.vaccination.app.vaccinee.databinding.DisplayQrCodeBottomsheetContentBinding
import com.ibm.health.vaccination.app.vaccinee.dependencies.vaccineeDeps
import com.ibm.health.vaccination.app.vaccinee.storage.GroupedCertificatesList
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.invoke
import kotlinx.parcelize.Parcelize

@Parcelize
internal class DisplayQrCodeFragmentNav(val certId: String) : FragmentNav(DisplayQrCodeFragment::class)

/**
 * Fragment which displays the QR code of a vaccination on a bottom sheet.
 */
internal class DisplayQrCodeFragment : BaseBottomSheet() {

    private val args: DisplayQrCodeFragmentNav by lazy { getArgs() }

    override val buttonTextRes by lazy {
        R.string.vaccination_certificate_detail_view_qrcode_screen_action_button_title
    }

    private val binding by viewBinding(DisplayQrCodeBottomsheetContentBinding::inflate)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheetBinding.bottomSheetSubtitle.setText(
            R.string.vaccination_certificate_detail_view_qrcode_screen_message
        )
        bottomSheetBinding.bottomSheetSubtitle.isVisible = true
        autoRun { updateViews(get(vaccineeDeps.certRepository.certs)) }
    }

    private fun updateViews(certificateList: GroupedCertificatesList) {
        val cert = certificateList.getCombinedCertificate(args.certId) ?: return
        bottomSheetBinding.bottomSheetTitle.text = getString(
            R.string.vaccination_certificate_detail_view_qrcode_screen_title,
            cert.vaccinationCertificate.currentSeries,
            cert.vaccinationCertificate.completeSeries,
            cert.vaccinationCertificate.fullName
        )
        launchWhenStarted {
            binding.displayQrImageview.setImageBitmap(
                generateQRCode(cert.vaccinationQrContent)
            )
        }
    }

    override fun onActionButtonClicked() {
        triggerBackPress()
    }

    private suspend fun generateQRCode(qrContent: String): Bitmap {
        return dispatchers.default {
            BarcodeEncoder().encodeBitmap(
                qrContent,
                BarcodeFormat.QR_CODE,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.widthPixels,
                mapOf(EncodeHintType.MARGIN to 0)
            )
        }
    }
}