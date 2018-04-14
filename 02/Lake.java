import java.util.concurrent.Semaphore;

public class Lake {

	public static void main(String[] args){
		
		if(args.length < 2){
			System.out.println("Please enter the number of baits and fishing rods");
			System.exit(0);
		}
		
		int r = Integer.parseInt(args[0]);
		int b = Integer.parseInt(args[1]);
		
		System.out.println("The day has started...");
		
		//Time the program is running in miliseconds
		long temp = System.currentTimeMillis();
		//Array to store average caught fish
		int[] averageCaughtFish = new int[10];
		for(int w=0; w<10; w++){
			averageCaughtFish[w] = 0;
		}
		
		//Run experiment 5 times
		for(int y=0; y<5; y++){
			
			//Create instance of container with the fishing rods and bait
			Container c = new Container(r, b);
			//Start the day 
			Daylight d = new Daylight();
			//Initialize all vacationers
			Vacationer[] v = new Vacationer[10];
			for(int i=0; i<10; i++){
				v[i] = new Vacationer(d, c);
			}
			//Start day
			d.start();
			
			//Initialize all vacationers
			for(int j=0; j<10; j++){
				v[j].start();
			}
			
			//Wait for day to end
			try {
				d.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//Terminate all vacationers
			for(int k=0; k<10; k++){
				v[k].interrupt();
			}
			//Store value of bucket at the end of the day
			for(int z=0; z<10; z++){
				averageCaughtFish[z] += v[z].bucket; 
			}
		}
		
		int sum = 0;
		//Take the average of the values at each index of the array
		for(int q=0; q<10; q++){
			averageCaughtFish[q] = averageCaughtFish[q] / 5;
			sum += averageCaughtFish[q];
		}
		System.out.println(System.currentTimeMillis() - temp);	
		
		for(int p=0; p<10; p++){
			System.out.println("Vacationer_"+p+" caught "+averageCaughtFish[p]+" fish");
		}
		System.out.println("The sum is "+sum);
	}
}

class Vacationer extends Thread{

	Container container;
	Daylight day;
	int bucket;
	
	Vacationer(Daylight day, Container container){
		this.container = container;
		this.day = day;
		bucket = 0;
	}
	
	public void run(){
		
		while(true){
			
			//Take bait out of container
			try {
				container.baits.acquire();
			} catch (InterruptedException e) {
				break;
			}
			
			//Take fishing rod out of container
			try {
				container.fishingRods.acquire();
			} catch (InterruptedException e) {
				break;
			}
			
			//Catch fish for 20 minutes
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
			bucket += (int) Math.floor(Math.random() * 11);
			
			//Release bait
			container.baits.release();
			
			//Wait one minute
			try {
				sleep(50);
			} catch (InterruptedException e) {
				break;
			}
			
			//Release fishing rod
			container.fishingRods.release();
		}
	}
}

class Daylight extends Thread{
	
	public void run(){
		//Wait for 8 hours (480 minutes or 24 * 20 minutes)
		//20 minutes in real life is a second in the program
		try {
			sleep(24000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class Container{
	
	Semaphore fishingRods;
	Semaphore baits;
	
	Container(int r, int b){
		fishingRods = new Semaphore(r, true);
		baits = new Semaphore(b, true);
	}
}