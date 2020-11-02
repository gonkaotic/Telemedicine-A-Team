package network.client;

import java.util.ArrayList;
import java.util.List;

import BITalino.*;
import pojos.ECG;

// This class will deal with the connection to the BITalino
public class BitalinoHandler {
	
	//acquisitionChannel[0] is ECG channel whereas acquisitionChannel[1] is the pulseoxymeter
	private String macAddress;
	private int samplingRate;
	private BITalino bitalino;
	private int[] acquisitionChannels;

	// Missing: we should define the optimal sampling frequency among the available
	// ones (10, 100, 1000 hz)
	// Look which channel is the one that corresponds to the ECG and ¿pulseoxymeter?
	public BitalinoHandler(String macAddress) {
		this.macAddress = macAddress;
		bitalino = new BITalino();

	}

// @description: connection with the BITalino 
// @return: true if connection was possible and false if not
	public boolean connect() {
		try {
			bitalino.open(macAddress, samplingRate);
			return true;
		} catch (BITalinoException ex) {
			ex.printStackTrace();
			return false;
		}
	}
// @description: record x ms of ECG
// @return: the recorded ECG
	public ECG recordECG() throws Throwable {
		int[] ecgChannel = {acquisitionChannels[0]};
		bitalino.start(ecgChannel);
		
//		int nSamples = samplingRate * secondsToRecord;
		Frame[] samples = bitalino.read(nSamples);
		
		ArrayList<Float> ecgSamples;
		
		for (int i=0; i<samples.length; i++) {
			 // The BITalino reads ints, we should look up how it converts V into int
			ecgSamples.add((float) samples[i].analog[0]);
		}
		
		bitalino.stop();
		
		ECG ecg = new ECG(ecgSamples);
		return ecg;
	}
}
