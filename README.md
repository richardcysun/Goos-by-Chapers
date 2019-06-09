# Goos-by-Chapers

In the [sf105](https://github.com/sf105/goos-code), it provides a complete work of the book "Frowing Object-Oriented Software, Guided by Tests". However, while reading this book chapter-by-chapter, it's become quite difficult to understand how this Auction Sniper example goes that far. Therefore, I type in codes of this book on chapter-basis, and it should be more fun to read this awesome book.

Pleaes be noticed that this repository is a kind of personal study note. In addition to original Java codes, some comments are adressed to help me understand what's really happening. Some additional codes are added (and comment-out befoe I commit to Git) to help me observe how this test work in live manner.

## Chapter 11
In this chapter, there is a tricky part confuses me a while. How come the FakeAuctionServer announces auction close by sending empty message in Chat?
While Main receiving a message (empty or not, no matter) from Chat, it always put "Lost" on the Label. The actual implements will be filled in following chapters. 

## Chapter 12
It seems some constants are not explicitly mentions in the Java examples. Here are some of them.

```java
// ApplicationRunner.java
public static final String SNIPER_XMPP_ID = "sniper@localhost/Auction";

// Main.java
public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
```
