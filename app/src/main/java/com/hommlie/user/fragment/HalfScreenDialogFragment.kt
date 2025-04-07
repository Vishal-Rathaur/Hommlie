package com.hommlie.user.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hommlie.user.R
import android.view.WindowManager
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.hommlie.user.activity.ActCheckout


class HalfScreenDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_half_screen_dialog, container, false)

        val ivGotit: ImageView = view.findViewById(R.id.iv_gotit)
        ivGotit.setOnClickListener {
            dismiss()
            val mainActivity = activity as? ActCheckout
            mainActivity?.exampleFunction("Hello from Fragment")
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

}