
import java.io.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;
import java.security.KeyStore;


/*
 * This example shows how to set up a key manager to perform client
 * authentication.
 *
 * This program assumes that the client is not inside a firewall.
 * The application can be modified to connect to a server outside
 * the firewall by following SSLSocketClientWithTunneling.java.
 */
public class client {

    public static void main(String[] args) throws Exception {
    	//host och port hårdkodat för enkelhet
        String host = "localhost";
        int port = 8080;
        String user;
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        while(true){
        	//Läser in användarnamn och lösenord
	        System.out.println("User/ID :");
	        System.out.print(">");
	        user = read.readLine();	        
	        char[] password;
	        password = System.console().readPassword("password >");
	
	        try { /* set up a key manager for client authentication */
	            SSLSocketFactory factory = null;
	            try {
	                KeyStore ks = KeyStore.getInstance("JKS");
	                KeyStore ts = KeyStore.getInstance("JKS");
	                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
	                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
	                SSLContext ctx = SSLContext.getInstance("TLS");
	                ks.load(new FileInputStream(user.toString()+ "-store"), password);  // keystore password (storepass)
					ts.load(new FileInputStream("clienttruststore"), "CLIENTpassTRUSTwordSTORE".toCharArray()); // truststore password (storepass);
					kmf.init(ks, password); // user password (keypass)
					tmf.init(ts); // keystore can be used as truststore here
					ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
	                factory = ctx.getSocketFactory();
	            } catch (Exception e) {
	                throw new IOException(e.getMessage());
	            }
	            SSLSocket socket = (SSLSocket)factory.createSocket(host, port);
	            System.out.println("\nsocket before handshake:\n" + socket + "\n");
	
	            /*
	             * send http request
	             *
	             * See SSLSocketClient.java for more information about why
	             * there is a forced handshake here when using PrintWriters.
	             */
	            socket.startHandshake();
	            
	            //Hämtar användarens namn från certifikatet
	            SSLSession session = socket.getSession();
	            X509Certificate cert = (X509Certificate)session.getPeerCertificateChain()[0];
	            String subject = cert.getSubjectDN().getName();
	            System.out.println("certificate name (subject DN field) on certificate received from server:\n" + subject + "\n");
	            System.out.println("socket after handshake:\n" + socket + "\n");
	            System.out.println("secure connection established\n\n");
	            
	            //Kontrollerar att signaturen på certifikatet stämmer
	            KeyStore ts = KeyStore.getInstance("JKS");
	            ts.load(new FileInputStream("clienttruststore"), "CLIENTpassTRUSTwordSTORE".toCharArray()); // truststore password (storepass);
	            cert.verify(ts.getCertificate("CAclient").getPublicKey());
	            
	             //Skapar reader/writer som läser och skriver mot socketen          
	            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	            String msg;
	            String pmsg;
	            
	            //läser in data och skriver ut data i konsolen till vi får in en *.
	            // Vid * då förväntar vi oss input från användaren som skickas till servern
	            // input=quit loggar ut användaren
				for (;;) {
					while(true){
						String tmp = in.readLine();
						if(tmp.endsWith("*")){
							msg = tmp.substring(0, tmp.length()-1);
							System.out.println(msg);
							break;
						}
						System.out.println(tmp);
					}
	                System.out.print(">");
	                pmsg = read.readLine();
	                if (pmsg.equalsIgnoreCase("quit")) {
	                	 out.println("q");
					    break;
					}
	                System.out.print("sending '" + pmsg + "' to server...");
	                out.println(pmsg);
	                out.flush();
	                System.out.println("done");
	
	                
	            }
	            in.close();
				out.close();
				read.close();
	            socket.close();
	        } catch (Exception e) {
	           System.out.println("Invalid login information");
	        }
        }
    }

}
