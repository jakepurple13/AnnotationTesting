package com.programmersbox.dslannotations

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD)
annotation class DslField(val name: String, val dslMarker: KClass<*> = DslFieldMarker::class)

@DslMarker
annotation class DslFieldMarker

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class DslClass(val dslMarker: KClass<*> = DslFieldMarker::class)