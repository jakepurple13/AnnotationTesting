package com.programmersbox.annotationtesting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.programmersbox.annotationtesting.next.TestDsls
import com.programmersbox.annotationtesting.next.*
import com.programmersbox.dslannotations.DslField

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TestDsl().apply {
            paramStuffThree { "$it" }
            testingThing { println("Hello") }
            actionRun { println("World") }
        }

        TestDsls().apply {
            paramStuffThree { "$it" }
            testingThing { println("Hello") }
            actionRun { println("World") }
        }

        NewDsl<Int>().apply {

        }

    }

}

@DslMarker
annotation class CustomDslMarker

@DslMarker
annotation class CustomizeDslMarker

class TestDsl {

    @DslField(name = "testingThing", dslMarker = CustomDslMarker::class)
    var testThing: () -> Unit = {}

    @DslField(name = "actionRun", dslMarker = CustomizeDslMarker::class)
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