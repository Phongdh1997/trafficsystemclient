package com.hcmut.admin.bktrafficsystem.util

class ClickDialogListener {
    interface Yes {
        fun onCLickYes()
    }

    interface OK {
        fun onCLickOK()
    }

    interface No {
        fun onClickNo()
    }
}