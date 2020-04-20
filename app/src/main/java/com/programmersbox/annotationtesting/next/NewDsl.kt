package com.programmersbox.annotationtesting.next

import com.programmersbox.dslannotations.DslClass
import com.programmersbox.dslannotations.DslField

@DslMarker
annotation class CustomsDslMarker

@DslMarker
annotation class CustomizesDslMarker

class TestDsls {

    @DslField(name = "testingThing", dslMarker = CustomsDslMarker::class)
    var testThing: () -> Unit = {}

    @DslField(name = "actionRun", dslMarker = CustomizesDslMarker::class)
    var runAction: () -> Unit = {}

    @DslField(name = "itemNumber")
    var numberItem = 4

    @DslField(name = "paramStuffOne")
    var paramOne: (Int, String) -> Unit = { _, _ -> }

    @DslField(name = "paramStuffTwo")
    var paramTwo: (num: Int) -> Unit = {}

    @DslField(name = "paramStuffThree")
    var paramThree: (Int) -> String = { "$it" }

    @DslField(name = "paramStuffFour")
    var paramFour = fun(_: Int) = Unit

}


@DslClass(dslMarker = CustomizesDslMarker::class)
class NewDsl<T> {
    @DslField("itemNumber")
    var numberItem = 4
    var testThing: () -> Unit = {}
    var runAction: () -> Unit = {}
    var paramOne: (Int, String) -> Unit = { _, _ -> }
    var paramTwo: (Int) -> Unit = {}
    var paramThree: (Int) -> String = { "$it" }
    var paramFour = fun(_: Int) = Unit
    var paramFive = fun(_: T) = Unit

    fun build() = Unit
}

@DslClass(dslMarker = CustomizesDslMarker::class)
class NewDsls<T, R> {
    @DslField("itemNumber")
    var numberItem = 4
    var testThing: () -> Unit = {}
    var runAction: () -> Unit = {}
    var paramOne: (Int, String) -> Unit = { _, _ -> }
    var paramTwo: (Int) -> Unit = {}
    var paramThree: (Int) -> String = { "$it" }
    var paramFour = fun(_: Int) = Unit
    var paramFive = fun(_: T, _: R) = Unit
    var paramSix = fun(_: T) = Unit

    fun build() = Unit
}