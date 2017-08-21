package com.byoutline.rx

import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.disposables.Disposable

fun LifecycleProvider<FragmentEvent>.invokeOnFPause(action: () -> Unit): Disposable
        = lifecycle().filter { it == FragmentEvent.PAUSE }
        .firstOrError()
        .subscribe { _ -> action() }

fun LifecycleProvider<ActivityEvent>.invokeOnAPause(action: () -> Unit): Disposable
        = lifecycle().filter { it == ActivityEvent.PAUSE }
        .firstOrError()
        .subscribe { _ -> action() }