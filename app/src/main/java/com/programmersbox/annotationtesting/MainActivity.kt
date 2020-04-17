package com.programmersbox.annotationtesting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.programmersbox.processor.DslField

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TestDsl().apply {

        }

    }
}

class TestDsl {

    @DslField(name = "testingThing")
    private var testThing: () -> Unit = {}

    @DslField(name = "actionRun")
    var runAction: () -> Unit = {}

    @DslField(name = "itemNumber")
    private var numberItem = 4

}