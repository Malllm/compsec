DROP TABLE IF EXISTS Files;
DROP TABLE IF EXISTS Journals;

CREATE TABLE Files(fileName VARCHAR(200) NOT NULL, fileContent BLOB, PRIMARY KEY (fileName)); 

CREATE TABLE Journals(pnr INT(10) NOT NULL,  doctorID INT(4), nurseID INT(4), journal TEXT, PRIMARY KEY (pnr));

-- Test of the SQL queries

INSERT INTO Journals VALUES(1385246972, 6120, 6102, 'blablabla');
SELECT * FROM Journals;
UPDATE Journals SET journal = CONCAT(journal, 'updaterad') WHERE pnr = 1385246972;
SELECT * FROM Journals;
SELECT journal FROM Journals WHERE pnr = 1385246972;
DELETE FROM Journals WHERE pnr = 1385246972;
SELECT * FROM Journals;