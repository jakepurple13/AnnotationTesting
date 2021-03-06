package com.programmersbox.processor

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD)
annotation class DslField(val name: String, val dslMarker: KClass<*> = DslFieldMarker::class)

@DslMarker
annotation class DslFieldMarker