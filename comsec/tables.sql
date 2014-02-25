DROP TABLE IF EXISTS Files;
DROP TABLE IF EXISTS Journals;

CREATE TABLE Files(fileName VARCHAR(200) NOT NULL, fileContent BLOB, PRIMARY KEY (fileName)); 

CREATE TABLE Journals(pnr BIGINT(12) NOT NULL,  doctorID INT(4), nurseID INT(4), 
division INT(2), journal TEXT, PRIMARY KEY (pnr));

-- Test of the SQL queries

INSERT INTO Journals VALUES(191123456789, 2210, 2201, 22, 'blablabla');
SELECT * FROM Journals;
UPDATE Journals SET journal = CONCAT(journal, 'updaterad') WHERE pnr = 191123456789;
SELECT * FROM Journals;
SELECT journal FROM Journals WHERE pnr = 191123456789 AND doctorID = 2211 OR division = 22;
-- DELETE FROM Journals WHERE pnr = 1385246972;
