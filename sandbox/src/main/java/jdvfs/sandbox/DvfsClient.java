package jdvfs.sandbox;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class DvfsClient {
    public static void main(String[] args) {
        System.out.println("Opening the channel");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", DvfsServer.PORT)
            .usePlaintext()
            .build();

        DvfsServiceGrpc.DvfsServiceBlockingStub stub = DvfsServiceGrpc.newBlockingStub(channel);

        System.out.println("Sending the request");
        GetDvfsCpusResponse response = stub.getDvfsCpus(GetDvfsCpusRequest.getDefaultInstance());
        System.out.println("Response received from server:\n" + response);

        channel.shutdown();
    }
}
