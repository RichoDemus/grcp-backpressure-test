package com.richodemus.test.grpc.backpressure.client


fun main() {
    println("hi")

    val client = AsyncClient("localhost", 50051)
//    println(client.greet("richo").get())
    IntRange(0,100)
            .map { client.greet("$it") }
            .map { println("response: ${it.get()}")}
}
