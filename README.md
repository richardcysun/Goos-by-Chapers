# Goos-by-Chapers

In the [sf105](https://github.com/sf105/goos-code), it provides a complete work in the book of "Frowing Object-Oriented Software, Guided by Tests". However, while reading this book chapter-by-chapter, it's become quite difficult to understand how this Auction Sniper goes that far.

Therefore, I type in codes in this book on chapter-basis, I think it will be more fun to read this awesome book.
Pleaes be noticed that this repository is a kind a personal study note. In addition to original Java codes, some comments are adressed to help me understand what's really happening. Some additional codes are added (and comment-out) to help me observe how this test work in-live.

## Chapter 11
In this chapter, there is a tricky part confuses me a while. How come the FakeAuctionServer announces auction close by sending empty message in Chat?
While Main receiving a message (empty or not, no matter) from Chat, it always put "Lost" on the Label. The actual implements will be filled in following chapters. 
