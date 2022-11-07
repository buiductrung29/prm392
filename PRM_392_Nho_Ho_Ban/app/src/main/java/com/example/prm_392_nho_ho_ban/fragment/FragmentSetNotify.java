package com.example.prm_392_nho_ho_ban.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.fragment.app.DialogFragment;
import com.example.prm_392_nho_ho_ban.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FragmentSetNotify extends DialogFragment {

    private TextView noteRemindDate;
    private TextView noteRemindTime;
    private Button remindConfirm;

    private Switch setAlarm;

    private DatePickerDialog.OnDateSetListener setListener;

    private String remindDate;
    private String remindTime;
    private boolean checkAlarm;

    private boolean noteAlarmPreState;

    private int nHour, nMinute;

    private Calendar calendar = Calendar.getInstance();
    private int nYear = calendar.get(Calendar.YEAR);
    private int nMonth = calendar.get(Calendar.MONTH);
    private int nDay = calendar.get(Calendar.DAY_OF_MONTH);

    private int lastSelectedHour = -1;
    private int lastSelectedMinute = -1;

    private void bindingView(View view) throws ParseException {
        noteRemindDate = view.findViewById(R.id.noteRemindDate);
        noteRemindTime = view.findViewById(R.id.noteRemindTime);
        remindConfirm = view.findViewById(R.id.btnConfirm);
        setAlarm = view.findViewById(R.id.swAlarm);
        noteAlarmPreState = this.getArguments().getBoolean("isNotify");
        if(noteAlarmPreState == true) {
            setAlarm.setChecked(true);
            noteRemindDate.setText(getSelectedDate());
            noteRemindTime.setText(getSelectedTime());
        }
    }

    private String getSelectedDate() {
        Date date = new Date(this.getArguments().getLong("dateTimePick"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dataDate = sdf.format(date);
        return dataDate;
    }

    private String getSelectedTime() throws ParseException {
        String finalTime = null;
        Date date = new Date(this.getArguments().getLong("dateTimePick"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String toTime = sdf.format(date);
        Date dateFormatString = sdf.parse(toTime);
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
        finalTime = timeFormatter.format(dateFormatString);
        return finalTime;
    }

    public interface OnBtnSaveClickListener {
        void onClick(String date, String time, boolean alarm);
    }

    private OnBtnSaveClickListener callback;

    public void setOnBtnSaveClickListener(OnBtnSaveClickListener callback) {
        this.callback = callback;
    }

    private void bindingAction() {
        noteRemindTime.setOnClickListener(this::onTimeSelect);
        noteRemindDate.setOnClickListener(this::onDateSelect);
        remindConfirm.setOnClickListener(this::onBtnConfirmClick);
        setAlarm.setOnCheckedChangeListener(this::onAlarmSwitchChange);
    }

    private void onAlarmSwitchChange(CompoundButton compoundButton, boolean alarmState) {
        if (alarmState) {
            checkAlarm = true;
        } else checkAlarm = false;
    }

    private void onDateSelect(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext()
                , new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                nMonth = month;
                String date = day+"/"+(month+1)+"/"+year;
                noteRemindDate.setText(date);
            }
        },nYear,nMonth,nDay);
        datePickerDialog.show();
        Log.i("TungDT", "Alarm state" + noteAlarmPreState);
    }

    private void onTimeSelect(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        nHour = hourOfDay;
                        nMinute = minute;
                        String time = nHour + ":" + nMinute;
                        SimpleDateFormat f24Hour = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hour.parse(time);
                            noteRemindTime.setText(f24Hour.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, 24, 0, true
        );
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.updateTime(nHour,nMinute);
        timePickerDialog.show();
    }

    private void onBtnConfirmClick(View view) {
        if(!noteRemindDate.getText().toString().isEmpty() &&
        !noteRemindTime.getText().toString().isEmpty()) {
            remindDate = noteRemindDate.getText().toString();
            remindTime = noteRemindTime.getText().toString();
            if(this.callback!=null) {
                this.callback.onClick(remindDate, remindTime, checkAlarm);
                closeFragment();
            }
        } else closeFragment();

    }

    private void closeFragment() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .remove(this)
                .commit();
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    public FragmentSetNotify() {
    }

    public static FragmentSetNotify newInstance(String param1, String param2) {
        FragmentSetNotify fragment = new FragmentSetNotify();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_notify, container, false);
        try {
            bindingView(view);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        bindingAction();
        return view;
    }
}