package com.byoutline.kickmaterial.utils;

import android.os.Parcel;
import org.joda.time.DateTime;
import org.parceler.converter.NullableParcelConverter;

/**
 * Created by Sebastian Kacprzak on 25.03.15.
 */
public class DateTimeParcelConverter extends NullableParcelConverter<DateTime> {

    @Override
    public void nullSafeToParcel(DateTime input, Parcel parcel) {
        parcel.writeLong(input.getMillis());
    }

    @Override
    public DateTime nullSafeFromParcel(Parcel parcel) {
        return new DateTime(parcel.readLong());
    }
}
