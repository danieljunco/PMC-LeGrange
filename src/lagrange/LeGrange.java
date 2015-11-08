/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lagrange;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import static java.awt.image.ImageObserver.ERROR;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 *
 * @author DanielJunco
 */
public class LeGrange implements SerialPortEventListener {

    // -----------------------------------------------------------------
    // Atributos de conexion
    // -----------------------------------------------------------------
    
    SerialPort serialPort;
    private static final String PORT_NAMES[] = {"/dev/cu.usbmodem1411", "COM35"};
    private BufferedReader input;
    private OutputStream output;
    
    // -----------------------------------------------------------------
    // Atributos de conexion
    // -----------------------------------------------------------------
    
    private String humedad;
    private String temperatura;
    private String humedadDeLaTierra;
    
    private ArrayList arrayHumedad;
    private ArrayList arrayTemperatura;
    private ArrayList arrayHumedadDeLaTierra;
    
    // -----------------------------------------------------------------
    // Constantes
    // -----------------------------------------------------------------

    private static final int TIME_OUT = 2000;
    private static final int DATA_RATE = 9600;

    // -----------------------------------------------------------------
    // Métodos
    // -----------------------------------------------------------------
    
    public void initialize() {
        humedad = null;
        temperatura = null;
        humedadDeLaTierra = null;
        arrayHumedad = new ArrayList();
        arrayTemperatura = new ArrayList();
        arrayHumedadDeLaTierra = new ArrayList();
        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        //First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();

            for (String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    break;
                }
            }
        }
        if (portId == null) {
            System.out.println("No se encuentra ningun Puerto");
            return;
        }

        try {
            serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);
            serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            // open the streams
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }
    
    public String darHumedad(){
        return humedad;
    }  
    
    public String darTemperatura(){
        return temperatura;
    }
    
    public String darHumedadTierra(){
        return humedadDeLaTierra;
    }
     
    public ArrayList darArregloHumedad(){
        System.out.println(arrayHumedad);
        return arrayHumedad;
    }
    
    public ArrayList darArregloTemperatura(){
        System.out.println(arrayTemperatura);
        return arrayTemperatura;
    }
    
    public ArrayList darArregloHumedadTierra(){
        System.out.println(arrayHumedadDeLaTierra);
        return arrayHumedadDeLaTierra;
    }

    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                while(input.ready()){
                    String datas = input.readLine();
                    String dat[] = datas.split(",");
                    humedad = dat[0];
                    temperatura = dat[1];
                    humedadDeLaTierra = dat[2];
                    arrayHumedad.add(humedad);
                    darArregloHumedad();
                    arrayTemperatura.add(temperatura);
                    darArregloTemperatura();
                    arrayHumedadDeLaTierra.add(humedadDeLaTierra);
                    darArregloHumedadTierra();
                }
                

            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
    // Ignore all the other eventTypes, but you should consider the other ones.
    }
    
    
          
    
    public synchronized void enviarDatos(String datos) throws Exception{
        try {
            output.write(datos.getBytes());
        } catch (Exception e) {
            System.exit(ERROR);
        }
    }
    
     public static void main(String[] args) throws Exception {
        LeGrange main = new LeGrange();
        main.initialize();
        Thread t = new Thread() {
            public void run() {
                //the following line will keep this app alive for 1000    seconds,
                //waiting for events to occur and responding to them    (printing incoming messages to console).
                try {
                    Thread.sleep(1000000);
                } catch (InterruptedException ie) {
                }
            }
        };
        t.start();
        System.out.println("Started");
    }
    
    

    

}
