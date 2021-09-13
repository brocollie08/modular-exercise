package com.tlin.secondfeature.viewModel

import androidx.lifecycle.*
import com.tlin.secondfeature.repo.SecondFeatureRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class FeatureTwoViewModel @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val featureRepo: SecondFeatureRepo,
    private val stateHandle: SavedStateHandle
): ViewModel() {

    val result: LiveData<Float>
        get() = _result
    private val _result: MutableLiveData<Float> = MutableLiveData(0f)

    /*val numPicked: Int
        get() = _numPicked
    private var _numPicked: Int = -1

    val numColors: Int
        get() = _numColors
    private var _numColors: Int = -1

    val perColor: Int
        get() = _perColor
    private var _perColor: Int = -1*/

    /*private val linkedList: LinkedList<Pair<String, Int>> = LinkedList<Pair<String, Int>>().apply {
        for (node in listOf(
            Pair("How many colors are there", -1),
            Pair("How many balls are there per color", -1),
            Pair("How many balls will be picked", -1)
        )) { this.addAtIndex(this.length, node) }
    }

    private var currentNode: LinkedList<Pair<String, Int>>.Node? = linkedList.head

    fun goNext(input: Int) {
        currentNode?.value = Pair(currentNode?.value?.first!!, input)
        currentNode = currentNode?.next
        currentNode?.value?.first?.let { _hintText.value = it }
        _buttonText.value = if (isReady()) "Calculate" else "Next"
    }*/

    fun calculateAverage(numColors: Int, perColor: Int, picked: Int, iterations: Int) {
        viewModelScope.launch(coroutineScope.coroutineContext) {
            featureRepo.calculateAverage(numColors, perColor, picked, iterations).collect {
                _result.postValue(it)
            }
        }
    }

    fun calculateRuns(numColors: Int, perColor: Int, picked: Int) {
        viewModelScope.launch(coroutineScope.coroutineContext) {
            featureRepo.calculateRuns(numColors, perColor, picked).collect {
                //_result.postValue(it)
            }
        }
    }

    //private fun isReady(): Boolean = linkedList.firstOrNull { it.value ?.second == -1 } == null
}

fun <T: Any> LinkedList<T>.firstOrNull(block: (LinkedList<T>.Node) -> Boolean): LinkedList<T>.Node? {
    var head = this.head
    while (head != null && !block(head)) {
        head = head.next
    }
    return head
}


//copy pasted, did not write this
class LinkedList<T: Any> {
    var head: Node? = null
    var tail: Node? = null
    var length: Int = 0
    inner class Node(var value: T?){
        var next: Node? = null
    }
    /* Add a node before the first element of the linked list */
    fun addAtHead(value: T?){
        val h = this.head
        val newNode = Node(value)
        newNode.next = this.head
        head = newNode
        if (h == null) tail = newNode
        this.length++
    }
    /* Append a node to the last element of the linked list. */
    fun addAtTail(value: T?){
        var h = head
        val newNode = Node(value)
        newNode.next = null
        while (h!!.next !=null) h = h.next
        h.next = newNode
        tail = newNode
        this.length++
    }
    /* Add a node before the index-th node in the linked list */
    fun addAtIndex(index: Int, value: T?){
        var h = head
        var newNode = Node(value)
        var counter = 0
        if (index < 0 || index > this.length) return
        if (index == 0) {
            addAtHead(value)
            return
        }
        if (index == this.length) {
            addAtTail(value)
            return
        }
        while (counter != index-1){
            h = h!!.next
            counter++
        }
        newNode.next = h!!.next
        h.next = newNode
        this.length++
    }
    /* Delete the index-th node in the linked list, if the index is valid. */
    fun deleteAtIndex(index: Int) {
        var curr = this.head
        var prev: Node? = null
        var counter = 0
        if (index < 0 || index >= this.length) return
        if (index == 0){
            head = curr!!.next
            this.length--
            return
        }
        while (counter != index){
            prev = curr
            curr = prev!!.next
            counter++
        }
        prev!!.next = curr!!.next
        if (index == length-1) tail = prev
        this.length--
    }
    /*  Get the value of the index-th node in the linked list. If the index is invalid, return -1. */
    fun get(index: Int): Any?{
        var h = head
        var counter = 0
        if (index < 0 || index >= this.length) return -1
        while (counter != index){
            h = h!!.next
            counter++
        }
        return h!!.value
    }

}