/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.byoutline.kickmaterial.projectdetails

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import java.util.*

/**
 * A custom ScrollView that can accept a scroll listener.
 */
class ObservableScrollView(context: Context, attrs: AttributeSet) : ScrollView(context, attrs) {
    private val callbacks = ArrayList<Callbacks>()

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        for (c in callbacks) {
            c.onScrollChanged(l - oldl, t - oldt)
        }
    }

    fun addCallbacks(listener: Callbacks) {
        if (!callbacks.contains(listener)) {
            callbacks.add(listener)
        }
    }

    interface Callbacks {
        fun onScrollChanged(deltaX: Int, deltaY: Int)
    }
}
