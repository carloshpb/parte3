package br.ufu;

import io.atomix.AtomixClient;
import io.atomix.catalyst.transport.netty.NettyTransport;

public class AppClient {

    public static void main(String[] args) {

        AtomixClient client = AtomixClient.builder()
                .withTransport(new NettyTransport())
                .build();

        client.connect(AppServer.cluster)
                .thenRun(() -> {
                    System.out.println("Client is connected to the cluster!");
                });
    }
}
