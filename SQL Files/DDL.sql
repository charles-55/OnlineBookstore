CREATE TABLE Customer (
	CustomerID	INT NOT NULL,
	Username	VARCHAR(25) UNIQUE NOT NULL,
	Pword		VARCHAR(16) NOT NULL,
	Admin       VARCHAR(3) NOT NULL,
	PRIMARY KEY (CustomerID)
);

CREATE TABLE Publisher (
	Pname			VARCHAR(100) NOT NULL,
	Address			VARCHAR(100),
	Email			VARCHAR(100),
	PhoneNumber		BIGINT,
	BankingAccount	BIGINT NOT NULL,
	PRIMARY KEY (Pname)
);

CREATE TABLE Book (
	ISBN				NUMERIC(13, 0) NOT NULL,
	Bname				VARCHAR(100) NOT NULL,
	AuthorName			VARCHAR(100) NOT NULL,
	ContributingAuthor	VARCHAR(100),
	Publisher			VARCHAR(100) NOT NULL,
	Genre               VARCHAR(15) NOT NULL,
	NumOfPages			INT NOT NULL,
	Price				NUMERIC(5, 2) NOT NULL,
	PubPercent			NUMERIC(3, 2),
	Amount				INT NOT NULL,
	PRIMARY KEY (ISBN),
	FOREIGN KEY (Publisher) REFERENCES Publisher (Pname)
);

CREATE TABLE Basket (
	CustomerID		INT NOT NULL,
	ISBN			NUMERIC(13, 0) NOT NULL,
	Amount			INT NOT NULL,
	PRIMARY KEY (CustomerID, ISBN),
	FOREIGN KEY (CustomerID) REFERENCES Customer (CustomerID),
	FOREIGN KEY (ISBN) REFERENCES Book (ISBN)
);

CREATE TABLE BillingInfo (
    Name        VARCHAR(100) NOT NULL,
    CardNum     NUMERIC(16, 0) NOT NULL,
    Address     VARCHAR(100) NOT NULL,
    PRIMARY KEY (CardNum)
);

CREATE TABLE BookOrder (
	OrderNum		INT NOT NULL UNIQUE,
	CustomerID		INT NOT NULL,
	ISBN			NUMERIC(13, 0) NOT NULL UNIQUE,
	Amount			INT NOT NULL,
	TotalPrice	    NUMERIC(5, 2) NOT NULL,
	BillingInfo		NUMERIC(16, 0) NOT NULL,
	ShippingInfo	VARCHAR(100) NOT NULL,
	PRIMARY KEY (OrderNum, ISBN),
	FOREIGN KEY (CustomerID) REFERENCES Customer (CustomerID),
	FOREIGN KEY (ISBN) REFERENCES Book (ISBN),
	FOREIGN KEY (BillingInfo) REFERENCES BillingInfo (CardNum)
);

CREATE TABLE Tracker (
	TrackerNum		INT NOT NULL,
	CustomerID		INT NOT NULL,
	OrderNum		INT NOT NULL,
	Status			VARCHAR(10),
	PRIMARY KEY (TrackerNum),
	FOREIGN KEY (CustomerID) REFERENCES Customer (CustomerID),
	FOREIGN KEY (OrderNum) REFERENCES BookOrder (OrderNum)
);

CREATE TABLE MonthlySummaryReport (
    MonthOfSale     INT NOT NULL,
    YearOfSale      NUMERIC(4, 0) NOT NULL,
    Revenue         NUMERIC(10, 2) NOT NULL,
    Expenditures    NUMERIC(10, 2) NOT NULL,
    SalesPerGenre   NUMERIC(10, 2) NOT NULL,
    SalesPerAuthor  NUMERIC(10, 2) NOT NULL,
    PRIMARY KEY(MonthOfSale, YearOfSale)
);

CREATE TABLE MonthlyBookSales (
    MonthOfSale     INT NOT NULL,
    YearOfSale      NUMERIC(4, 0) NOT NULL,
    ISBN            NUMERIC(13, 0) NOT NULL,
    Amount          INT NOT NULL,
    PRIMARY KEY (MonthOfSale, YearOfSale, ISBN),
    FOREIGN KEY (ISBN) REFERENCES Book (ISBN)
);