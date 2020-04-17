package com.programmersbox.processor

import java.lang.annotation.ElementType

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD)
annotation class DslField(val name: String)

@DslMarker
annotation class DslFieldMarker