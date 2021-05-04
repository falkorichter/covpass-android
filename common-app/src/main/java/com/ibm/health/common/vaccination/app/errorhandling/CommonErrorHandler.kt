package com.ibm.health.common.vaccination.app.errorhandling

import androidx.fragment.app.FragmentManager
import com.ibm.health.common.logging.Lumber
import com.ibm.health.common.vaccination.app.R
import com.ibm.health.common.vaccination.app.dialog.DialogModel
import com.ibm.health.common.vaccination.app.dialog.showDialog

public abstract class CommonErrorHandler {

    public fun handleError(error: Throwable, fragmentManager: FragmentManager) {
        Lumber.e(error)
        val dialogModel = getSpecificDialogModel(error) ?: getCommonDialogModel(error)
        showDialog(dialogModel, fragmentManager)
    }

    protected abstract fun getSpecificDialogModel(error: Throwable): DialogModel?

    private fun getCommonDialogModel(error: Throwable): DialogModel =
        if (isConnectionError(error)) {
            DialogModel(
                titleRes = R.string.error_title_connection,
                messageRes = R.string.error_message_connection,
                positiveButtonTextRes = R.string.dialog_button_ok,
                tag = TAG_ERROR_CONNECTION,
            )
        } else {
            DialogModel(
                titleRes = R.string.error_title_general,
                messageRes = R.string.error_message_general,
                positiveButtonTextRes = R.string.dialog_button_ok,
                tag = TAG_ERROR_GENERAL,
            )
        }

    public companion object {
        public const val TAG_ERROR_GENERAL: String = "error_general"
        public const val TAG_ERROR_CONNECTION: String = "error_connection"
    }
}