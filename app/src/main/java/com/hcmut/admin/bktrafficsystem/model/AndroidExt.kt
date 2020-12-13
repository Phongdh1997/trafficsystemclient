package com.hcmut.admin.bktrafficsystem.model

import android.content.Context
import com.hcmut.admin.bktrafficsystem.R
import com.hcmut.admin.bktrafficsystem.util.ClickDialogListener
import com.hcmut.admin.bktrafficsystem.util.MessageDialog

class AndroidExt {
    fun comfirm(context: Context,
                title: String? = "",
                description: String? = "",
                onYesListener: ClickDialogListener.Yes
    ) {
        MessageDialog(context, title, description, true)
                .setColorTitle(R.color.green)
                .setButtonYesText("Đồng ý")
                .setButtonNoText("Hủy bỏ")
                .setClickYes { onYesListener.onCLickYes() }
                .show()
    }

    fun showErrorDialog(context: Context,
                        description: String? = ""
    ) {
        MessageDialog(context, "Lỗi", description, false)
                .setColorTitle(R.color.red)
                .show()
    }

    fun showSuccessDialog(context: Context,
                          description: String? = "",
                          onOKListener: ClickDialogListener.OK
    ) {
        MessageDialog(context, "Thành công", description, false)
                .setColorTitle(R.color.green)
                .setClickOk { onOKListener.onCLickOK() }
                .show()
    }

    fun showSuccess(context: Context,
                          description: String? = ""
    ) {
        MessageDialog(context, "Thành công", description, false)
                .setColorTitle(R.color.green)
                .show()
    }

    fun showMessageNoAction(context: Context,
                            title: String? = "",
                            description: String? = ""
    ) {
        MessageDialog(context, title, description, false)
                .setColorTitle(R.color.green)
                .setButtonNoText("Đóng lại")
                .show()
    }

    fun showDialog(context: Context,
                          title: String? = "",
                          description: String? = "",
                          onOKListener: ClickDialogListener.Yes
    ) {
        MessageDialog(context, title, description, true)
                .setColorTitle(R.color.green)
                .setButtonYesText("Đồng ý")
                .setButtonNoText("Hủy bỏ")
                .setClickYes { onOKListener.onCLickYes() }
                .show()
    }


    fun showNotifyDialog(context: Context,
                          description: String? = "",
                          onOKListener: ClickDialogListener.OK
    ) {
        MessageDialog(context, "Thông báo", description, false)
                .setColorTitle(R.color.green)
                .setClickOk { onOKListener.onCLickOK() }
                .show()
    }

    fun showAutoDetectDialog(context: Context,
                          description: String? = "",
                            onOKListener: ClickDialogListener.OK
    ) {
        MessageDialog(context, "Tự động cập nhật vận tốc", description, false)
                .setColorTitle(R.color.green)
                .setClickOk { onOKListener.onCLickOK() }
                .show()
    }

    fun showGetDirection(context: Context,
                          description: String? = "",
                          onYesListener: ClickDialogListener.Yes
    ) {
        MessageDialog(context, "Lỗi", description, true)
                .setColorTitle(R.color.green)
                .setButtonYesText("Đồng ý")
                .setButtonNoText("Hủy")
                .setClickYes { onYesListener.onCLickYes() }
                .show()
    }

    fun showTryAgainDialog(context: Context,
                           description: String? = "",
                           clickYesListener: ClickDialogListener.Yes
    ) {
        MessageDialog(context, "Lỗi", description, true)
                .setColorTitle(R.color.green)
                .setButtonYesText("Thử lại")
                .setButtonNoText("Huỷ")
                .setClickYes {
                    clickYesListener.onCLickYes()
                }
                .show()
    }

    fun showNotifyDialog(context: Context,
                         description: String? = "",
                         clickYesListener: ClickDialogListener.Yes,
                         clickNoListener: ClickDialogListener.No
    ) {
        MessageDialog(context, "Cảnh báo", description, true)
                .setColorTitle(R.color.green)
                .setButtonYesText("Tìm đường khác")
                .setButtonNoText("Tìm lại sau")
                .setClickYes {
                    clickYesListener.onCLickYes()
                }
                .setClickNo { clickNoListener.onClickNo() }
                .show()
    }

    fun showConfirmDialog(context: Context,
                          description: String?,
                          clickYesListener: ClickDialogListener.Yes,
                          clickNoListener: ClickDialogListener.No
    ) {
        MessageDialog(context, "Thông báo", "Bạn muốn báo cáo ở $description", true)
                .setColorTitle(R.color.green)
                .setButtonYesText("Xác nhận")
                .setButtonNoText("Hủy")
                .setClickYes { clickYesListener.onCLickYes() }
                .setClickNo { clickNoListener.onClickNo() }
                .show()
    }
}
