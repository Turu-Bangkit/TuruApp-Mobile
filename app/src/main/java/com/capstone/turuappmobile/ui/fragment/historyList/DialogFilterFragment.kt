package com.capstone.turuappmobile.ui.fragment.historyList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.capstone.turuappmobile.databinding.FragmentDialogFilterBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*


class DialogFilterFragment : DialogFragment() {
    private var listener: DataListener? = null

    private var _binding: FragmentDialogFilterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDialogFilterBinding.inflate(inflater, container, false)
        return binding.root
    }


    fun setDialogListener(listener: DataListener) {
        this.listener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageView4.setOnClickListener {
            listener?.onDataReceived("haloo")
            dismiss()
        }

        binding.tglMulaiText.setOnClickListener {
            showDatePickerStart(it)
        }

    }

    private fun showDatePickerStart(view: View) {


        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select dates")
            .setSelection(
                androidx.core.util.Pair(
                    MaterialDatePicker.thisMonthInUtcMilliseconds(),
                    MaterialDatePicker.todayInUtcMilliseconds()
                )
            )
            .build()

        datePicker.show(requireActivity().supportFragmentManager, "datePicker")

        datePicker.addOnPositiveButtonClickListener { selection ->


            val selectedDateStart = Calendar.getInstance()
            val selectedDateEnd = Calendar.getInstance()
            selectedDateStart.timeInMillis = selection.first
            selectedDateEnd.timeInMillis = selection.second
            val formattedDateStart =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDateStart.time)
            val formattedDateEnd = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDateEnd.time)


            binding.tglMulaiText.setText(formattedDateStart + " - " + formattedDateEnd)

        }


    }


}