package com.byoutline.kickmaterial.model.utils

import android.os.Parcel
import org.joda.time.DateTime
import paperparcel.Adapter
import paperparcel.ProcessorConfig
import paperparcel.TypeAdapter

class DateTimeAdapter : TypeAdapter<DateTime> {

    override fun writeToParcel(dateTime: DateTime, dest: Parcel, flags: Int)
            = dest.writeLong(dateTime.millis)

    override fun readFromParcel(source: Parcel): DateTime
            = DateTime(source.readLong())

    companion object {
        @JvmField val INSTANCE = DateTimeAdapter()
    }
}

@ProcessorConfig(adapters = [Adapter(DateTimeAdapter::class)])
interface PaperParcelConfig