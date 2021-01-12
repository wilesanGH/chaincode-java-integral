/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.util.ArrayList;
import java.util.List;

@Contract(
        name = "basic",
        info = @Info(
                title = "integral Transfer",
                description = "The hyperlegendary integral transfer",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "a.transfer@example.com",
                        name = "Adrian Transfer",
                        url = "https://hyperledger.example.com")))
@Default
public final class IntegralTransfer implements ContractInterface {

    private final Genson genson = new Genson();

    /**
     * Creates some initial integrals on the ledger.
     *
     * @param ctx the transaction context
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void InitIntegralLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        CreateIntegral(ctx, "integral1", "user1", "e1", "in", 20, "2020-12-05 12:00:00");
        CreateIntegral(ctx, "integral2", "user2", "e2", "in", 30, "2020-12-05 12:00:00");
        CreateIntegral(ctx, "integral3", "user3", "e3", "in", 40, "2020-12-05 12:00:00");
        CreateIntegral(ctx, "integral4", "user4", "e4", "out", 60, "2020-11-05 12:00:00");
        CreateIntegral(ctx, "integral5", "user5", "e5", "in", 70, "2021-12-05 12:00:00");
        CreateIntegral(ctx, "integral6", "user6", "e6", "out", 30, "2020-12-05 12:00:00");

    }

    /**
     * Creates a new integral on the ledger.
     *
     * @param ctx        the transaction context
     * @param integralId the ID of the new integral
     * @param userId     the ID of the new integral
     * @param eventId    the color of the new integral
     * @param type       the size for the new integral
     * @param number     the owner of the new integral
     * @return the created integral
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Integral CreateIntegral(
            final Context ctx,
            final String integralId,
            final String userId,
            final String eventId,
            final String type,
            final int number,
            final String createDate
    ) {
        ChaincodeStub stub = ctx.getStub();

        if (IntegralExists(ctx, integralId)) {
            String errorMessage = String.format("Integral %s already exists", integralId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, IntegralTransferErrors.ASSET_ALREADY_EXISTS.toString());
        }

        Integral integral = new Integral(integralId, userId, eventId, type, number, createDate);
        String integralJSON = genson.serialize(integral);
        stub.putStringState(integralId, integralJSON);

        return integral;
    }

    /**
     * Retrieves an integral with the specified ID from the ledger.
     *
     * @param ctx        the transaction context
     * @param integralID the ID of the integral
     * @return the integral found on the ledger if there was one
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Integral ReadIntegral(final Context ctx, final String integralID) {
        ChaincodeStub stub = ctx.getStub();
        String integralJSON = stub.getStringState(integralID);

        if (integralJSON == null || integralJSON.isEmpty()) {
            String errorMessage = String.format("IntegralIntegral %s does not exist", integralID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, IntegralTransferErrors.ASSET_NOT_FOUND.toString());
        }

        Integral integral = genson.deserialize(integralJSON, Integral.class);
        return integral;
    }

    /**
     * Updates the properties of an integral on the ledger.
     *
     * @param ctx        the transaction context
     * @param integralId the ID of the new integral
     * @param userId     the ID of the new integral
     * @param eventId    the color of the new integral
     * @param type       the size for the new integral
     * @param number     the owner of the new integral
     * @return the transferred integral
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Integral UpdateIntegral(
            final Context ctx,
            final String integralId,
            final String userId,
            final String eventId,
            final String type,
            final int number,
            final String createDate
    ) {
        ChaincodeStub stub = ctx.getStub();

        if (!IntegralExists(ctx, integralId)) {
            String errorMessage = String.format("Integral %s does not exist", integralId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, IntegralTransferErrors.ASSET_NOT_FOUND.toString());
        }

        Integral newIntegral = new Integral(
                integralId,
                userId,
                eventId,
                type,
                number,
                createDate
        );
        String newIntegralJSON = genson.serialize(newIntegral);
        stub.putStringState(integralId, newIntegralJSON);

        return newIntegral;
    }

    /**
     * Deletes integral on the ledger.
     *
     * @param ctx        the transaction context
     * @param integralID the ID of the integral being deleted
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void DeleteIntegral(final Context ctx, final String integralID) {
        ChaincodeStub stub = ctx.getStub();

        if (!IntegralExists(ctx, integralID)) {
            String errorMessage = String.format("Integral %s does not exist", integralID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, IntegralTransferErrors.ASSET_NOT_FOUND.toString());
        }

        stub.delState(integralID);
    }

    /**
     * Checks the existence of the integral on the ledger
     *
     * @param ctx        the transaction context
     * @param integralID the ID of the integral
     * @return boolean indicating the existence of the integral
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean IntegralExists(final Context ctx, final String integralID) {
        ChaincodeStub stub = ctx.getStub();
        String integralJSON = stub.getStringState(integralID);

        return (integralJSON != null && !integralJSON.isEmpty());
    }

    /**
     * Changes the owner of a integral on the ledger.
     *
     * @param ctx        the transaction context
     * @param integralID the ID of the integral being transferred
     * @param newNumber  the new number
     * @return the updated integral
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Integral TransferIntegral(final Context ctx, final String integralID, final int newNumber) {
        ChaincodeStub stub = ctx.getStub();
        String integralJSON = stub.getStringState(integralID);

        if (integralJSON == null || integralJSON.isEmpty()) {
            String errorMessage = String.format("Integral %s does not exist", integralID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, IntegralTransferErrors.ASSET_NOT_FOUND.toString());
        }

        Integral integral = genson.deserialize(integralJSON, Integral.class);

        Integral newIntegral = new Integral(
                integral.getIntegralId(),
                integral.getUserId(),
                integral.getEventId(),
                integral.getType(),
                newNumber,
                integral.getCreateDate()
        );
        String newIntegralJSON = genson.serialize(newIntegral);
        stub.putStringState(integralID, newIntegralJSON);

        return newIntegral;
    }

    /**
     * Retrieves all integrals from the ledger.
     *
     * @param ctx the transaction context
     * @return array of integrals found on the ledger
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllIntegrals(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        List<Integral> queryResults = new ArrayList<Integral>();

        // To retrieve all integrals from the ledger use getStateByRange with empty startKey & endKey.
        // Giving empty startKey & endKey is interpreted as all the keys from beginning to end.
        // As another example, if you use startKey = 'integral0', endKey = 'integral9' ,
        // then getStateByRange will retrieve integral with keys between integral0 (inclusive) and integral9 (exclusive) in lexical order.
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result : results) {
            Integral integral = genson.deserialize(result.getStringValue(), Integral.class);
            queryResults.add(integral);
            System.out.println(integral.toString());
        }

        final String response = genson.serialize(queryResults);

        return response;
    }

    private enum IntegralTransferErrors {
        ASSET_NOT_FOUND,
        ASSET_ALREADY_EXISTS
    }
}
