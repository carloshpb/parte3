package br.ufu;

import io.atomix.AtomixReplica;
import io.atomix.catalyst.transport.Address;

import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 *
 */
public class AppServer
{

    public static volatile List<Address> cluster;

    static {
        cluster = Arrays.asList(
                new Address("localhost", 8700),
                new Address("localhost", 8701),
                new Address("localhsot", 8702));
    }

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

        AtomixReplica replica1 = AtomixReplica
                .builder(cluster.get(0))
                .build();
        replica1.bootstrap(cluster).join();

        AtomixReplica replica2 = AtomixReplica
                .builder(cluster.get(1))
                .build();

        replica2.bootstrap(cluster).join();

        AtomixReplica replica3 = AtomixReplica
                .builder(cluster.get(2))
                .build();

        replica3.bootstrap(cluster).join();
    }
}

