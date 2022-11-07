package com.example.prm_392_nho_ho_ban.handler;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.example.prm_392_nho_ho_ban.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import java.util.Collection;
import java.util.HashSet;

public class EventDecorator implements DayViewDecorator {
    private final HashSet<CalendarDay> dates;
    private final Drawable drawable;

    public EventDecorator(Collection<CalendarDay> dates, Context context) {
        this.dates = new HashSet<CalendarDay>(dates);
        this.drawable = ContextCompat.getDrawable(context, R.drawable.ic_drawing_circle);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(drawable);
    }
}
