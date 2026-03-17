-- Q0: Show table schema
\d+ retail;

-- Q1: Show first 10 rows
SELECT *
FROM retail
LIMIT 10;

-- Q2: Check number of records
SELECT COUNT(*)
FROM retail;

-- Q3: Number of clients (unique customer_id)
SELECT COUNT(DISTINCT customer_id)
FROM retail;

-- Q4: Invoice date range
SELECT 
    MAX(invoice_date) AS max_date,
    MIN(invoice_date) AS min_date
FROM retail;

-- Q5: Number of SKU / products
SELECT COUNT(DISTINCT stock_code)
FROM retail;

-- Q6: Average invoice amount (exclude negative invoices)
SELECT AVG(invoice_amt)
FROM (
    SELECT 
        invoice_no,
        SUM(quantity * unit_price) AS invoice_amt
    FROM retail
    GROUP BY invoice_no
    HAVING SUM(quantity * unit_price) > 0
) t;

-- Q7: Total revenue
SELECT SUM(quantity * unit_price)
FROM retail;

-- Q8: Total revenue by YYYYMM
SELECT
    EXTRACT(YEAR FROM invoice_date) * 100 +
    EXTRACT(MONTH FROM invoice_date) AS yyyymm,
    SUM(quantity * unit_price) AS revenue
FROM retail
GROUP BY yyyymm
ORDER BY yyyymm;

