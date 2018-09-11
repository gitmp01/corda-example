package it.oraclize.corda

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.ProgressTracker


@StartableByRPC
@InitiatingFlow
class ExampleFlow : FlowLogic<Unit>() {

    companion object {
        object QUERYING_ORACLIZE : ProgressTracker.Step("Querying Oraclize")
        object VERIFYING_PROOF : ProgressTracker.Step("Verifying the proof")
        object CREATING_TRANSACTION : ProgressTracker.Step("Creating the transaction")
        object GATHERING_SIGNATURES : ProgressTracker.Step("Gathering signatures")
        object FINALIZING_TRANSACTION : ProgressTracker.Step("Finalizing transaction")

        fun tracker() = ProgressTracker(QUERYING_ORACLIZE, VERIFYING_PROOF,
                CREATING_TRANSACTION, GATHERING_SIGNATURES, FINALIZING_TRANSACTION)
    }

    override val progressTracker = tracker()

    @Suspendable
    override fun call(){
        progressTracker.currentStep = QUERYING_ORACLIZE


        progressTracker.currentStep = VERIFYING_PROOF

        progressTracker.currentStep = CREATING_TRANSACTION

        progressTracker.currentStep = GATHERING_SIGNATURES

        progressTracker.currentStep = FINALIZING_TRANSACTION

    }
}