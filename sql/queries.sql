--Modifying Data
--Q1
INSERT INTO cd.facilities
VALUES(9,'Spa',20,30,100000,800);
--Q2
INSERT INTO cd.facilities
(facid, name, membercost, guestcost, initialoutlay, monthlymaintenance)
SELECT (SELECT max(facid) FROM cd.facilities)+1, 'Spa', 20, 30, 100000, 800;
--Q3
UPDATE cd.facilities
	SET initialoutlay = 10000
	WHERE facid = 1
--Q4
UPDATE cd.facilities facs
    SET
        membercost = (SELECT membercost * 1.1 FROM cd.facilities WHERE facid = 0),
        guestcost = (SELECT guestcost * 1.1 FROM cd.facilities WHERE facid = 0)
    WHERE facs.facid = 1;
--Q5
DELETE FROM cd.bookings;
--Q6
DELETE FROM cd.members
WHERE memid = 37;
--Basics
--Q7
SELECT facid, name, membercost, monthlymaintenance
	FROM cd.facilities
	WHERE membercost>0 and
		 (membercost < monthlymaintenance/50.0);
--Q8
SELECT * from cd.facilities 
	WHERE name LIKE '%Tennis%';
--Q9
SELECT * FROM cd.facilities
	WHERE facid IN (1,5);
--Q10
SELECT memid, surname, firstname, joindate
	FROM cd.members
	WHERE joindate >= '2012-09-01'; 
--Q11
SELECT surname FROM cd.members
UNION
SELECT name FROM cd.facilities;
--Join
--Q12
SELECT starttime FROM cd.bookings bks
	JOIN cd.members person
	ON person.memid = bks.memid
WHERE 	person.firstname='David' 
AND     person.surname='Farrell';
--Q13
SELECT starttime AS start, name
FROM cd.bookings bks
	JOIN cd.facilities fc
	ON bks.facid = fc.facid
WHERE fc.name LIKE 'Tennis%'
AND bks.starttime >= '2012-09-21'
AND bks.starttime < '2012-09-22'
ORDER BY bks.starttime ASC;
--Q14
SELECT person.firstname AS memfname, person.surname AS memsname,
	   rec.firstname AS recfname, rec.surname AS recsname
	   FROM cd.members person
	   LEFT JOIN cd.members rec
	        ON rec.memid = person.recommendedby
ORDER BY memsname, memfname;
--Q15
SELECT DISTINCT recs.firstname AS firstname,
				recs.surname AS surname
FROM cd.members mem
JOIN cd.members recs
ON recs.memid = mem.recommendedby
ORDER BY surname, firstname;
--Q16
SELECT DISTINCT 
    mems.firstname || ' ' || mems.surname AS member,
    (
        SELECT recs.firstname || ' ' || recs.surname AS recommender
        FROM cd.members recs
        WHERE recs.memid = mems.recommendedby
    )
FROM 
    cd.members mems
ORDER BY member;
--Aggregation
--Q17
SELECT recommendedby, COUNT(*)
FROM cd.members
WHERE recommendedby is not null
GROUP BY recommendedby
ORDER BY recommendedby;
--Q18
SELECT facid, SUM(slots)AS "Total Slots"
FROM cd.bookings
GROUP BY facid
ORDER BY facid;
--Q19
SELECT facid, SUM(slots) AS "Total Slots"
FROM cd.bookings
WHERE starttime >= '2012-09-01'
AND starttime < '2012-10-01'
GROUP BY facid
ORDER BY SUM(slots);
--Q20
SELECT facid, EXTRACT(month from starttime)AS month, SUM(slots) as "Total Slots"
FROM cd.bookings
WHERE EXTRACT(year from starttime) = 2012
GROUP BY facid, month
ORDER BY facid, month;
--Q21
SELECT COUNT(DISTINCT memid) from cd.bookings;
--Q22
SELECT mem.surname, mem.firstname, mem.memid, MIN(bk.starttime)AS starttime
FROM cd.bookings bk
JOIN cd.members mem
ON bk.memid = mem.memid
WHERE starttime>= '2012-09-01'
GROUP BY mem.memid
ORDER BY mem.memid;
--Q23
SELECT (SELECT COUNT(*)FROM cd.members)AS count, firstname, surname
FROM cd.members
ORDER BY joindate;
--Q24
SELECT row_number() OVER(ORDER BY joindate), firstname, surname
FROM cd.members
ORDER BY joindate;
--Q25
SELECT facid, total
FROM (
    SELECT 
        facid,
        SUM(slots) AS total,
        RANK() OVER (ORDER BY SUM(slots) DESC) AS rank
    FROM cd.bookings
    GROUP BY facid
) AS ranked
WHERE rank = 1;
--String
--Q26
SELECT surname || ', '|| firstname as name
FROM cd.members;
--Q27
SELECT memid, telephone
FROM cd.members
WHERE telephone ~ '[()]';
--Q28
SELECT UPPER(SUBSTR(surname, 1, 1)) AS letter, COUNT(*) 
FROM cd.members
GROUP BY letter
ORDER BY letter;
