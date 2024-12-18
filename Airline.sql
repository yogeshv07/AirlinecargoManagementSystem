CREATE DATABASE login_system;

USE login_system;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    role ENUM('user', 'employee') NOT NULL
);

-- Insert sample data
INSERT INTO users (username, password, role) VALUES 
('user1', 'password1', 'user'),
('employee1', 'password2', 'employee');


-- Create the AirCargo table
CREATE TABLE AirCargo (
    TrackingID VARCHAR(20) PRIMARY KEY, -- Unique identifier for tracking cargo
    SenderName VARCHAR(100) NOT NULL, -- Name of the sender
    SenderAddress VARCHAR(255) NOT NULL, -- Address of the sender
    SenderPhone VARCHAR(15) NOT NULL, -- Phone number of the sender
    ReceiverName VARCHAR(100) NOT NULL, -- Name of the receiver
    ReceiverAddress VARCHAR(255) NOT NULL, -- Address of the receiver
    ReceiverPhone VARCHAR(15) NOT NULL, -- Phone number of the receiver
    CargoType VARCHAR(50) NOT NULL, -- Type of cargo (e.g., Hazardous, Electronics)
    Weight FLOAT NOT NULL, -- Weight of the cargo in kg
    Charge FLOAT NOT NULL, -- Shipping charge in Rs
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Timestamp when the cargo was created
    Status VARCHAR(20) DEFAULT 'Pending' -- Status of the cargo (e.g., Pending, Delivered)
);







CREATE TABLE flights (
    flight_id VARCHAR(10) PRIMARY KEY,       -- Flight ID, formatted as "F-1", "F-2", etc.
    airline VARCHAR(100) NOT NULL,            -- Airline name
    departure VARCHAR(100) NOT NULL,          -- Departure airport
    arrival VARCHAR(100) NOT NULL,            -- Arrival airport
    scheduled_departure DATETIME NOT NULL,    -- Scheduled departure date and time
    scheduled_arrival DATETIME NOT NULL,      -- Scheduled arrival date and time
    actual_departure DATETIME,                -- Actual departure date and time (nullable)
    actual_arrival DATETIME,                  -- Actual arrival date and time (nullable)
    status VARCHAR(50) NOT NULL               -- Flight status (On Time, Delayed, etc.)
);



CREATE TABLE Payments (
    PaymentID INT PRIMARY KEY AUTO_INCREMENT,
    TrackingID VARCHAR(20),
    Amount FLOAT NOT NULL,
    PaymentDate DATETIME,
    CardNumber VARCHAR(20),
    ExpiryDate VARCHAR(7),
    CVV VARCHAR(4),
    CardHolderName VARCHAR(100),
    IsPaid BOOLEAN,
    FOREIGN KEY (TrackingID) REFERENCES AirCargo(TrackingID)
);

CREATE TABLE CargoToFlight (
    id INT AUTO_INCREMENT PRIMARY KEY,
    TrackingID VARCHAR(20),
    flight_id VARCHAR(10),
    AssignmentCode VARCHAR(20) UNIQUE,
    assignment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (TrackingID) REFERENCES AirCargo(TrackingID),
    FOREIGN KEY (flight_id) REFERENCES Flights(flight_id)
);

