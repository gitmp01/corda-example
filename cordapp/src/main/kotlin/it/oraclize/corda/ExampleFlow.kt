package it.oraclize.corda

import co.paralleluniverse.fibers.Suspendable
import it.oraclize.cordapi.OraclizeUtils
import it.oraclize.cordapi.entities.Answer
import it.oraclize.cordapi.entities.ProofType
import it.oraclize.cordapi.flows.OraclizeQueryAwaitFlow
import net.corda.core.contracts.*
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.transactions.LedgerTransaction
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import javax.xml.transform.TransformerConfigurationException


@StartableByRPC
@InitiatingFlow
class ExampleFlow : FlowLogic<SignedTransaction>() {

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
    override fun call() : SignedTransaction{
        progressTracker.currentStep = QUERYING_ORACLIZE
        val query = "json(https://www.therocktrading.com/api/ticker/BTCEUR).result.0.last"
        val answer = subFlow(OraclizeQueryAwaitFlow("URL", query, ProofType.TLSNOTARY, 0))

        progressTracker.currentStep = VERIFYING_PROOF
        val pvt = OraclizeUtils.ProofVerificationTool()

        if (!pvt.verifyProof(answer.proof!!))
            throw FlowException("The proof is not valid")

        progressTracker.currentStep = CREATING_TRANSACTION
        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val builder = TransactionBuilder(notary)



        progressTracker.currentStep = GATHERING_SIGNATURES

        progressTracker.currentStep = FINALIZING_TRANSACTION

    }
}


data class SomeState(val amount: Int, val owner: Party) : ContractState {
    override val participants = listOf(owner)
}


class SomeContract : Contract {

    interface Commands {
        class SomeCommand : Commands, TypeOnlyCommandData()
    }

    override fun verify(tx: LedgerTransaction)  = requireThat {
        val state = tx.outputsOfType<SomeState>().single()
        val answ = tx.commandsOfType<Answer>().single().value

        "The amount should be positive" using (state.amount > 0)
        "The answer is not empty" using (!answ.isEmpty())
    }
}