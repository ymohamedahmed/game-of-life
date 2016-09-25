public class Tone {
	double freq;
	double dur;
	public Tone(double freq, double dur){
		this.freq = freq;
		this.dur = dur;
		 // frequency
	       

        // number of seconds to play the note
       

        // create the array
        double[] a = tone(freq, dur);

        // play it using standard audio
        StdAudio.play(a);
	}
  // create a pure tone of the given frequency for the given duration
    public static double[] tone(double hz, double duration) { 
        int n = (int) (StdAudio.SAMPLE_RATE * duration);
        double[] a = new double[n+1];
        for (int i = 0; i <= n; i++) {
            a[i] = Math.sin(2 * Math.PI * i * hz / StdAudio.SAMPLE_RATE);
        }
        return a; 
    } 

}
