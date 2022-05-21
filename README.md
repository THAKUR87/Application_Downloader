# Website Downloader

## _Introduction_

- This is a simple console application  which was developed for the purpose of downloading all the resources of the given website to a given direcotry.
- The application will ask for a download location. By default it will take current directory as download location.
- Next the application will ask for Website url. It takes the url and download the index html file of the website as index.html and process the HTML text of that file.
- During processing of index.html file the application will extract set of Absolute Urls. 
- We use the ExecutorService to create several threads and CompletableFuture classes to recursively download above retrieved Urls.

## _How to run Application_

- Clone repo to your local machine from git hub [https://github.com/THAKUR87/Application_Downloader]. Use the master branch.
- Make sure that you have java runtime & MAVEN installed on your machine. JAVA 8 or higher is required.
- Navigate to the project location on your machine and open command prompt.
- build & run the application using command below:

Use the following commands on your system [Windows OS]
 ```sh
 cd {user_home}/website_downloder/
 mvn clean install
 mvn exec:java -Dexec.mainClass=com.sample.app.ApplicationStarter
 ```
- Enter the download location. Press enter to use default current directory location.
- Enter the website URL. Press enter to use default URL.
