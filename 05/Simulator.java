import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Simulator {
	public static void main(String[] args){
		System.out.println("Hi! Welcome to CPU Scheduling Simulator. "
				+ "Please give me required parameters.");
		System.out.println("Scheduling algorithm – (1) FCFS (2) RR (3) SJF (4) SRTN (5) All");
		
		Scanner scanner = new Scanner(System.in);
		String str = scanner.nextLine();
		
		System.out.println("Enter the number of processes");
		
		Scanner sc = new Scanner(System.in);
		String num = scanner.nextLine();
		int n = Integer.parseInt(num);
		
		/************** TEST CASES ***************/
		
		/*
		Process[] p = new Process[5];
		p[0] = new Process(10, 3, 1);
		p[1] = new Process(13, 9, 2);
		p[2] = new Process(6, 11, 3);
		p[3] = new Process(12, 8, 4);
		p[4] = new Process(7, 5, 5);
		*/
		/*
		Process[] p = new Process[3];
		p[0] = new Process(10, 0, 1);
		p[1] = new Process(1, 15, 2);
		p[2] = new Process(5, 18, 3);
		*/
		
		/*****************************************/
		
		Process[] p = new Process[n];
		
		for(int i=0; i<n; i++){
			p[i] = new Process(i);
		}
		
		p = sort(p);
		p = relative(p);
		
		data(p);
		
		switch(str){
		
		case "1" :
			System.out.println("No. of Processes in the system: "+n);
			System.out.println("Wait.... Generating Schedules...");
			fcfs(p);
			break;
		case "2" :
			System.out.println("Please enter quantum size.");
			Scanner s = new Scanner(System.in);
			String t = scanner.nextLine();
			int q = Integer.parseInt(t);
			System.out.println("No. of Processes in the system: "+n);
			System.out.println("Wait.... Generating Schedules...");
			rr(p, q);
			break;
		case "3" :
			System.out.println("No. of Processes in the system: "+n);
			System.out.println("Wait.... Generating Schedules...");
			sjf(p);
			break;
		case "4" :
			System.out.println("No. of Processes in the system: "+n);
			System.out.println("Wait.... Generating Schedules...");
			srtn(p);
			break;
		case "5" :
			Process[] p1 = dup(p);
			Process[] p2 = dup(p);
			Process[] p3 = dup(p);
			Process[] p4 = dup(p);
			System.out.println("No. of Processes in the system: "+n);
			System.out.println("Wait.... Generating Schedules...");
			rr(p1, 5);
			fcfs(p2);
			sjf(p3);
			srtn(p4);
			break;
		default :
            System.out.println("Invalid parameters");
		}
		System.out.println("DONE. Please check output file Output.txt for all the results.");
	}

	public static void fcfs(Process[] p){
		
		double time = 0;
		double idleTime = 0;
		
		for(int i=0; i<p.length; i++){
			if(p[i].arrivalTime > time){
				idleTime += p[i].arrivalTime - time;
				time += p[i].arrivalTime - time;
			}
			p[i].responseTime = time - p[i].arrivalTime;
			time = time + p[i].burst;
			p[i].turnAroundTime = time - p[i].arrivalTime;
			p[i].waitingTime = p[i].turnAroundTime - p[i].burst;
		}
		
		double u = (time - idleTime) / time;
		
		write(p, "FCFS", u);
	}
	
	public static void rr(Process[] p, int quantum){
		
		int count = p.length;
		double time = 0;
		double idleTime = 0;
		boolean justFinished = false;
		int i = 0;

		do{
			
			if(p[i].remainingTime == p[i].burst){
				p[i].responseTime = time - p[i].arrivalTime;
			}
			
			if(p[i].remainingTime<= quantum && p[i].remainingTime>0){
				time = time + p[i].remainingTime;
				p[i].remainingTime = 0;
				justFinished = true;
			}
			else if(p[i].remainingTime>0){
				p[i].remainingTime = p[i].remainingTime - quantum;
				time = time + quantum;
			}

			if(p[i].remainingTime == 0 && justFinished == true){
				p[i].turnAroundTime = time - p[i].arrivalTime;
				p[i].waitingTime = p[i].turnAroundTime - p[i].burst;
				count--;
				justFinished = false;
				
				for(int n=i+1; n<p.length; n++){
					if(p[n].arrivalTime > time){
						idleTime += p[n].arrivalTime - time;
						time += p[n].arrivalTime - time;
						break;
					}
				}
			}
			//Pick next process
			for(int m=0; m<p.length-1; m++){
				if(p[m+1].arrivalTime<=time){
					Process temp = p[m];
					p[m] = p[m+1];
					p[m+1]=temp;
				}
			}
		}while(count!=0);
		
		double u = (time - idleTime) / time;
		
		write(p, "RR", u);
	}
	
	public static void sjf(Process[] p){
		
		int time = 0;
		int x = 1;
		Process temp;
		
		for(int i=0; i<p.length-1; i++){
			time = time + p[i].burst;
			int min = p[x].burst;
			for(int m=x; m<p.length; m++){
				if(p[m].arrivalTime <= time){
					if(p[m].burst < min){
						temp = p[x];
						p[x]=p[m];
						p[m]=temp;
					}
				}
			}
			x++;
		}
		
		double sum = 0;
		double idleTime = 0;
		
		for(int j=0; j<p.length; j++){
			if(p[j].arrivalTime > sum){
				idleTime += p[j].arrivalTime - sum;
				sum += p[j].arrivalTime - sum;
			}
			p[j].responseTime = sum - p[j].arrivalTime;
			sum = sum + p[j].burst;
			p[j].turnAroundTime = sum - p[j].arrivalTime;
			p[j].waitingTime = p[j].turnAroundTime - p[j].burst;
		}
		
		double u = (sum - idleTime) / sum;
		
		//Write into text file
		write(p, "SJF", u);
	}
	
	public static void srtn(Process[] p){
		
		double time = 0;
		double idleTime = 0;
		int min = 0;
		int count = p.length;
		
		do{
			for(int j=0; j<p.length; j++){
				if(p[j].arrivalTime<=time && p[j].remainingTime>0){
					if(p[j].remainingTime <= p[min].remainingTime){
						min = j;
					}
				}
			}
			
			if(p[min].remainingTime == p[min].burst){
				p[min].responseTime = time - p[min].arrivalTime;
			}
			
			p[min].remainingTime--;
			time++;

			if(p[min].remainingTime == 0){
				count--;
				p[min].turnAroundTime = time - p[min].arrivalTime;
				p[min].waitingTime = p[min].turnAroundTime - p[min].burst;
				
				//Pick new value for min
				boolean flag = false;
				//Check if there is another process in queue
				for(int s=0;s<p.length; s++){
					if(p[s].arrivalTime <= time && p[s].remainingTime > 0){
						flag = true;
						min = s;
					}
				}
				if(flag == false){
					//Find min arrival time
					double c = 99999;
					for(int r=0; r<p.length; r++){
						if(p[r].remainingTime > 0 && p[r].arrivalTime < c){
							c = p[r].arrivalTime;
							min = r;
						}
					}
					if(p[min].remainingTime>0){
						idleTime += p[min].arrivalTime - time;
						time += p[min].arrivalTime - time;
					}
				}
			}
			
		}while(count!=0);
		
		double u = (time - idleTime) / time;

		write(p, "SRTN", u);
	}
	
	public static Process[] relative(Process[] p){
		
		for(int m=p.length-1; m>=0; m--){
			p[m].arrivalTime = p[m].arrivalTime - p[0].arrivalTime;
		}
		
		return p;
	}
	
	public static Process[] sort(Process[] p){
		
		Process temp;
		
		for(int j=0; j<p.length; j++){
			for(int k=1; k<p.length; k++){
				if(p[k].arrivalTime < p[k-1].arrivalTime){
					temp = p[k-1];
					p[k-1] = p[k];
					p[k] = temp;
				}
			}
		}
		return p;
	}
	
	public static Process[] dup(Process[] p){
		
		Process[] p2 = new Process[p.length];
		
		for(int i=0; i<p.length; i++){
			p2[i] = new Process(p[i].burst, p[i].arrivalTime, p[i].pid);
		}
		
		return p2;
	}
	
	public static void data(Process[] p){
		
		String fileName = "Data.txt";
		
		try {
	          // Assume default encoding.
	          FileWriter fileWriter =
	              new FileWriter(fileName);

	          // Always wrap FileWriter in BufferedWriter.
	          BufferedWriter bufferedWriter =
	              new BufferedWriter(fileWriter);

	          for(int i=0; i<p.length; i++){
	        	  
	        	  bufferedWriter.write("Process "+p[i].pid);
	        	  bufferedWriter.write(" Arrival time: "+p[i].arrivalTime);
	        	  bufferedWriter.write(" Burst time: "+p[i].burst);
	        	  bufferedWriter.newLine();
	          }

	          // Always close files.
	          bufferedWriter.close();
	      }
	      catch(IOException ex) {
	          System.out.println(
	              "Error writing to file '"
	              + fileName + "'");
	      }
		
	}
	
	public static void write(Process[] p, String algorithm, double u){
		
		  // The name of the file to open.
      String fileName = "Output.txt";

      try {
          // Assume default encoding.
          FileWriter fileWriter =
              new FileWriter(fileName);

          // Always wrap FileWriter in BufferedWriter.
          BufferedWriter bufferedWriter =
              new BufferedWriter(fileWriter);
          
        	  double wsum = 0;
              double tatsum = 0;
              double rtsum = 0;
              //Write into Output.txt
              bufferedWriter.write(algorithm+":");
              bufferedWriter.newLine();
              for(int i=0; i<p.length; i++){
              	bufferedWriter.write("Process "+p[i].pid);
              	bufferedWriter.write(" Waiting time: "+p[i].waitingTime);
              	bufferedWriter.write(" Turn around time: "+p[i].turnAroundTime);
              	bufferedWriter.write(" Response time: "+p[i].responseTime);
              	bufferedWriter.newLine();
              	wsum = wsum + p[i].waitingTime;
              	tatsum = tatsum + p[i].turnAroundTime;
              	rtsum = rtsum + p[i].responseTime;
              }
             bufferedWriter.write("Average waiting time: "+(wsum / p.length));
             bufferedWriter.newLine();
             bufferedWriter.write("Average turn around time: "+(tatsum / p.length));
             bufferedWriter.newLine();
             bufferedWriter.write("Average response time: "+(rtsum / p.length));
             bufferedWriter.newLine();
             bufferedWriter.write("CPU Utilization: "+u);
      
          // Always close files.
          bufferedWriter.close();
      }
      catch(IOException ex) {
          System.out.println(
              "Error writing to file '"
              + fileName + "'");
      }
	}
}


