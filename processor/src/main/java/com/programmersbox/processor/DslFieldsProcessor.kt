package com.programmersbox.processor


import com.google.auto.service.AutoService
import com.programmersbox.processor.APUtils.GetClassValue
import com.programmersbox.processor.APUtils.getTypeMirrorFromAnnotationValue
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import me.eugeniomarletti.kotlin.metadata.shadow.name.FqName
import me.eugeniomarletti.kotlin.metadata.shadow.platform.JavaToKotlinClassMap
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.MirroredTypesException
import javax.lang.model.type.TypeMirror
import kotlin.reflect.KClass


@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(DslFieldsProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class DslFieldsProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {

        var packageName = ""

        val functions = roundEnv.getElementsAnnotatedWith(DslField::class.java).mapNotNull { methodElement ->
            //println("--------------------------------------------")
            //println("$methodElement | ${methodElement.kind}")

            if (methodElement.kind != ElementKind.FIELD) {
                processingEnv.messager.errormessage { "Can only be applied to functions,  element: $methodElement " }
                return false
            }

            (methodElement as? VariableElement)?.let {
                packageName = processingEnv.elementUtils.getPackageOf(methodElement).toString()
                generateNewMethod(it)
            }
        }

        val generatedSourcesRoot: String = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()
        if (generatedSourcesRoot.isEmpty()) {
            processingEnv.messager.errormessage { "Can't find the target directory for generated Kotlin files." }
            return false
        }

        //println(functions.joinToString("\n"))

        if (functions.isNotEmpty()) {

            val file = File(generatedSourcesRoot)
            if (!file.exists()) file.mkdir()
            val fileBuilder = FileSpec.builder(
                packageName,
                "DslFieldsGenerated"
            )

            functions.forEach { fileBuilder.addFunction(it) }
            fileBuilder
                .build()
                //.also { println(it.toString()) }
                .writeTo(file)

        }

        return false
    }

    private fun generateNewMethod(variable: VariableElement): FunSpec {
        val builder = FunSpec
            .builder(variable.getAnnotation(DslField::class.java).name)
            .addModifiers(KModifier.PUBLIC)
            .receiver(variable.enclosingElement.asType().asTypeName())
        try {
            val a: DslField = variable.getAnnotation(DslField::class.java)
            getTypeMirrorFromAnnotationValue(object : GetClassValue {
                override fun execute() {
                    a.dslMarker
                }
            })?.forEach { it?.let { (it.asTypeName().javaToKotlinType() as? ClassName)?.let { builder.addAnnotation(it) } } }
        } catch (e: Exception) {
            builder.addAnnotation(DslFieldMarker::class)
        }
        return builder
            .addParameter("block", variable.asType().asTypeName().javaToKotlinType2())
            .addStatement("${variable.simpleName} = block")
            .build()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> = mutableSetOf(DslField::class.java.canonicalName)
}

object APUtils {
    fun getTypeMirrorFromAnnotationValue(c: GetClassValue): List<TypeMirror?>? {
        try {
            c.execute()
        } catch (ex: MirroredTypesException) {
            return ex.typeMirrors
        }
        return null
    }

    @FunctionalInterface
    interface GetClassValue {
        @Throws(MirroredTypeException::class, MirroredTypesException::class)
        fun execute()
    }
}

annotation class MyAnnotationType(vararg val value: KClass<*> = [])

fun Messager.errormessage(message: () -> String) {
    this.printMessage(javax.tools.Diagnostic.Kind.ERROR, message())
}

fun Messager.noteMessage(message: () -> String) {
    this.printMessage(javax.tools.Diagnostic.Kind.NOTE, message())
}

fun Messager.warningMessage(message: () -> String) {
    this.printMessage(javax.tools.Diagnostic.Kind.WARNING, message())
}

fun Element.javaToKotlinType(): TypeName = asType().asTypeName().javaToKotlinType()

fun TypeName.javaToKotlinType(): TypeName {
    return if (this is ParameterizedTypeName) {
        (rawType.javaToKotlinType() as ClassName).parameterizedBy(*typeArguments.map { it.javaToKotlinType() }.toTypedArray())
    } else {
        val className = JavaToKotlinClassMap.mapJavaToKotlin(FqName(toString()))?.asSingleFqName()?.asString()
        return if (className == null) {
            this
        } else {
            ClassName.bestGuess(className)
        }
    }
}

private fun TypeName.javaToKotlinType2(): TypeName {
    return when (this) {
        is ParameterizedTypeName ->
            (rawType.javaToKotlinType2() as ClassName).parameterizedBy(*typeArguments.map { it.javaToKotlinType2() }.toTypedArray())
        is WildcardTypeName ->
            if (inTypes.isNotEmpty())
                WildcardTypeName.consumerOf(inTypes[0].javaToKotlinType2())
            else
                WildcardTypeName.producerOf(outTypes[0].javaToKotlinType2())
        else -> {
            val className = JavaToKotlinClassMap.mapJavaToKotlin(FqName(toString()))?.asSingleFqName()?.asString()
            if (className == null) {
                this
            } else {
                ClassName.bestGuess(className)
            }
        }
    }
}