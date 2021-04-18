# PDFViewerFX

[![](https://jitpack.io/v/Dansoftowner/PDFViewerFX.svg)](https://jitpack.io/#Dansoftowner/PDFViewerFX)
[![GitHub last commit](https://img.shields.io/github/last-commit/Dansoftowner/PDFViewerFX)](https://github.com/Dansoftowner/PDFViewerFX/commits/master)
[![GitHub issues](https://img.shields.io/github/issues/Dansoftowner/PDFViewerFX)](https://github.com/Dansoftowner/PDFViewerFX/issues)
[![GitHub](https://img.shields.io/github/license/Dansoftowner/PDFViewerFX)](LICENSE)

#### A library for displaying/viewing pdf documents inside your javaFX application
This library is actually a javaFX wrapper for Mozilla's [PDF.js](https://github.com/mozilla/pdf.js/) javascript library, but with this library you don't have to
deal with any javascript code, so you can just use a java object to view pdf documents in javaFX. 

## How to get it?

Releases are available through [JitPack](https://jitpack.io/#Dansoftowner/PDFViewerFX):

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependency>
    <groupId>com.github.Dansoftowner</groupId>
    <artifactId>PDFViewerFX</artifactId>
    <version>0.8</version>
</dependency>
```  

### Gradle

```groovy
repositories {
    ...
    maven { url 'https://jitpack.io' }
}
```

```groovy
dependencies {
    implementation 'com.github.Dansoftowner:PDFViewerFX:0.8'
}
```

## Usage

First, you have to import the necessary class:
```java
import com.dansoftware.pdfdisplayer.PDFDisplayer;
```

```java
PDFDisplayer displayer = new PDFDisplayer();

primaryStage.setScene(new Scene(displayer.toNode())); // to create the javaFX object from the displayer, you have to use the toNode() function 
primaryStage.show();

/*
 * This method creates a FileInputStream and than wraps it into a
 * BufferedInputStream, and then read the data from it.
*/
displayer.loadPDF(new File("path/to/your/file.pdf"));
```

You can also load the pdf from a java.net.URL object:
```java
/*
 * This method calls the URL.openStream() function to get the InputStream then wraps it into a
 * BufferedInputStream, and then read the data from it.
*/
displayer.loadPDF(new URL("https://www.tutorialspoint.com/jdbc/jdbc_tutorial.pdf"));
```

Or you can read the data from any kind of InputStream:
```java
InputStream inputStream = getAnInputStream(); //just for representation
displayer.loadPDF(inputStream);
```

You can use the constructors as well:
```
PDFDisplayer(URL); //reads the data from the URL
PDFDisplayer(File); //reads the data from the File
PDFDisplayer(InputStream) //reads the data from the InputStream 
```

## More operations, tutorials
If you want to deal with some other useful functions from this library, just go to the [wiki](https://github.com/Dansoftowner/PDFViewerFX/wiki) page! 

## Projects using `PDFViewerFX`
If this library is used by your project, let me know in the `Discussions` and I will mention that in this section.

* [Document Archiver](https://github.com/Document-Archiver/com.sophisticatedapps.archiving.document-archiver) - Archive all your documents in a consistent way, which enables you to retrieve them later fast and easy.

## Screenshots

![alt text](screenshots/1.jpg)

![alt text](screenshots/2.jpg)

![alt text](screenshots/3.jpg)
