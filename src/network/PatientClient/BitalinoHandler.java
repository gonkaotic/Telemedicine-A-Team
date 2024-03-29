package network.PatientClient;

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
	boolean connected;

	/**
	 * Constructor only specifying macAddress.
	 * By default acquisitions channels are:
	 * 	- A2: ECG
	 * 	- A3: Heart Rate
	 * 	- A4: O2 Saturation
	 *
	 * @param macAddress
	 */
	public BitalinoHandler(String macAddress) {
		this.macAddress = macAddress;
		bitalino = new BITalino();
		acquisitionChannels= new int[3];
		acquisitionChannels[0]=1; //ECG Channel: A2
		acquisitionChannels[1]=2; //HR Channel: A3
		acquisitionChannels[2]=3; //SpO2 Channel: A4
		samplingRate = 1000;
		connected=false;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public int[] getAcquisitionChannels() {
		return acquisitionChannels;
	}

	public void setAcquisitionChannels(int[] acquisitionChannels) {
		this.acquisitionChannels = acquisitionChannels;
	}

	/**
	 * Connection with the BITalino
	 * 
	 * @return true if connection was possible and false if not
	 */
	public boolean connect() {
		try {
			bitalino.open(macAddress, samplingRate);
			connected=true;
			return true;
		} catch (BITalinoException ex) {
			//ex.printStackTrace();
			return false;
		}
	}

	/**
	 * Disconnect from the BITalino
	 *
	 * @return tru if disconnection was possible and false if there was any problem
	 */
	public boolean disconnect(){
		try{
			bitalino.close();
			connected = false;
			return true;
		} catch (BITalinoException e) {
			return false;
		}
	}
	
	
	/**
	 * Records 5 s of ECG and parses the read value to its true voltage
	 * 
	 * @return the true ECG signal
	 * @throws Throwable
	 * @see {@link #convertECG(int)}
	 */
	public ECG recordECG() throws Throwable {
		int[] ecgChannel = {acquisitionChannels[0]};
		int secondsToRecord = 5;
		if (!connected){
			boolean verification = this.connect();
			if (!verification) throw new Exception("Unable to connect");

		}
		bitalino.start(ecgChannel);

		int nSamples = samplingRate * secondsToRecord;
		Frame[] samples = bitalino.read(nSamples);

		ArrayList<Float> ecgSamples = new ArrayList<Float>();
		ArrayList<Float> ecgTimes = new ArrayList<Float>();

		for (int i=0; i<samples.length; i=i+10) {
			 // The BITalino reads ints therefore we have to convert the measurement to mV
			ecgSamples.add(convertECG(samples[i].analog[0]));
			ecgTimes.add(((1F/(float)samplingRate)*1000F)*i); // sample number*1/Hz*1000 (ms)
		}

		bitalino.stop();

		ECG ecg = new ECG(ecgSamples,ecgTimes);
		return ecg;
	}

	/**
	 * Records the heart rate and the blood oxygenation
	 *
	 * @return an array with the heart rate in BPM first and SpO2(%) second
	 * @throws Throwable
	 * @see {@link #convertPulseOximeter(int[])}
	 */
	public float[] recordPulseOximeter() throws Throwable {
		float[] pulseoximeter;
		int[] pulseOximeterChannels = {acquisitionChannels[1],acquisitionChannels[2]};

		if (!connected){
			boolean verification = this.connect();
			if (!verification) throw new Exception("Unable to connect");

		}
		bitalino.start(pulseOximeterChannels);

		//We will record the first minute and discard this and then record a few
		//more seconds (10s) which will be the calculated measures
		int secondsToRecord = 60;
		int nSamples = samplingRate*secondsToRecord;
		Frame[] samples = bitalino.read(nSamples);

		secondsToRecord=5;
		nSamples=secondsToRecord*samplingRate;
		samples = bitalino.read(nSamples);
		int meanRate = 0;
		int meanSat =0;

		for (int i=0; i<nSamples;i++){
			meanRate=meanRate+samples[i].analog[0];
			meanSat=meanSat+samples[i].analog[1];
		}
		meanRate= meanRate/nSamples;
		meanSat= meanSat/nSamples;

		int[] rawData={meanRate,meanSat};

		pulseoximeter= convertPulseOximeter(rawData);

		return pulseoximeter;
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
		float trueValue= ((float)(rawValue)*VCC/ (float)(Math.pow(2, 10)) - (VCC/2F)) /1100F; //Volts conversion
		trueValue=trueValue*1000; //mV conversion
		return trueValue;
	}

	/**
	 * Conversion of the BITalino ADC reading to BMP and %SpO2
	 *
	 * @param rawData
	 * 		 	the value sent by the BITalino
	 * @return the true BPM and %SpO2 (in that specific order)
	 */
	private float[] convertPulseOximeter(int[] rawData){
		//BPM conversion
		float heartRate = 0.25F*(float)(rawData[0]) - 0.8F;

		//SpO2 conversion
		float saturation= 0.25F*(float)(rawData[1]) - 0.8F;

		float[] pulseOximeter = {heartRate,saturation};
		return pulseOximeter;
	}
}
