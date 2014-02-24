import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.security.PublicKey;

import javax.net.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;

public class server implements Runnable {
	private static int PRIVATEPERSON = 0;
	private static int NURSE = 1;
	private static int DOCTOR = 2;
	private static int GOVERMENTAGENT = 3;
	
    private ServerSocket serverSocket = null;
    private AuthenticationManager am;

    public server(ServerSocket ss) throws IOException {
        serverSocket = ss;
        newListener();
        am = new AuthenticationManager();
    }

    public void run() {
        try {
        	//v�ntar p� att en klient ska connecta och skapar en ny tr�d f�r ytterligare klienter
        	SSLSocket socket=(SSLSocket)serverSocket.accept();
            newListener();
            
            
            // l�ser av clientens cert och h�mtar namn
            SSLSession session = socket.getSession();          
            X509Certificate cert = (X509Certificate)session.getPeerCertificateChain()[0];
            String subject = cert.getSubjectDN().getName();
           
            //INSERT CHECK OF SIGNATURE OF CA HERE
            //cert.verify(CApublickey);
            
            // skriver ut den nya klientens namn
            System.out.println("client connected");
            System.out.println("client name (cert subject DN field): " + subject);
            
            //skapar en ny reader och writer p� socketen
            PrintWriter out = null;
            BufferedReader in = null;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            int usertype;
            if(subject.length() > 4){
            	usertype = PRIVATEPERSON;
            }
            else if(subject.substring(2).equals("01")){
            	usertype = NURSE;
            }
            else if(subject.substring(2).equals("10")){
            	System.out.println("Doctor");
            	usertype = DOCTOR;
            }
            else{
            	usertype = GOVERMENTAGENT;
            }
            String journal;
            switch(usertype){
            	case 0:	
            		journal = am.getJournal(Integer.parseInt(subject));
            		out.println(journal);          		
            		break;
            	case 1: //Nurse
                	while(true){
                		out.println("Write to journal[w] or read journal[r] ");
                		String temp = in.readLine();
                			if(temp.startsWith("q") || temp.startsWith("Q")){
                				break;
                			}
                			if(temp.equals("r")){
                				int pnr = readPnr(in, out);
                				journal = am.getJournal(pnr, Integer.parseInt(subject), "nurseID"); 		//Var görs rättighetskollen?
                            	out.println(journal);														//I hämtningen från databasen
                            	
                			}else if(temp.equals("w")){  
                				int pnr = readPnr(in, out);
                				out.println("Text to add to journal: ");
                				temp = in.readLine();
                				am.updateJournal(temp, pnr, Integer.parseInt(subject), "nurseID");
                				
                			}
                		}
            		break;
            	case 2: //Doctor
            		//read/write/create?
            		//ask for which pnr doctor wants?
            		//return journal if access else return error
            		while(true){
                		out.println("Create journal[c], write to journal[w] or read journal[r] ");
                		String temp = in.readLine();
                			if(temp.startsWith("q") || temp.startsWith("Q")){
                				break;
                			}
                			if(temp.equals("r")){
                				int pnr = readPnr(in, out);
                				journal = am.getJournal(pnr, Integer.parseInt(subject), "doctorID"); 
                            	out.println(journal);
                            	
                			}else if(temp.equals("w")){  
                				int pnr = readPnr(in, out);
                				out.println("Text to add to journal: ");
                				temp = in.readLine();
                				am.updateJournal(temp, pnr, Integer.parseInt(subject), "doctorID");
                				
                			}else if(temp.equals("c")) {
                				int pnr = readPnr(in, out);
                				int nurseID = readNurseID(in, out);
                				out.println("Text to insert into journal: ");
                				temp = in.readLine();
                				am.createJournal(pnr, Integer.parseInt(subject), nurseID, temp);
                			}
                		}
            		break;
            	case 3: //Govermentagent
            		//read/delete?
            		//ask for which pnr agent wants?
            		//return or delete journal
            		while(true){
                		out.println("Read journal[r] or delete journal[d]");
                		String temp = in.readLine();
                			if(temp.startsWith("q") || temp.startsWith("Q")){
                				break;
                			}
                			if(temp.equals("r")){
                				int pnr = readPnr(in, out);
                				journal = am.getJournal(pnr); 
                            	out.println(journal);
                            	
                			}else if(temp.equals("w")){  
                				int pnr = readPnr(in, out);
                				am.deleteJournal(pnr);	
                			}
                		}
            		break;
            }
            
         
			in.close();
			out.close();
			socket.close();
            System.out.println("client disconnected");
		} catch (IOException e) {
            System.out.println("Client died: " + e.getMessage());
            e.printStackTrace();
            return;
        }
    }
    
    private int readPnr(BufferedReader in, PrintWriter out) {
    	String temp;
    	int pnr = 0;
    	out.println("Insert Personnumber: ");
    	while(true){
	    	try{
	    		temp = in.readLine();
	    		if(temp.startsWith("q") || temp.startsWith("Q")){
    				break;
    			}
	    		if(temp.length() != 10){
    				out.println("Input should be in format: ��MMDDXXXX");
    			}else {
    				try{
					pnr = Integer.parseInt(temp);    												
    				}catch(NumberFormatException nfe)  {  
    					out.println("Input should be in format: ÅÅMMDDXXXX");
    				}
    				return pnr;
    			}
	    	} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
		return pnr;
    }
    
    private int readNurseID(BufferedReader in, PrintWriter out) {
    	String temp = null;
    	int nurseID = 0;
    	out.println("Insert nurse ID to associate with journal: ");
    	while(true){
    		try {
    			temp = in.readLine();
    		} catch (IOException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
	    	if(temp.startsWith("q") || temp.startsWith("Q")){
				break;
			}
			if(temp.length() != 4){
				out.println("Input should be in format: AALS");
			}else {
				try{
					nurseID = Integer.parseInt(temp);    												
				}catch(NumberFormatException nfe)  {  
					out.println("Input should be in format: AALS");
				}
				return nurseID;
			}
    	}
		return nurseID;
    }
    
    private String readText(BufferedReader in, PrintWriter out) {
    	String temp;
    	while(true){
	    	try{
				out.println("Text to insert into journal: ");
	    		temp = in.readLine();
	    		if(temp.startsWith("q") || temp.startsWith("Q")){
    				break;
    			}
	    	}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	}
		return temp;
    }

    private void newListener() { (new Thread(this)).start(); } // calls run()
    public static void main(String args[]) {
        System.out.println("\nServer Started\n");
        int port = -1;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        String type = "TLS";
        try {
            ServerSocketFactory ssf = getServerSocketFactory(type);
            ServerSocket ss = ssf.createServerSocket(port);
            ((SSLServerSocket)ss).setNeedClientAuth(true); // enables client authentication
            new server(ss);
        } catch (IOException e) {
            System.out.println("Unable to start Server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    /**
     * S�tter upp en SSL socket
     * @param type
     * @return
     */
    private static ServerSocketFactory getServerSocketFactory(String type) {
        if (type.equals("TLS")) {
            SSLServerSocketFactory ssf = null;
            try { // set up key manager to perform server authentication
                SSLContext ctx = SSLContext.getInstance("TLS");
                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                KeyStore ks = KeyStore.getInstance("JKS");
				KeyStore ts = KeyStore.getInstance("JKS");
                char[] password = "password".toCharArray();

                ks.load(new FileInputStream("serverkeystore"), password);  // keystore password (storepass)
                ts.load(new FileInputStream("servertruststore"), password); // truststore password (storepass)
                kmf.init(ks, password); // certificate password (keypass)
                tmf.init(ts);  // possible to use keystore as truststore here
                ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
                ssf = ctx.getServerSocketFactory();
                return ssf;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return ServerSocketFactory.getDefault();
        }
        return null;
    }
}