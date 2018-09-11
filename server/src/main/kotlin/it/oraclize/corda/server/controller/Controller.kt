package it.oraclize.corda.server.controller

import it.oraclize.corda.ExampleFlow
import it.oraclize.corda.server.NodeRPCConnection
import net.corda.core.contracts.ContractState
import net.corda.core.messaging.startFlow
import net.corda.core.messaging.vaultQueryBy
import net.corda.core.utilities.getOrThrow
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * A CorDapp-agnostic controller that exposes standard endpoints.
 */
@RestController
@RequestMapping("/") // The paths for GET and POST requests are relative to this base path.
class Controller(private val rpc: NodeRPCConnection) {

    companion object {
        private val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    private val proxy = rpc.proxy

    @GetMapping(value = "/exampleflow", produces = arrayOf("application/json"))
    private fun cordaexample() = rpc.proxy.startFlow(::ExampleFlow).returnValue.getOrThrow()

}