How to Run:


1. Extract the WebCrawler.zip file into your source folder.
2. In order to run this project you need to install the jsoup library (available in ‘lib’ directory).
3. After installing the jsoup library move to main directory where the WebCrawler.java is located, run following commands:


javac WebCrawler_Main.java
java WebCrawler_Main [URL_To_Be_Crawled]


i.e.
javac WebCrawler_Main.java
java WebCrawler_Main https://www.rescale.com


4. The output is displayed accordingly. First, the input URL (which is by-default https://www.rescale.com ) is output listing down the crawled URLs. 




Assumption:


Number of Threads = 5 (Globally defined)
Stopping Condition = 1000                             // maximum number of URLs that will be crawled