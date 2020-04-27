package pt.tecnico.sauron.spotter;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import pt.tecnico.sauron.silo.client.SiloFrontend;
import pt.tecnico.sauron.silo.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class SpotterApp {

    public static void main(String[] args) throws ZKNamingException {
        SiloFrontend siloFrontend;
        try {
            System.out.println(SpotterApp.class.getSimpleName());
            System.out.println("> Spotter client started");

            Scanner scanner = new Scanner(System.in);

            boolean exit = false;

            //verifies length of args
            if (args.length < 2) {
                throw new IOException();

            } else if (args.length > 3) {
                throw new IOException();
            }

            final String host = args[0];
            final String port = args[1];
            final String repN;
            if (args.length == 3)
                repN = args[2];
            else
                repN = "";

            final String target = host + ":" + port;

            final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

            siloFrontend = new SiloFrontend(host, port, repN);

            while (scanner.hasNextLine()) {
                try {
                    String[] spotterTokens = scanner.nextLine().split(" ");

                    //verifies if the command and arguments are valid. If not, asks for next command
                    while (!checkCommand(spotterTokens)) {
                        System.out.println("Invalid Arguments!");
                        spotterTokens = scanner.nextLine().split(" ");
                    }

                    final String command = spotterTokens[0];

                    //exits from spotter client
                    switch (command) {
                        case "exit":
                            exit = true;

                            break;
                        case "ping": {

                            final String name = spotterTokens[1];

                            PingRequest request = PingRequest.newBuilder().setInputCommand(name).build();
                            PingResponse response = siloFrontend.ctrlPing(request);
                            System.out.println(response.getOutputText());
                            break;
                        }
                        case "init": {

                            InitRequest request = InitRequest.newBuilder().build();
                            siloFrontend.ctrlInit(request);
                            System.out.println("Nothing to be configured!");
                            break;
                        }
                        case "clear": {

                            ClearRequest request = ClearRequest.newBuilder().clear().build();
                            siloFrontend.ctrlClear(request);
                            System.out.println("System is now empty!");

                            break;
                        }
                        case "help":

                            System.out.println("-----------------------------");
                            System.out.println("Spotter commands:");
                            System.out.println("spot -> spot <type> <id> ");
                            System.out.println("trail -> trail <type> <id> ");
                            System.out.println("ping -> ping <name>");
                            System.out.println("clear -> clear");
                            System.out.println("init -> init");
                            System.out.println("-----------------------------");

                            break;
                        default:

                            final String type = spotterTokens[1];
                            final String id = spotterTokens[2];


                            if (command.equals("spot")) {

                                String t = verifyType(type);

                                if (id.contains("*")) {

                                    TrackMatchRequest request = TrackMatchRequest.newBuilder().setType(t).setSubId(id).build();
                                    TrackMatchResponse response = siloFrontend.trackMatchObj(request);
                                    trackMatchResponseToString(response, siloFrontend);

                                } else {

                                    TrackRequest request = TrackRequest.newBuilder().setType(t).setId(id).build();
                                    TrackResponse response = siloFrontend.trackObj(request);
                                    trackResponseToString(response, siloFrontend);
                                }
                            }

                            if (command.equals("trail")) {

                                String t = verifyType(type);

                                TraceRequest request = TraceRequest.newBuilder().setType(t).setId(id).build();
                                TraceResponse response = siloFrontend.traceObj(request);
                                traceResponseToString(response, siloFrontend);

                            }
                            break;
                    }
                    if (exit) break;
                } catch (StatusRuntimeException e) {

                    //Change server when the previous goes down
                    if (e.getStatus().getCode() == Status.Code.UNAVAILABLE) {
                        System.out.println("Server is down, reconnecting...");
                        siloFrontend = new SiloFrontend(siloFrontend.getHost(), siloFrontend.getPort(), "", siloFrontend.getPrevTS());
                        System.out.println("Reconnected");
                    } else
                        System.out.println(e.getStatus().getDescription());

                }
            }
            System.out.println("> Closing client");
            channel.shutdownNow();

        } catch (IOException e) {
            System.out.println("Caught exception with description: Invalid input");
        }
    }

    //Prints the responses to the trail command
    private static void traceResponseToString(TraceResponse response, SiloFrontend siloFrontend) {

        List<ObservationMessage> observationList = response.getObservationList();
        printResponses(observationList, siloFrontend);
    }

    //Prints the responses to the spot * command
    private static void trackMatchResponseToString(TrackMatchResponse response, SiloFrontend siloFrontend) {

        List<ObservationMessage> observationList = response.getObservationList();
        printResponses(observationList, siloFrontend);
    }

    //Prints the responses to the spot command
    private static void trackResponseToString(TrackResponse response, SiloFrontend siloFrontend) {
        CamInfoRequest request = CamInfoRequest.newBuilder().setCamName(response.getObservation().getCamName()).build();
        CamInfoResponse camResponse = siloFrontend.getCamInfo(request);

        if (response.getObservation().getType().equals("CAR")) {
            System.out.println("car" + "," +
                    response.getObservation().getId() + "," + response.getObservation().getDatetime() +
                    "," + response.getObservation().getCamName() + "," + camResponse.getLatitude() + "," +
                    camResponse.getLongitude());
        } else if (response.getObservation().getType().equals("PERSON")) {
            System.out.println("person" + "," +
                    response.getObservation().getId() + "," + response.getObservation().getDatetime() +
                    "," + response.getObservation().getCamName() + "," + camResponse.getLatitude() + "," +
                    camResponse.getLongitude());
        }

    }

    //Auxiliary function to print a list of observations
    private static void printResponses(List<ObservationMessage> observationList, SiloFrontend siloFrontend) {

        for (ObservationMessage om : observationList) {
            if (om.getType().equals("CAR")) {

                CamInfoRequest request = CamInfoRequest.newBuilder().setCamName(om.getCamName()).build();
                CamInfoResponse camResponse = siloFrontend.getCamInfo(request);

                System.out.println("car" + "," +
                        om.getId() + "," + om.getDatetime() + "," + om.getCamName() + "," +
                        camResponse.getLatitude() + "," + camResponse.getLongitude());
            } else if (om.getType().equals("PERSON")) {

                CamInfoRequest request = CamInfoRequest.newBuilder().setCamName(om.getCamName()).build();
                CamInfoResponse camResponse = siloFrontend.getCamInfo(request);

                System.out.println("person" + "," +
                        om.getId() + "," + om.getDatetime() + "," + om.getCamName() + "," +
                        camResponse.getLatitude() + "," + camResponse.getLongitude());
            }
        }
    }

    //Verifies and returns the type of the object
    private static String verifyType(String string) {
        if (string.equals("car")) return "CAR";
        if (string.equals("person")) return "PERSON";
        else return string;
    }

    //Verifies the user's command
    private static boolean checkCommand(String[] args) {
        if (args.length < 3) {

            if (args.length == 1 && (args[0].equals("exit") || args[0].equals("help") ||
                    args[0].equals("init") || args[0].equals("clear"))) return true;

            else if (args.length == 2 && args[0].equals("ping")) return true;

            return false;
        }

        if (args.length > 3) return false;

        if (!(args[0].equals("spot") || args[0].equals("trail"))) return false;

        return true;

    }
}
