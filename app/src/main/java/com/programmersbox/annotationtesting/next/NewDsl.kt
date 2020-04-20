package com.programmersbox.annotationtesting.next

import com.programmersbox.processor.DslField

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
    var paramTwo: (Int) -> Unit = {}

    @DslField(name = "paramStuffThree")
    var paramThree: (Int) -> String = { "$it" }

    @DslField(name = "paramStuffFour")
    var paramFour = fun(_: Int) = Unit

}