# Goos-by-Chapers

In the [sf105](https://github.com/sf105/goos-code), it provides a complete work of the book "Growing Object-Oriented Software, Guided by Tests". However, while reading this book chapter-by-chapter, it's become quite difficult to understand how this Auction Sniper example goes that far. Therefore, I type in codes of this book on chapter-basis, and it should be more fun to read this awesome book.

Pleaes be noticed that this repository is a kind of personal study note. In addition to original Java codes, some comments are adressed to help me understand what's really happening. Some additional codes are added (and comment-out befoe I commit to Git) to help me observe how this test work in live manner.

### Before you dig in the source codes in this Git repository...
Please be noticed that the authors don't write every single line of code in the book. From Chapter 11 to 15, maybe 95% of codes are mentioned in the book, and it's not difficult to fill in the missing pieces by myself. However, since Chapter 16, it seems authors are expecting reader to be getting skillful to AuctionSniper. Sometimes, authors only mention what parts need to be done, but they don't write down exact codes in the book. 

Therefore, I have to fill in the gaps either by myself or finding from [sf105](https://github.com/sf105/goos-code).
Please alse be reminded that source codes in this Git repository is mostly handcraft, and the final work of Chapter 19 is quite different from [sf105](https://github.com/sf105/goos-code).

## Chapter 11 Passing the First Test
**TO DO: Single item-join, lose without bidding.**

In this chapter, authors simply create a walking skeleton. Unexpectedly, the efforts of building up a skeleton is larger than initial expectations. The production codes of walking skeleton only has two classes, the **Main** (very initial business logics) and **MainWindow** (very rough GUI). Inside **Main**, a very simple implementation for interface **MessageListener** handles (listens to) messages from **FakeAuctionServer**.

While **Main** receiving a message (empty or not, no matter) from **Chat**, it always put "Lost" on the Label GUI. Anyway, this is simply a startup, the actual implementations will be filled in following chapters. 

### Class Diagram of End-to-End Test
![image](https://github.com/richardcysun/Goos-by-Chapers/blob/master/Ch11/auction-sniper-ch11/test/end-to-end/test/endtoend/auctionsniper/Ch11_e2eTest_ClassDiagram.jpg)

## Chapter 12 Getting Ready to Bid
**TO DO: Single item-join, bid & lose. (part I)**

This chapter is going to make the walking skeleton more formal. In Chapter 11, the implementation of interface **MessageListener** is enriched and handled by class **AuctionMessageTranslator**.

To chase the spirit of TDD, an interface **AuctionEventListener** is introduced. In the unit test **AuctionMessageTranslatorTest**, the **AuctionEventListener** is a seam. JMock impersonates itself as **MessageListener**, and send fake messages to test  **AuctionMessageTranslator**. If **AuctionMessageTranslator** works as expected, it should call event receiver **auctionClosed** or **currentPrice** to **AuctionEventListener** (the JMock).

One end-to-end test (**sniperMakesAHigherBidButLoses**) and two unit tests (**notifiesAuctionClosedWhenCloseMessageReceived** and **notifiesBidDetailWhenCurrentPriceMessageReceived**) are forged for future production codes development.

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

## Chapter 13 The Sniper Makes a Bid
**TO DO: Single item-join, bid & lose. (part II)**

Compared to Chapter 12, ther are some highlights.
1. For better "Role and Responsibility" discipline, nested classes **SniperStateDisplayer** (displays wordings on UI) and **XMPPAuction** (communicates to auction server) are created.
2. Core business logics are moved to the new class **AuctionSniper** from class **Main**. The **AuctionSniper** implements **AuctionEventListener**, and it processes auction events from auction server (which translated by **AuctionMessageTranslator**).
3. **AuctionSniper** owns two new members, **SniperListener** (implemented by **SniperStateDisplayer**) is responsible for message display and **Auction** (implemented by **XMPPAuction**) is responsble for communication with Auction Server.
4. Finally, another nested class **AuctionEvent** is given birth because it can make outer class **AuctionMessageTranslator** more neat and clean. The **AuctionEvent** is a parser to decompose message of "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;".

In the term of unit test, the **AuctionSniperTest** tests **AuctionSniper** with interfaces **Auction** and **SniperListener**. They both are good seams to insert mockup.

### Class Diagram of Source Codes
![image](https://github.com/richardcysun/Goos-by-Chapers/blob/master/Ch13/auction-sniper-ch13/src/auctionsniper/Ch13_ClassDiagram.jpg)

## Chapter 14 The Sniper Wins the Auction
**TO DO: Single item-join, bid & win.**

In this chapter, the auction sniper wins for the very first time.

Compared to Chapter 13, it doesn't change production codes in large scale. In stead, this chapter mainly focuses on the elaobration of test scenes about how to win and how to bid.

Here are some highlights.
1. An enumeration **PriceSource** is created to distiguish sniper and other bidder.
2. **AuctionMessageTranslator** collaborates with **AuctionEvent** parser to see who is bidding at what price.
3. **AuctionSniper**(implements **AuctionEventListener**) take actions (winning or bidding) according to **SniperState**.
4. If currentPrice() receives a price from other bidders, then bids with higher price (price+increment), this is the bidding state.
5. If currentPrice() receives a price from sniper, the state is winning.
6. At the moment of auction closed, if the state is bidding, the sniper loses auction. If the state is winning, the sniper wins.

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
### Class Diagram of Source Codes
![image](https://github.com/richardcysun/Goos-by-Chapers/blob/master/Ch14/auction-sniper-ch14/src/auctionsniper/Ch14_ClassDiagram.jpg)

## Chapter 15 Towards a Real User Interface
**TO DO: Single item-show price details.**

In this chapter, JLabel is replaced by JTable for better data visibility.
With the table with details, the **SniperSnapshot** is designed to carry more details bidding information, such as item ID, latest price (of everyone) and latest bid (of sniper).

In previous chapter, bidding data are passing in plaintext-style parameters. But since chapter 15, bidding data (or object) in wrapped into **SniperSnapshot**. Therefore, the entire project of **AuctionSniper** is reformed to embrace **SniperSnapshot**.

### Class Diagram of Source Codes
![image](https://github.com/richardcysun/Goos-by-Chapers/blob/master/Ch15/auction-sniper-ch15/src/auctionsniper/Ch15_ClassDiagram.jpg)

## Chapter 16 Sniping for Multiple Items
**TO DO: Multiple Items.**

This chapter is getting quite nasty because authors only demonstrate primary implementations. For those related or surrounding codes, I have to dig them out from [sf105](https://github.com/sf105/goos-code), such as class Announcer.

Since the authors intend to put multiple items on auction house, the first thing they do is to elaborate an integration test to ensure MainWindow can accompish a "Join Auction" work. Meanwhile, more unit tests for MainWindow are created because GUI experieces are enriched.

Overall, the core of this chapter is create better user experieces and start up with more tests on UI.

### Class Diagram of Source Codes
![image](https://github.com/richardcysun/Goos-by-Chapers/blob/master/Ch16/auction-sniper-ch16/src/auctionsniper/Ch16_ClassDiagram.jpg)

## Chapter 17 Testing Apart Main
**TO DO: Add new items through the GUI.**

For the perspective of code development, this chapter is very difficult because most codes are not in the book. Readers must fill in the gap on their own, [sf105](https://github.com/sf105/goos-code) is a good reference, but readers still have to tweak them because sf105 is the final work of this book.

The most important value of this chapter is source code refactoring. In the Chapter 16, class Main tightly manages XMPP connection, Chat room listener and GUI messages. In this chapter, class XMPPAuctionHouse manages XMPP connection, class XMPPAuction handles Chat room, and class SnipersTableModel output messages to GUI.

Authors call above improvement as "incremental Architecture" by "Three-Point Contact" (三點不動一點動的攀岩技巧) skill.

### Class Diagram of Source Codes
![image](https://github.com/richardcysun/Goos-by-Chapers/blob/master/Ch17/auction-sniper-ch17/src/auctionsniper/Ch17_ClassDiagram.jpg)

## Chapter 18 Filling in Details
**TO DO: Stop bidding at stop price.**

In case the bidding price raising unlimitedly (or you can say in case of bankruptcy), a stop price is introduced in this chapter.
In previous chapters, item identifiers are kept in a String. In this chapter, class Item holds both identifier and stop price.
Once the last price from other bidder is higher than stop price, Auction Sniper judges this auction has lost.

### Class Diagram of Source Codes
![image](https://github.com/richardcysun/Goos-by-Chapers/blob/master/Ch18/auction-sniper-ch18/src/auctionsniper/Ch18_ClassDiagram.jpg)

## Chapter 19 Handling Failure
**TO DO: Translator - invalid message from Auction.**

"What if it doesn't work?" is the key idea of this chapter. Based on this question mark, authors creates some test cases to drill the failure scenarios. In addtion, since there are failures occur, logging the failures is becoming an inevtible task. To achieve this task, authors plot a failure report to catch and log XMPP failures.

### Class Diagram of Source Codes
![image](https://github.com/richardcysun/Goos-by-Chapers/blob/master/Ch19/auction-sniper-ch19/src/auctionsniper/Ch19_ClassDiagram.jpg)
