package socketclient;

/**
 * Group 8 Project 1 CNT 4504 Client Application Stations used:
 * Client:192.168.100.115 Server: 192.168.100.116
 */
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class SocketClient implements Runnable {

    private static String serverHost;
    private static int pid = 6544;
    private static int select = 0;
    private static Socket netSocket = null;
    private static PrintWriter out = null;
    private static BufferedReader in = null;
    private static long total;
    public static void main(String[] args) throws IOException,InterruptedException {

        //String serverHost;
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter server hostname: ");
        serverHost = scan.nextLine();
        //int pid = 6544;
        System.out.println("Enter the number of Clients");
        int numOfClients = scan.nextInt();
        if (args.length > 0) {
            serverHost = args[0];
        }
        System.out.println("Attemping to connect to host "
                + serverHost + " on port " + pid + ".");

        try {
            netSocket = new Socket(serverHost, pid);
            out = new PrintWriter(netSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(netSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Cannot connect to host: " + serverHost);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Could not get I/O for "
                    + "the connection to: " + serverHost);
            System.exit(1);
        }

        do {
            select = displayMenu();
            long avg;
            if (select >= 1 && select < 7) {

                Thread clients[] = new Thread[numOfClients]; //creates the client threads array
                //delcares the client threads
                for (int i = 0; i < numOfClients; i++) {
                    clients[i] = new Thread(new SocketClient());
                }
                //starts the threads
                for (int x = 0; x < numOfClients; x++) {
                    clients[x].start();
                    clients[x].join();
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {}
                avg = total / numOfClients;
               System.out.println("Response time: " + avg + " ms");
               avg = 0;
               total = 0;
               select=0;
            }//end if check
            

        } while (select != 7);
        
    }

    public static void command(int select) throws IOException {
        long start = System.currentTimeMillis();
        out.println(select);
        String line = in.readLine();
        long response;
        
        while (line != null) {

            System.out.print(line + "\n");
            line = in.readLine();
            
        }
        out.close();
        netSocket.close();
        long end = System.currentTimeMillis();
        response = end - start;
        
        total = response + total;
    }// end command()

    public static int displayMenu() throws IOException {

        boolean x = true;
        Scanner scan = new Scanner(System.in);
        String userInput = "";
        do {
            System.out.println("Enter a command: ");
            System.out.println("1.  Host current Date and Time");
            System.out.println("2.  Host uptime");
            System.out.println("3.  Host memory use");
            System.out.println("4.  Host Netstat");
            System.out.println("5.  Host current users");
            System.out.println("6.  Host running processes");
            System.out.println("7.  Quit");

            try {
                userInput = scan.next();
                select = Integer.parseInt(userInput);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number between 1-7");
                x = false;
            }
            if (select > 7 || select <= 0) {
                System.out.println("Pick a number between 1 and 7.");
                x = false;
            }
        } while (!x);
        return select;

    }// end displayMenu()

    @Override
    public void run() {
        try {
            command(select);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }//end run()
}//end SocketClient class
