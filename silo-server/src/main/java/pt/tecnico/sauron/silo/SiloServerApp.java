package pt.tecnico.sauron.silo;


import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.tecnico.sauron.silo.api.ServerGossipGateway;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.io.IOException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.System.exit;

public class SiloServerApp {

    public static void main(String[] args) throws IOException, InterruptedException, ZKNamingException {
        ZKNaming zkNaming = null;
        System.out.println(SiloServerApp.class.getSimpleName());

        // receive and print arguments
        System.out.printf("Received %d arguments%n", args.length);
        for (int i = 0; i < args.length; i++) {
            System.out.printf("arg[%d] = %s%n", i, args[i]);
        }

        // check arguments
        if (args.length < 5) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s port%n", SiloServerApp.class.getName());
            return;
        }

        final String zooHost;
        final String zooPort;
        final String host;
        final int port;
        final String path;
        final String portBind = args[4];
        int port1;
        int gossipPeriod1 = 30000;
        final int gossipPeriod;

        try {
            port1 = Integer.parseInt(args[4]);
        } catch (NumberFormatException e) {
            port1 = 8081;
        }

        if (args.length == 6)
            gossipPeriod1 = Integer.parseInt(args[5]);


        zooHost = args[0];
        zooPort = args[1];
        path = "/grpc/sauron/silo/" + args[2];
        host = args[3];
        port = port1;
        gossipPeriod = gossipPeriod1;

        try {
            int repN = Integer.parseInt(args[2]);
            final SiloServiceImp impl = new SiloServiceImp(repN);


            // Create a new server to listen on port
            Server server = ServerBuilder.forPort(port).addService(impl).build();

            zkNaming = new ZKNaming(zooHost, zooPort);
            // publish
            zkNaming.rebind(path, host, portBind);


            // Start the server
            server.start();

            // Server threads are running in the background.
            System.out.println("Server started");

            //Server terminates when user presses enter
            new Thread(() -> {
                System.out.println("Enter to terminate");
                new Scanner(System.in).nextLine();
                server.shutdown();
            }).start();

            //Server starts gossip service
            ZKNaming finalZkNaming = zkNaming;
            new Thread(() -> {

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        try {
                            ServerGossipGateway gateway = new ServerGossipGateway(zooHost, zooPort, args[2]);
                            if (finalZkNaming.listRecords("/grpc/sauron/silo").size() > 1) {
                                gateway.gossip(impl.buildGossipRequest());
                                System.out.println("Sent gossip");
                            }
                        } catch (ZKNamingException e) {
                            e.printStackTrace();
                        }
                    }
                }, gossipPeriod, gossipPeriod);
            }).start();

            // Do not exit the main thread. Wait until server is terminated.
            server.awaitTermination();


            System.out.println("> Server Closing");


        } finally {
            if (zkNaming != null) {
                // remove
                zkNaming.unbind(path, host, portBind);
                exit(0);
            }
        }
    }

}
