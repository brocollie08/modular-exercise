package com.tlin.secondfeature.repo

import com.example.network.APIWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.math.BigInteger
import javax.inject.Inject

class SecondFeatureRepo @Inject constructor(
    private val apiWorker: APIWorker
) {
    suspend fun calculateAverage(numColors: Int, perColor: Int, selections: Int, iterations: Int): Flow<Float> {
        println(Thread.currentThread())
        val bag = createBag(numColors, perColor)
        var result = 0
        var i = 0
        while (i < iterations) {
            val fromOneIteration = calculateOneIteration(bag.toMutableList(), selections)
            result += fromOneIteration
            i++
        }
        val final = result.toFloat()/iterations
        return flow { emit(final) }
    }

    suspend fun calculateRuns(numColors: Int, perColor: Int, selections: Int): Flow<Int> {
        val theoretical = calculateTheoretical(numColors, perColor, selections)
        val low = theoretical*0.99
        val high = theoretical*1.01
        println("theoretical is $theoretical")
        var result = 0f
        val bag = createBag(numColors, perColor)
        var runs = 0
        while (result < low || result > high) {
            val latest = calculateOneIteration(bag.toMutableList(), selections)
            result = (result*runs+latest)/(runs+1)
            runs ++
        }
        return flow { emit(runs) }
    }

    private fun createBag(numColors: Int, perColor: Int): List<Int> {
        val list = mutableListOf<Int>()
        for ((startingColor, i) in (1..numColors).withIndex()) {
            for (j in 1..perColor) {
                list.add(startingColor)
            }
        }
        return list
    }

    private fun calculateOneIteration(bag: MutableList<Int>, selections: Int): Int {
        val tempBag = bag.toMutableList()
        val set = hashSetOf<Int>()
        var j = 0
        while (j < selections) {
            val picked = tempBag.random()
            set.add(picked)
            tempBag.remove(picked)
            j++
        }
        return set.size
    }

    private fun calculateTheoretical(numColors: Int, perColor: Int, selections: Int): Float {
        val N = numColors*perColor
        val K = numColors
        val first = N pick selections
        val second = N-(N/K) pick selections
        return selections*(1-second.toFloat()/first.toFloat())
    }

    private infix fun Int.pick(k: Int) = this.factorial()/(k.factorial()*(this-k).factorial())

    fun Int.factorial(): BigInteger {
        var result : BigInteger = 1.toBigInteger()
        (1..this).map {
            result *= it.toBigInteger();
        }
        return result;
    }
    //private fun Int.factorial(): BigInteger = BigInteger.valueOf((1..BigInteger.valueOf(this)).reduce { a, b -> BigInteger.valueOf(a) * BigInteger.valueOf(b) })
}