package space.davidboles.lib.neuralnetwork.old.layers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import space.davidboles.lib.neuralnetwork.old.processors.NetworkProcessor;

public abstract class NetworkLayer extends Thread {
	public static final int INIT_0 = 0;
	public static final int INIT_RANDOM = 1;
	
	private int numInNeurons;
	private int numNeurons;
	public float[][] coefficients;
	private ArrayList<float[]> input = new ArrayList<>();
	private NetworkProcessor processor;
	
	public NetworkLayer(int numInNeurons, int numNeurons, int coefInitType, NetworkProcessor processor) {
		this.numInNeurons = numInNeurons;
		this.numNeurons = numNeurons;
		this.processor = processor;
		
		this.coefficients = new float[this.numNeurons][this.numInNeurons];
		switch(coefInitType) {
		case 1:
			initRandomCoefficients();
			break;
		}
	}
	
	@Override
	public void run() {
		while(!Thread.interrupted()) {
			while(input.size()>0) {
				float[] outdata;
				
				outdata = this.processor.process(input.get(0), this.coefficients);
				
				this.output(outdata);
				input.remove(0);
			}
			
			try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
		}
	}
	
	public abstract void output(float[] data);
	
	//MANAGEMENT
	
	public boolean addInput(float[] input) {
		if(input.length != this.numInNeurons) return false;
		this.input.add(input);
		return true;
	}
	
	public boolean setCoefficients(float[][] coefficients) {
			boolean fine = true;
			fine = coefficients.length == this.numNeurons;
			
			for(int lNeuron = 0; lNeuron < this.numNeurons && fine; lNeuron++) {
				fine = coefficients[lNeuron].length == numInNeurons;
			}
			
			if(fine) {
				this.coefficients = coefficients;
			}
			
			return fine;
	}
	
	public float[][] getCoefficients() {
		synchronized(this.coefficients) {
			return Arrays.copyOf(this.coefficients, this.coefficients.length);
		}
	}
	
	public void initRandomCoefficients() {
		Random random = new Random();
		synchronized (this.coefficients) {
			for(int lNeuron = 0; lNeuron < this.numNeurons; lNeuron++) {
				for(int iNeuron = 0; iNeuron < this.numNeurons; iNeuron++) {
					float rand = random.nextFloat();
					float coef = rand * Float.MAX_VALUE;
					
					boolean pN = random.nextBoolean();
					if (pN) coef = -coef;
					
					coefficients[lNeuron][iNeuron] = coef;
				}
			}
		}
	}
}
