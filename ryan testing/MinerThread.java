import java.util.concurrent.atomic.AtomicBoolean;

public class MinerThread extends ConcurrentThread{
	// turns on when block is mined
	private AtomicBoolean minerFlag;

	public MinerThread(SharedData sd, String name, Block block) {
		super(sd,name,block);
	}
	public void run() {	

		if (currentBlock != null)
			currentBlock.startMine(100000);
				

		if (sd.flagA.get()) {
			// do blocks stuffs

		}
		if (sd.flagB.get()) {
			// check if new merkle is buenoo
			// if ()
		}
	}

	public static void main(String[] args) {
		SharedData data = new SharedData("joe mama");
		int n = 8;
		for (int i=0;i<n;i++) {
			MinerThread joe = new MinerThread(data, ""+i,null);

			joe.start();
			System.out.println("jo");
		}
		MinerThread joe = new MinerThread(data, ""+n,null);
		joe.run();
		System.out.println(data);
	}



}
