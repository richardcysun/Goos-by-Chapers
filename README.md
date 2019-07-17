# Goos-by-Chapers

In the [sf105](https://github.com/sf105/goos-code), it provides a complete work of the book "Growing Object-Oriented Software, Guided by Tests". However, while reading this book chapter-by-chapter, it's become quite difficult to understand how this Auction Sniper example goes that far. Therefore, I type in codes of this book on chapter-basis, and it should be more fun to read this awesome book.

Pleaes be noticed that this repository is a kind of personal study note. In addition to original Java codes, some comments are adressed to help me understand what's really happening. Some additional codes are added (and comment-out befoe I commit to Git) to help me observe how this test work in live manner.

## Chapter 11
**TO DO: Single item-join, lose without bidding.**

In this chapter, authors simply create a walking skeleton. Unexpectedly, the efforts of building up a skeleton is larger than initial expectations. The production codes of walking skeleton only has two classes, the **Main**(very initial business logics) and **MainWindow** (very rough GUI). Inside **Main**, a very simple implementation for interface **MessageListener** handles (listens to) messages from **FakeAuctionServer**.

While **Main** receiving a message (empty or not, no matter) from **Chat**, it always put "Lost" on the Label GUI. Anyway, this is simply a startup, the actual implementations will be filled in following chapters. 

### Class Diagram of End-to-End Test
![image](https://github.com/richardcysun/Goos-by-Chapers/blob/master/Ch11/auction-sniper-ch11/test/end-to-end/test/endtoend/auctionsniper/Ch11_e2eTest_ClassDiagram.jpg)

## Chapter 12
**TO DO: Single item-join, bid & lose. (part I)**

This chapter is going to make the walking skeleton more formal. In Chapter 11, the implementation of interface **MessageListener** is enriched and handled by class **AuctionMessageTranslator**.

To chase the spirit of TDD, an interface **AuctionEventListener** is introduced. In the unit test **AuctionMessageTranslatorTest**, the **AuctionEventListener** is the seam. JMock impersonates itself as **MessageListener**, and send fake messages to test  **AuctionMessageTranslator**. If **AuctionMessageTranslator** works fine, it should call event receiver **auctionClosed** or **currentPrice** to **AuctionEventListener** (the JMock).

One end-to-end test (**sniperMakesAHigherBidButLoses**) and two unit tests (**notifiesAuctionClosedWhenCloseMessageReceived** and **notifiesBidDetailWhenCurrentPriceMessageReceived**) are forged for future production codes.

The end-to-end test **sniperMakesAHigherBidButLoses** is not finished in Chapter 11, and it fails the test because auction sniper hasn't bid yet. The related production codes will be completed in Chapter 12.

By the way, in the example coces, it seems some constants are not mentioned in the book so I create below definitions.

```java
// ApplicationRunner.java
public static final String SNIPER_XMPP_ID = "sniper@localhost/Auction";

// Main.java
public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
```
### Class Diagram of Source Codes
![image](https://github.com/richardcysun/Goos-by-Chapers/blob/master/Ch12/auction-sniper-ch12/src/auctionsniper/Ch12_ClassDiagram.jpg)

## Chapter 13
**TO DO: Single item-join, bid & lose. (part II)**

Compared to Chapter 12, ther are some highlights.
1. For better "Role and Responsibility" discipline, nested classes **SniperStateDisplayer** (GUI) and **XMPPAuction** (Communication) are created.
2. Core business logics are moved to the new class **AuctionSniper** from class **Main**. The **AuctionSniper** implements **AuctionEventListener**, and it processes auction events from **FakeAuctionServer**.
3. **AuctionSniper** owns two new members, **SniperListener** (implemented by **SniperStateDisplayer**) is responsible for message display ,and **Auction** (implemented by **XMPPAuction**) is responsble for communication with Auction Server.
4. Finally, another nested class **AuctionEvent** is given birth because it can make outer class **AuctionMessageTranslator** more neat and clean, and of course, much more easy to maintain.

In the term of unit test, the **AuctionSniperTest** tests **AuctionSniper** with interfaces **Auction** and **SniperListener**. They both are good seams to insert mockup.

### Class Diagram of Source Codes
![image](https://github.com/richardcysun/Goos-by-Chapers/blob/master/Ch13/auction-sniper-ch13/src/auctionsniper/Ch13_ClassDiagram.jpg)

## Chapter 14
**TO DO: Single item-join, bid & win.**

In this chapter, the auction sniper wins for the very first time.

Compared to Chapter 13, it doesn't change production codes in large scale. In stead, this chapter main focuses on the elaobration of test scenes.

Here are some highlights.
1. An enumeration **PriceSource** is created to distiguish sniper and other bidder.
2. **AuctionMessageTranslator** can judge who is bidding at what price.
3. **AuctionSniper**(implements **AuctionEventListener**) can judge sniper is winning or not.

Again, some defintions are not mentioned in this chapter.

```java
// AuctionMessageTranslator.java
public AuctionMessageTranslator(String sniperId, AuctionEventListener listener) {
    this.sniperId = sniperId;
    [...]

// SniperListener.java
public interface SniperListener extends EventListener {
   [...]
    void sniperWinning();
}

// ApplicationRunner.java
    public void hasShownSniperIsWinning() {
        driver.showsSniperStatus(MainWindow.STATUS_WINNING);
    }

    public void showsSniperHasWonAuction() {
        driver.showsSniperStatus(MainWindow.STATUS_WON);
    }

```
### Class Diagram of Src
![image](https://github.com/richardcysun/Goos-by-Chapers/blob/master/Ch14/auction-sniper-ch14/src/auctionsniper/Ch14_ClassDiagram.jpg)

## Chapter 15
**TO DO: Single item-show price details.**

### Class Diagram of Source Codes
![image](https://github.com/richardcysun/Goos-by-Chapers/blob/master/Ch15/auction-sniper-ch15/src/auctionsniper/Ch15_ClassDiagram.jpg)

