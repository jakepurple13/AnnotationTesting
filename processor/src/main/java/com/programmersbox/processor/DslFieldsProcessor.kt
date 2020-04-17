package com.programmersbox.processor


import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.asTypeName
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(DslFieldsProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class DslFieldsProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        println("WE ARE HERE!!!")
        roundEnv.getElementsAnnotatedWith(DslField::class.java).forEach { methodElement ->
            println("$methodElement | ${methodElement.kind}")



            if (methodElement.kind != ElementKind.FIELD) {
                processingEnv.messager.errormessage { "Can only be applied to functions,  element: $methodElement " }
                return false
            }

            (methodElement as? ExecutableElement)?.parameters?.forEach { variableElement ->
                println("We are here now")
                generateNewMethod(methodElement, variableElement, processingEnv.elementUtils.getPackageOf(methodElement).toString())
            }

            println(methodElement::class.java.simpleName)

            when (methodElement) {
                is ExecutableElement -> println("Executable")
                is VariableElement -> println("Variable")
                is TypeElement -> println("Type")
            }

            (methodElement as? VariableElement)?.let {
                println("We are here now")
                generateNewMethod(it, processingEnv.elementUtils.getPackageOf(methodElement).toString())
            }

            //generateNewMethod(methodElement as VariableElement, processingEnv.elementUtils.getPackageOf(methodElement).toString())

        }

        val functions = roundEnv.getElementsAnnotatedWith(DslField::class.java).mapNotNull { methodElement ->
            println("$methodElement | ${methodElement.kind}")

            if (methodElement.kind != ElementKind.FIELD) {
                processingEnv.messager.errormessage { "Can only be applied to functions,  element: $methodElement " }
                return false
            }

            (methodElement as? VariableElement)?.let {
                println("We are here now")
                generateNewMethod(it, processingEnv.elementUtils.getPackageOf(methodElement).toString())
            }
        }

        val generatedSourcesRoot: String = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()
        if (generatedSourcesRoot.isEmpty()) {
            processingEnv.messager.errormessage { "Can't find the target directory for generated Kotlin files." }
        }

        val file = File(generatedSourcesRoot)
        if (!file.exists()) file.mkdir()
        //val functionPackages = functions.groupBy {  }
        FileSpec.builder("com.programmersbox.dslfields"/*processingEnv.elementUtils.getPackageOf(methodElement).toString()*/, "DslFieldsGenerated")
            .apply { functions.forEach { addFunction(it) } }//.addFunction(funcBuilder.build())
            .build()
            .also { println(it.toString()) }
            .writeTo(file)

        return false
    }

    private fun generateNewMethod(method: ExecutableElement, variable: VariableElement, packageOfMethod: String) {
        println("$method\n$variable\n$packageOfMethod")
        val generatedSourcesRoot: String = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()
        if (generatedSourcesRoot.isEmpty()) {
            processingEnv.messager.errormessage { "Can't find the target directory for generated Kotlin files." }
            return
        }

        val variableAsElement = processingEnv.typeUtils.asElement(variable.asType())
        //val fieldsInArgument = ElementFilter.fieldsIn(variableAsElement.enclosedElements)
        //val annotationArgs = method.getAnnotation(DslField::class.java)//.viewIds

        val funcBuilder = FunSpec
            .builder("${variable.enclosingElement.simpleName}." + variable.getAnnotation(DslField::class.java).name)
            .addModifiers(KModifier.PUBLIC)
            .addAnnotation(DslFieldMarker::class.java)
            .addParameter("block", variableAsElement.asType().asTypeName())
            .addStatement("%S = block", variable.simpleName.toString())
        val file = File(generatedSourcesRoot)
        if (!file.exists()) file.mkdir()
        FileSpec.builder(packageOfMethod, "DslFieldsGenerated")
            .addFunction(funcBuilder.build())
            .build()
            .also { println(it.toString()) }
            //.writeTo(file)
            .writeTo(System.out)

        println(file.readText())
        println("Created")
    }

    private fun generateNewMethod(variable: VariableElement, packageOfMethod: String): FunSpec {
        println("$variable\n$packageOfMethod")

        val variableAsElement = processingEnv.typeUtils.asElement(variable.asType())
        //val fieldsInArgument = ElementFilter.fieldsIn(variableAsElement.enclosedElements)
        //val annotationArgs = method.getAnnotation(DslField::class.java)//.viewIds
        println(variable.enclosingElement)
        println(variableAsElement.asType().toString())
        return FunSpec
            .builder(variable.getAnnotation(DslField::class.java).name)
            .addModifiers(KModifier.PUBLIC)
            .receiver(variable.enclosingElement.asType().asTypeName())
            .addAnnotation(DslFieldMarker::class.java)
            .addParameter("block", variableAsElement.asType().asTypeName())
            .addStatement("${variable.simpleName} = block")
            .build()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> = mutableSetOf(DslField::class.java.canonicalName)
}

fun Messager.errormessage(message: () -> String) {
    this.printMessage(javax.tools.Diagnostic.Kind.ERROR, message())
}

fun Messager.noteMessage(message: () -> String) {
    this.printMessage(javax.tools.Diagnostic.Kind.NOTE, message())
}

fun Messager.warningMessage(message: () -> String) {
    this.printMessage(javax.tools.Diagnostic.Kind.WARNING, message())
}