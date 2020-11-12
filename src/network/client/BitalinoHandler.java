package network.client;

import java.util.ArrayList;

import BITalino.BITalino;
import BITalino.BITalinoException;
import BITalino.Frame;
import pojos.ECG;

/**
 * Class to handle all operations with BITalino
 * @author LolaA
 *
 */
public class BitalinoHandler {
	
	//acquisitionChannel[0] is ECG channel whereas acquisitionChannel[1] is the pulseoxymeter
	private String macAddress;
	private int samplingRate;
	private BITalino bitalino;
	private int[] acquisitionChannels;

	// Missing: we should define the optimal sampling frequency among the available
	// ones (10, 100, 1000 hz)
	// Look which channel is the one that corresponds to the ECG and �pulseoxymeter?
	public BitalinoHandler(String macAddress) {
		this.macAddress = macAddress;
		bitalino = new BITalino();

	}
	
	
	/**
	 * Connection with the BITalino
	 * 
	 * @return true if connection was possible and false if not
	 */
	public boolean connect() {
		try {
			bitalino.open(macAddress, samplingRate);
			return true;
		} catch (BITalinoException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * Records 60 s of ECG and parses the read value to its true voltage
	 * 
	 * @return the true ECG signal
	 * @throws Throwable
	 * @see {@link #convertECG(int)}
	 */
	public ECG recordECG() throws Throwable {
		int[] ecgChannel = {acquisitionChannels[0]};
		int secondsToRecord = 60;
		bitalino.start(ecgChannel);
		
		int nSamples = samplingRate * secondsToRecord;
		Frame[] samples = bitalino.read(nSamples);
		
		ArrayList<Float> ecgSamples = new ArrayList<Float>();
		
		for (int i=0; i<samples.length; i++) {
			 // The BITalino reads ints therefore we have to convert the measurement to mV
			ecgSamples.add(convertECG(samples[i].analog[0]));
		}
		
		bitalino.stop();
		
		ECG ecg = new ECG(ecgSamples);
		return ecg;
	} 
	
	 /**
     * Conversion of the value sent by the BITalino to its true voltage
     *
     * @param rawValue
     *          the value read by the BITalino.
     * @return a value ranging between -1.5 and 1.5mV
     */
	private float convertECG (int rawValue) {
		float VCC = 3.3F;
		float trueValue= (rawValue*VCC/ (float)(Math.pow(2, 10)) - (VCC/2F)) /1100F; //Volts conversion
		trueValue=trueValue*1000; //mV conversion
		return trueValue;
	}
}
