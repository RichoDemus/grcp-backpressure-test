package com.richodemus.test.grpc.backpressure.server

import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import com.richodemus.test.grpc.backpressure.api.BackpressureGrpc
import com.richodemus.test.grpc.backpressure.api.HelloReply
import com.richodemus.test.grpc.backpressure.api.HelloRequest

fun main() {
    println("hello")

    HelloWorldServer().start()

    System.`in`.read()

}

class HelloWorldServer {

    private var server: Server? = null

    fun start() {
        /* The port on which the server should run */
        val port = 50051
        server = ServerBuilder.forPort(port)
                .addService(GreeterImpl())
                .build()
                .start()
//        logger.log(Level.INFO, "Server started, listening on {0}", port)
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down")
                this@HelloWorldServer.stop()
                System.err.println("*** server shut down")
            }
        })
    }

    fun stop() {
        server?.shutdown()
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    @Throws(InterruptedException::class)
    private fun blockUntilShutdown() {
        server?.awaitTermination()
    }

    internal class GreeterImpl : BackpressureGrpc.BackpressureImplBase() {

        @Synchronized
        override fun sayHello(req: HelloRequest, responseObserver: StreamObserver<HelloReply>) {
            val reply = HelloReply.newBuilder().setMessage("Hello ${req.name}").build()
            println("sleeping...")
            Thread.sleep(100L)
            responseObserver.onNext(reply)
            responseObserver.onCompleted()
        }
    }
}

