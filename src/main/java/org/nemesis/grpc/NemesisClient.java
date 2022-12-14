package org.nemesis.grpc;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.nemesis.grpc.NemesisGrpc.NemesisBlockingStub;

import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class NemesisClient {
    private static final Logger logger = Logger.getLogger(NemesisClient.class.getName());

    private final ManagedChannel channel;
    private final NemesisBlockingStub blockingStub;

    public NemesisClient(int port) {
        channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build();
        blockingStub = NemesisGrpc.newBlockingStub(channel);
        logger.info("Client started");
    }

    public void stop() throws Exception {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        logger.info("Client shutdown");
    }

    public boolean isRunning() {
        ConnectivityState state = channel.getState(true);
        if (state == ConnectivityState.IDLE) {
            return false;
        }
        if (state == ConnectivityState.TRANSIENT_FAILURE | state == ConnectivityState.SHUTDOWN) {
            if (!channel.isShutdown())
                try {
                    stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            return false;
        }
        return true;
    }

    private String username = "", token = "";

    public JoinReply joinGame(String password) {
        if (!isRunning()) {
            return JoinReply.newBuilder().build();
        }
        return blockingStub.joinGame(JoinRequest.newBuilder().setUsername(username).setPassword(password).build());
    }

    public StatusReply gameStatus() {
        if (!isRunning()) {
            return StatusReply.newBuilder().build();
        }
        return blockingStub.gameStatus(StatusRequest.newBuilder().setUsername(token).setToken(token).build());
    }

    public ChangeReply changeEconomy(String location, boolean upgraded, boolean destroyed) {
        if (!isRunning()) {
            return ChangeReply.newBuilder().build();
        }
        return blockingStub.changeEconomy(ChangeEconomyRequest.newBuilder().setUsername(token).setToken(token)
                .setLocation(location).setUpgrad(upgraded)
                .setDestroy(destroyed).build());
    }

    public ChangeReply changeMilitary(String location, String destination, boolean upgraded, boolean destroyed) {
        if (!isRunning()) {
            return ChangeReply.newBuilder().build();
        }
        return blockingStub.changeMilitary(ChangeMilitaryRequest.newBuilder().setUsername(token).setToken(token)
                .setLocation(location).setUpgrad(upgraded)
                .setDestroy(destroyed).build());
    }

    public ChangeReply changeDevense(String location, boolean upgraded, boolean destroyed) {
        if (!isRunning()) {
            return ChangeReply.newBuilder().build();
        }
        return blockingStub.changeDevense(ChangeDevenseRequest.newBuilder().setUsername(token).setToken(token)
                .setLocation(location).setUpgrad(upgraded)
                .setDestroy(destroyed).build());
    }
}
