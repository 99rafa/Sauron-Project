package pt.tecnico.sauron.silo;


import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.io.IOException;
import java.util.Scanner;

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
		if (args.length < 1) {
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


		try {
			port1 = Integer.parseInt(args[4]);
		} catch (NumberFormatException e) {
			port1 = 8081;
		}

		zooHost = args[0];
		zooPort = args[1];
		path = "/grpc/sauron/silo/" + args[2];
		host = args[3];
		port = port1;

		try {
			int repN = Integer.parseInt(args[2]);
			final BindableService impl = new SiloServiceImp(repN);


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
			new Thread(()->{
				System.out.println("Enter to terminate");
				new Scanner(System.in).nextLine();
				server.shutdown();
			}).start();

			// Do not exit the main thread. Wait until server is terminated.
			server.awaitTermination();

			System.out.println("> Server Closing");

		} finally {
			if (zkNaming != null) {
				// remove
				zkNaming.unbind(path,host,portBind);
			}
		}
	}
	
}
