public class Process {
	
	int pid;
	double arrivalTime;
	int burst;
	int deadline;
	double waitingTime;
	double responseTime;
	double turnAroundTime;
	double remainingTime;
	
	Process(int pid){
		arrivalTime = (int) Math.floor(Math.random() * 21);
		burst = (int) Math.floor(Math.random() * 51);
		deadline = (int) (Math.random() * 51) + burst;
		remainingTime = burst;
		waitingTime = 0;
		turnAroundTime = 0;
		responseTime = 0;
		this.pid = pid;
	}
	
	Process(int bt, double at, int pid){
		arrivalTime = at;
		burst = bt;
		remainingTime = burst;
		this.pid = pid;
		waitingTime = 0;
		turnAroundTime = 0;
		responseTime = 0;
	}

}
