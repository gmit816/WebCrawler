import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler_Main {
	// limiting max number of links that can be accessed to 1000
	public static final int MAX_LINKS = 1000;
	public static Queue<String> queue = new LinkedList<>(); //traversal queue    
	public static Set<String> marked = new HashSet<>();		//visited queue
	public static int timer = 10;						
	public static int numThreads = 5;						//number of threads -> one master and rest slave

	public void bfs(String root) throws Exception {
		List<ThreadHandler> tList = new ArrayList();		//thread status list
		
		//creating number of threads
		for (int i = 0; i < numThreads; i++)
			tList.add(new ThreadHandler());
		
		// adding root node into queue
		queue.add(root);
		
		//checking visited queue size against stopping condition
		while (marked.size() <= MAX_LINKS) {
			
			int timer2 = 0;
		
			// if there is nothing to consume then sleep for 200ms
			while (queue.isEmpty() && marked.size() <= MAX_LINKS && timer2 < timer) {
				Thread.sleep(200);
				timer2++;
			}
			
			// fetching first URL from queue
			String currentLink = queue.poll();
			
			//assigning task to each thread
			for (int i = 0; i < numThreads; i++) {
				ThreadHandler threadHandler = tList.get(i);
			
				//checking status of thread; if not busy then continue
				if (!threadHandler.busy) {
					
					//to check if all threads are working
					//System.out.println("Started thread "+(i+1)+" Total links uptil now = "+marked.size());
					
					threadHandler.crawledUrl = currentLink;
					
					//starting master thread
					threadHandler.startThread();
					Thread t1 = new Thread(threadHandler);
					t1.start();
					break;
				}
			}
		}
	}

	public static void main(String[] arg) {
		try {
		WebCrawler_Main wc = new WebCrawler_Main();
			//passing main URL in BFS
			wc.bfs(arg[0]);
			
		} catch (Exception i) {
			i.printStackTrace();
		}
	}

	class ThreadHandler implements Runnable {
		public ReentrantLock lock= new ReentrantLock();
		public boolean busy = false;				//status of thread
		public String crawledUrl;

		public ThreadHandler() {

		}

		public void run() {
			if (marked.size() > MAX_LINKS)
				return;

			try {
				
				//Connecting URL and Jsoup to parse URL
				Document nDocument = Jsoup.connect(crawledUrl).get();
				
				//filter anchor tag from HTML document of URL
				Elements hrefsElements = nDocument.select("a");
				
				// Acquire thread lock
				lock.lock();
				
				// print current crawledUrl
				System.out.println(crawledUrl);
				for (Element elements : hrefsElements) {
					
					//filter absolute href attribute of anchor tag			
					String anchor = elements.attr("abs:href").trim();
					
					// print subURLs of current crawledURL
					System.out.println("  "+anchor);
					
					//check visited queue size against stopping condition
					if (marked.size() <= MAX_LINKS) {
						
						//adding subURL at end of visited queue and traversal queue
						marked.add(anchor);
						queue.add(anchor);
					}
				}
				//release thread lock
				lock.unlock();
				
			} catch (Exception e) {
				stopThread();
			}
			stopThread();
		}
		//thread start
		public void startThread() {
			busy = true;			//thread is busy
		}
		//thread terminate
		public void stopThread() {
			busy = false;			//thread is available
		}
	}
}